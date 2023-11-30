package com.nn.accountapp.configuration;

import feign.RetryableException;
import feign.Retryer;

public class RetryerCustomConfig implements Retryer {

        private final int maxAttempts;
        private final long backoff;
        int attempt;

        public RetryerCustomConfig(long backoff, int maxAttempts) {
            this.backoff = backoff;
            this.maxAttempts = maxAttempts;
            this.attempt = 1;
        }

        @Override
        public void continueOrPropagate(RetryableException retryableException) {
            if (attempt++ >= maxAttempts) {
                throw retryableException;
            }

            try {
                Thread.sleep(backoff);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public Retryer clone() {
            return new RetryerCustomConfig(backoff, maxAttempts);
        }
}
