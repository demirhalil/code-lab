package com.dev.driver;

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryListener;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.Retryable;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DriverRetryListener implements RetryListener {

    private static final Logger logger = LoggerFactory.getLogger(DriverRetryListener.class);

    private final AtomicInteger totalRetries = new AtomicInteger(0);
    private final AtomicInteger successfulRecoveries = new AtomicInteger(0);
    private final AtomicInteger finalFailures = new AtomicInteger(0);
    private final ThreadLocal<Integer> currentAttempt = ThreadLocal.withInitial(() -> 0);

    @Override
    public void beforeRetry(final RetryPolicy retryPolicy, final Retryable<?> retryable) {
        int attemptNumber = currentAttempt.get() + 1;
        currentAttempt.set(attemptNumber);
        totalRetries.incrementAndGet();
        logger.info("RetryListener: Attempt: #{} starting for operation '{}'", attemptNumber, retryable.getName());
    }

    @Override
    public void onRetrySuccess(final RetryPolicy retryPolicy, final Retryable<?> retryable, @Nullable final Object result) {
        int attemptCount = currentAttempt.get();
        if (attemptCount > 1) {
            successfulRecoveries.incrementAndGet();
            logger.info("RetryListener: Operation '{}' succeeded after {} attempts", retryable.getName(), attemptCount);
        } else {
            logger.debug("RetryListener: Operation '{}' succeeded on first attempt", retryable.getName());
        }
        currentAttempt.remove();
    }

    @Override
    public void onRetryFailure(final RetryPolicy retryPolicy, final Retryable<?> retryable, final Throwable throwable) {
        int attemptCount = currentAttempt.get();
        finalFailures.incrementAndGet();
        logger.warn("RetryListener: Operation '{}' failed after {} attempts", retryable.getName(), attemptCount);
        currentAttempt.remove();
    }
}
