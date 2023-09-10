package au.com.wex.internationalpayments.service;

import au.com.wex.internationalpayments.exception.ServerException;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

public interface IRetryService {

    default RetryBackoffSpec exponentialRetry(int retryAttempts, int delayInSecs) {
        return Retry.backoff(retryAttempts, Duration.ofSeconds(delayInSecs))
                .filter(throwable -> throwable instanceof ServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new ServerException("Treasury Service unresponsive after max retries of " + retryAttempts);
                });
    }
}
