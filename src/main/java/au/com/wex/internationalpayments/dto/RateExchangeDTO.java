package au.com.wex.internationalpayments.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class RateExchangeDTO {

    @NotNull
    private String convertedCurrencyCode;

    @NotNull
    private LocalDate rateExchangeDate;
}
