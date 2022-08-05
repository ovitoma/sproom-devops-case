#!/bin/bash

NOW=`date "+%Y%m%d%H%M%S"`

LOG=/var/log/script-execution-$NOW.log
echo "[INFO] ARGS: $@" >> $LOG 2>&1

AZURE_CONNECTION_STRING=`echo $1 | base64 -d`
echo "[INFO] AZURE_CONNECTION_STRING=${AZURE_CONNECTION_STRING}" >> $LOG 2>&1
AZURE_SAS_TOKEN=`echo $2 | base64 -d`
echo "[INFO] AZURE_SAS_TOKEN=${AZURE_SAS_TOKEN}" >> $LOG 2>&1
APP=$3
echo "[INFO] APP=${APP}" >> $LOG 2>&1
USERNAME=$4
echo "[INFO] USERNAME=${USERNAME}" >> $LOG 2>&1

### unzip
echo "[INFO] Install unzip" >> $LOG 2>&1
if [ $(dpkg-query -W -f='${Status}' unzip 2>/dev/null | grep -c "ok installed") -eq 0 ];
then
  apt-get install -y unzip;
fi

### java
JAVA_EXE=/usr/java/openjdk/jdk-18.0.2/bin/java
if [ -f "$JAVA_EXE" ]; then
    echo "[INFO] java already installed.!" >> $LOG 2>&1
else 
    echo "[INFO] Download&install java" >> $LOG 2>&1
    rm -rf /tmp/java
    wget https://download.java.net/java/GA/jdk18.0.2/f6ad4b4450fd4d298113270ec84f30ee/9/GPL/openjdk-18.0.2_linux-x64_bin.tar.gz -P /tmp/java  >> $LOG 2>&1

    mkdir -p /usr/java/openjdk
    cd /usr/java/openjdk
    tar -xzvf /tmp/java/openjdk-18.0.2_linux-x64_bin.tar.gz --directory /usr/java/openjdk >> $LOG 2>&1
    if [ $? -gt 0 ]; then
        echo "[ERROR] Error unpacking java!" >> $LOG 2>&1
        exit 1
    fi
fi

### APP SERVICE
WORKING_DIR="/home/$USERNAME/file-service"

echo "[INFO] Downloading ${AZURE_CONNECTION_STRING}/${APP}${AZURE_SAS_TOKEN}" >> $LOG 2>&1
cd /home/$USERNAME
curl "${AZURE_CONNECTION_STRING}/${APP}${AZURE_SAS_TOKEN}" -o $APP >> $LOG 2>&1

echo "[INFO] Installing application" >> $LOG 2>&1
mv $WORKING_DIR "${WORKING_DIR}_${NOW}" 2> /dev/null

mkdir $WORKING_DIR
unzip /home/$USERNAME/$APP -d $WORKING_DIR

sed -i "s@java@/usr/java/openjdk/jdk-18.0.2/bin/java@g" $WORKING_DIR/start-app.sh
chmod +x $WORKING_DIR/start-app.sh
sed -i "s@path:.*@path: /home/$USERNAME/file-service/dummy-pdf-or-png@g" $WORKING_DIR/config/application.yml
sed -i "s@config:.*@config: config/log4j2.xml@g" $WORKING_DIR/config/application.yml

chown -R $USERNAME:$USERNAME $WORKING_DIR


echo "[INFO] Stop the service (if running)" >> $LOG 2>&1
systemctl stop file-service.service >> $LOG 2>&1

rm -rf /etc/systemd/system/file-service.service
echo "[Unit]
Description=File Service 
After=syslog.target network.target

[Service]
User=$USERNAME
Group=$USERNAME
Type=simple
WorkingDirectory=$WORKING_DIR
ExecStart=/bin/bash $WORKING_DIR/start-app.sh
ExecStop=/bin/kill -WINCH ${MAINPID}
Restart=on-failure

[Install]
WantedBy=multi-user.target" | tee -a /etc/systemd/system/file-service.service >> $LOG 2>&1

if [ $? -gt 0 ]; then
    echo "[ERROR] Service creation failed!" >> $LOG 2>&1
    exit 1
fi

echo "[INFO] Reload daemons" >> $LOG 2>&1
systemctl daemon-reload >> $LOG 2>&1
if [ $? -gt 0 ]; then
    echo "[ERROR] Reload failed!" >> $LOG 2>&1
    exit 1
fi

echo "[INFO] Enable service restart" >> $LOG 2>&1
systemctl enable file-service.service >> $LOG 2>&1
if [ $? -gt 0 ]; then
    echo "[ERROR] Service enablement failed!" >> $LOG 2>&1
    exit 1
fi

echo "[INFO] Starting the service" >> $LOG 2>&1
systemctl start file-service.service >> $LOG 2>&1
if [ $? -gt 0 ]; then
    echo "[ERROR] Service start failed!" >> $LOG 2>&1
    exit 1
fi

echo "[INFO] Ended with succces" >> $LOG 2>&1