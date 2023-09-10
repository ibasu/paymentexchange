package au.com.wex.internationalpayments.dto;

import au.com.wex.internationalpayments.util.MathUtil;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Builder
@Getter
@EqualsAndHashCode
@ToString
public class CurrencyExchangeDTO {

    private BigDecimal exchangeRate;
    private BigDecimal originalPurchaseAmount;
    private BigDecimal convertedPurchaseAmount;
    private String convertedCountryCurrency;

    public BigDecimal getConvertedPurchaseAmount() {
        return MathUtil.multiplyAndScale(exchangeRate, originalPurchaseAmount, 2);
    }
}
