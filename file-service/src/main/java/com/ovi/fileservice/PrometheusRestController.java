package com.ovi.fileservice;

import com.ovi.fileservice.metrics.MicrometerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PrometheusRestController {

    private final MicrometerRegistry micrometerRegistry;

    @Autowired
    public PrometheusRestController(MicrometerRegistry micrometerRegistry) {
        this.micrometerRegistry = micrometerRegistry;
    }

    @GetMapping("/metrics")
    public String getMetrics() {
        MeterRegistry registry = micrometerRegistry.getMeterRegistry();
        if(registry instanceof PrometheusMeterRegistry prometheusMeterRegistry) {
            return prometheusMeterRegistry.scrape(TextFormat.CONTENT_TYPE_OPENMETRICS_100);
        } else {
            throw new NotImplementedException("No Prometheus monitoring is configured!");
        }
    }
}
