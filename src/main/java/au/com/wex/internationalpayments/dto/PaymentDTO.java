package au.com.wex.internationalpayments.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class PaymentDTO {

    private final CurrencyEnum originalCurrency = CurrencyEnum.USD;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private String transactionId;
    @Positive(message = "The purchase amount is required and must be greater than 0")
    private BigDecimal purchaseAmount;

    @NotNull(message = "The transaction description is required.")
    private String transactionDescription;

    @NotNull(message = "The transaction description is required.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime transactionDate;

    private String createdUser = "System";
}
