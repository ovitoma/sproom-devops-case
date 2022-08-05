package com.ovi.fileservice.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerRegistry {

    private static final PrometheusMeterRegistry prometheusMeterRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

    public MeterRegistry getMeterRegistry() {
        return prometheusMeterRegistry;
    }
}
