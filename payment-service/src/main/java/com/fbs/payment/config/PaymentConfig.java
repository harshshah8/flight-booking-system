package com.fbs.payment.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Payment simulator configuration properties
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment.simulator")
public class PaymentConfig {

    private int successRate = 70;
    private DelayConfig delay = new DelayConfig();
    private FailureConfig failures = new FailureConfig();
    private boolean enableRandomFailures = true;
    private boolean demoMode = false;
    private List<String> demoSuccessEmails;
    private List<String> demoFailureEmails;

    @Data
    public static class DelayConfig {
        private int min = 1;
        private int max = 3;
    }

    @Data
    public static class FailureConfig {
        private int insufficientFundsRate = 15;
        private int cardDeclinedRate = 10;
        private int networkErrorRate = 5;
    }
}