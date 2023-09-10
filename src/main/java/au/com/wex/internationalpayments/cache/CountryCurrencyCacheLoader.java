package au.com.wex.internationalpayments.cache;

import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.configuration.PaymentsConfiguration;
import au.com.wex.internationalpayments.service.IRetryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "services.treasury.cache-refresh", havingValue = "true")
public class CountryCurrencyCacheLoader implements InitializingBean, CacheLoader<Cache<Set<String>>>, IRetryService {

    @Qualifier(value = "treasury")
    private WebClient webClient;

    private PaymentsConfiguration paymentsConfiguration;

    private Cache<Set<String>> cache;

    private Fiscaldata fetchAllCountryCurrency() {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(paymentsConfiguration.getTreasuryExchangeRateUrl())
                        .queryParam("fields", "country_currency_desc")
                        .queryParam("page[size]", "500")
                        .build())
                .retrieve()
                .bodyToMono(Fiscaldata.class)
                .retryWhen(exponentialRetry(paymentsConfiguration.getTreasuryExchangeRateUrlRetryAttempts(), paymentsConfiguration.getTreasuryExchangeRateUrlRetryDelayInSecs()))
                .block();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        loadCache();
    }

    @Override
    public Cache<Set<String>> loadCache() {
        Fiscaldata fiscaldata = fetchAllCountryCurrency();
        Set<String> countryCurrencySet = fiscaldata.getData().stream()
                .map(m -> m.getCountryCurrencyDesc().toUpperCase())
                .collect(Collectors.toUnmodifiableSet());

        log.info("Country Currency cache loaded {}", countryCurrencySet);
        cache.addToCache(countryCurrencySet);

        return cache;
    }
}
