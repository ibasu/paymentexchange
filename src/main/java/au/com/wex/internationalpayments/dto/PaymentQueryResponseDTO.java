package au.com.wex.internationalpayments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Builder
@Data
@EqualsAndHashCode
@ToString
public class PaymentQueryResponseDTO {

    @JsonProperty(value = "paymentTransactionDetails")
    private PaymentDTO paymentDTO;

    @JsonProperty(value = "exchangeRateDetails")
    private CurrencyExchangeDTO currencyExchangeDTO;
}
