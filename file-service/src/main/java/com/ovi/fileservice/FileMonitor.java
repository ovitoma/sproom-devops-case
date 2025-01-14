package com.ovi.fileservice;

import com.ovi.fileservice.metrics.MicrometerRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Initializes the counters used to monitor file downloads.
 */
@Component
public class FileMonitor {

    private final MicrometerRegistry micrometerRegistry;

    private Counter pdfCounter;
    private Counter invalidPdfCounter;
    private Counter pngCounter;

    private Counter total;

    @Autowired
    public FileMonitor(MicrometerRegistry micrometerRegistry) {
        this.micrometerRegistry = micrometerRegistry;
    }

    @PostConstruct
    private void init() {
        MeterRegistry meterRegistry = micrometerRegistry.getMeterRegistry();
        pdfCounter = meterRegistry.counter("file-service.pdf-count");
        invalidPdfCounter = meterRegistry.counter("file-service.pdf-count-invalid");
        pngCounter = meterRegistry.counter("file-service.png-count");
        total =  meterRegistry.counter("file-service.total");
    }

    Counter getPdfCounter() {
        return pdfCounter;
    }

    Counter getInvalidPdfCounter() {
        return invalidPdfCounter;
    }

    Counter getPngCounter() {
        return pngCounter;
    }

    Counter getTotal() { return total; }
}
