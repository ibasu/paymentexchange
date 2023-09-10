package au.com.wex.internationalpayments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class PaymentQueryDTO {

    @NotNull
    private String transactionId;

    @NotNull(message = "The converted currency is required.")
    private String convertedCurrencyDesc;

}
