package au.com.wex.internationalpayments.service;

import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.dto.RateExchangeDTO;
import au.com.wex.internationalpayments.exception.ServerException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

public interface ITreasuryService extends IRetryService {

    Mono<Fiscaldata> fetchExchangeRate(RateExchangeDTO rateExchangeDTO);

    default RetryBackoffSpec exponentialRetry(int retryAttempts, int delayInSecs) {
        return Retry.backoff(retryAttempts, Duration.ofSeconds(delayInSecs))
                .filter(throwable -> throwable instanceof ServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    throw new ServerException("Treasury Service unresponsive after max retries of " + retryAttempts);
                });
    }
}
