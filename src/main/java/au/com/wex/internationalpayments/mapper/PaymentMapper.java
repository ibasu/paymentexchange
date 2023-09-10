package au.com.wex.internationalpayments.mapper;

import au.com.wex.generated.fiscaldata.pojo.Datum;
import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.dto.CurrencyExchangeDTO;
import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.exception.ExchangeRateNotFoundException;
import au.com.wex.internationalpayments.repository.entity.PaymentEntity;
import au.com.wex.internationalpayments.util.MathUtil;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.function.BiFunction;

public class PaymentMapper {
    public BiFunction<PaymentDTO, Fiscaldata, PaymentQueryResponseDTO> mergeExchangeRate = (paymentDTO, fiscaldata) -> {
        Datum eligibleData = fiscaldata.getData().stream()
                .filter(e -> LocalDate.parse(e.getRecordDate()).compareTo(paymentDTO.getTransactionDate().toLocalDate()) <= 0)
                .sorted(Comparator.comparing(Datum::getRecordDate).reversed())
                .findFirst()
                .orElseThrow(() -> new ExchangeRateNotFoundException("convertedCountryCurrency", "The purchase cannot be converted to the target currency"));

        return PaymentQueryResponseDTO.builder()
                .paymentDTO(paymentDTO)
                .currencyExchangeDTO(CurrencyExchangeDTO.builder()
                        .originalPurchaseAmount(paymentDTO.getPurchaseAmount())
                        .convertedCountryCurrency(eligibleData.getCountryCurrencyDesc())
                        .exchangeRate(MathUtil.convert(eligibleData.getExchangeRate(), 2))
                        .build())
                .build();

    };

    public PaymentEntity mapToModel(@NotNull PaymentDTO paymentDTO) {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setCreatedUser(paymentDTO.getCreatedUser());
        paymentEntity.setLastUpdatedUser(paymentDTO.getCreatedUser());
        paymentEntity.setTransactionAmount(paymentDTO.getPurchaseAmount());
        paymentEntity.setTransactionDescription(paymentDTO.getTransactionDescription());
        paymentEntity.setTransactionOriginalCurrencyCode(paymentDTO.getOriginalCurrency());
        paymentEntity.setTransactionDate(paymentDTO.getTransactionDate());

        return paymentEntity;
    }

    public PaymentDTO mapToDto(@NotNull PaymentEntity paymentEntity) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setCreatedUser(paymentEntity.getCreatedUser());
        paymentDTO.setTransactionDate(paymentEntity.getTransactionDate());
        paymentDTO.setTransactionDescription(paymentEntity.getTransactionDescription());
        paymentDTO.setPurchaseAmount(paymentEntity.getTransactionAmount());
        paymentDTO.setTransactionId(paymentEntity.getId());

        return paymentDTO;
    }
}
