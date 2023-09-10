package au.com.wex.internationalpayments.exception.handler;

import au.com.wex.internationalpayments.exception.BusinessException;
import au.com.wex.internationalpayments.exception.ServerException;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

public class ReactiveExceptionFilter {

    public static ExchangeFilterFunction errorFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new ServerException(errorBody)));
            } else if (clientResponse.statusCode().is4xxClientError()) {
                return clientResponse.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new BusinessException("", errorBody)));
            } else {
                return Mono.just(clientResponse);
            }
        });
    }
}
