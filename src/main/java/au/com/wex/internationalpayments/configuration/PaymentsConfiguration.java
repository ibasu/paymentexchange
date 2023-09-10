package au.com.wex.internationalpayments.configuration;

import au.com.wex.internationalpayments.exception.BusinessException;
import au.com.wex.internationalpayments.exception.ServerException;
import au.com.wex.internationalpayments.exception.handler.ReactiveExceptionHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static au.com.wex.internationalpayments.exception.handler.ReactiveExceptionFilter.errorFilter;

@Configuration
@Getter
@Slf4j
public class PaymentsConfiguration {

    @Value("${services.treasury.base-url}")
    private String treasuryBaseUrl;

    @Value("${services.treasury.exchange-rate}")
    private String treasuryExchangeRateUrl;

    @Value("${services.treasury.retry-attempts}")
    private int treasuryExchangeRateUrlRetryAttempts;

    @Value("${services.treasury.retry-delay-in-secs}")
    private int treasuryExchangeRateUrlRetryDelayInSecs;

    @Bean(name = "treasury")
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .clientConnector(new ReactorClientHttpConnector())
                .baseUrl(treasuryBaseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filter(logRequest())
                .filter(errorFilter())
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            return Mono.just(clientRequest);
        });
    }

    @Bean
    @Order(-2)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
    public ReactiveExceptionHandler reactiveExceptionHandler(WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        ReactiveExceptionHandler exceptionHandler = new ReactiveExceptionHandler(
                new DefaultErrorAttributes(), webProperties.getResources(), applicationContext, exceptionToStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR
        );
        exceptionHandler.setMessageWriters(configurer.getWriters());
        exceptionHandler.setMessageReaders(configurer.getReaders());
        return exceptionHandler;
    }

    @Bean
    public Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode() {
        return Map.of(
                BusinessException.class, HttpStatus.BAD_REQUEST,
                ServerException.class, HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
