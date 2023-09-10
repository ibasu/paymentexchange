package au.com.wex.internationalpayments.mapper;

import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static common.internationalpayments.helper.PaymentDataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentMapperTest {

    PaymentMapper paymentMapper = new PaymentMapper();

    @Test
    void testMergeExchangeRate() {
        PaymentQueryResponseDTO paymentQueryResponseDTO = paymentMapper.mergeExchangeRate.apply(defaultPaymentDTO(), defaultFiscalData());
        assertEquals(paymentQueryResponseDTO.getCurrencyExchangeDTO().getExchangeRate(), new BigDecimal("82.09"));
        assertEquals(paymentQueryResponseDTO.getCurrencyExchangeDTO().getConvertedPurchaseAmount(), new BigDecimal("1013442.10"));
        assertEquals(paymentQueryResponseDTO.getCurrencyExchangeDTO().getConvertedCountryCurrency(), DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC);
    }
}
