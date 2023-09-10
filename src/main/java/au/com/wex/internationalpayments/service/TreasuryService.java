package au.com.wex.internationalpayments.service;

import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.configuration.PaymentsConfiguration;
import au.com.wex.internationalpayments.dto.RateExchangeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class TreasuryService implements ITreasuryService {

    @Qualifier(value = "treasury")
    private WebClient webClient;
    private PaymentsConfiguration paymentsConfiguration;

    public Mono<Fiscaldata> fetchExchangeRate(RateExchangeDTO rateExchangeDTO) {
        String filter = String.format("record_date:gte:%s,country_currency_desc:in:(%s)", rateExchangeDTO.getRateExchangeDate(), rateExchangeDTO.getConvertedCurrencyCode());
        log.info("Exchange Rate Query Filter: {}", filter);

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(paymentsConfiguration.getTreasuryExchangeRateUrl())
                        .queryParam("filter", filter)
                        .build())
                .retrieve()
                .bodyToMono(Fiscaldata.class)
                .retryWhen(exponentialRetry(paymentsConfiguration.getTreasuryExchangeRateUrlRetryAttempts(), paymentsConfiguration.getTreasuryExchangeRateUrlRetryDelayInSecs()));
    }
}
