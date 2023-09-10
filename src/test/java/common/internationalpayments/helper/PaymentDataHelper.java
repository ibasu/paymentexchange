package common.internationalpayments.helper;

import au.com.wex.generated.fiscaldata.pojo.Datum;
import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.dto.*;
import au.com.wex.internationalpayments.mapper.PaymentMapper;
import au.com.wex.internationalpayments.repository.entity.PaymentEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class PaymentDataHelper {

    public static final BigDecimal DEFAULT_PAYMENT_TRANSACTION = new BigDecimal("12345.50");
    public static final String DEFAULT_PAYMENT_TRANSACTION_USER = "IBasu";
    public static final String DEFAULT_PAYMENT_TRANSACTION_ID = "df464e0a-7ccb-49ad-beb0-6049832b2f8d";
    public static final String DEFAULT_PAYMENT_TRANSACTION_DESC = "Big Payment";
    public static final String DEFAULT_PAYMENT_EXCHANGE_RATE_COUNTRY = "India";
    public static final String DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY = "Rupee";
    public static final String DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC = "India-Rupee";
    public static final String AUD_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC = "Australia-Dollar";
    public static final String DEFAULT_INVALID_COUNTRY_CURRENCY_ERROR_MESSAGE = "The Country Currency is unsupported. Please check the link https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country,country_currency_desc";

    private PaymentMapper paymentMapper = new PaymentMapper();

    public static PaymentDTO defaultPaymentDTO() {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setTransactionId(DEFAULT_PAYMENT_TRANSACTION_ID);
        paymentDTO.setTransactionDate(LocalDateTime.now());
        paymentDTO.setTransactionDescription(DEFAULT_PAYMENT_TRANSACTION_DESC);
        paymentDTO.setPurchaseAmount(DEFAULT_PAYMENT_TRANSACTION);
        paymentDTO.setCreatedUser(DEFAULT_PAYMENT_TRANSACTION_USER);

        return paymentDTO;
    }

    public static PaymentEntity defaultPaymentEntity() {
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setId(DEFAULT_PAYMENT_TRANSACTION_ID);
        paymentEntity.setTransactionAmount(DEFAULT_PAYMENT_TRANSACTION);
        paymentEntity.setCreatedUser(DEFAULT_PAYMENT_TRANSACTION_USER);
        paymentEntity.setTransactionDate(LocalDateTime.now());
        paymentEntity.setTransactionDescription(DEFAULT_PAYMENT_TRANSACTION_DESC);
        paymentEntity.setTransactionOriginalCurrencyCode(CurrencyEnum.USD);

        return paymentEntity;
    }

    public static Fiscaldata defaultFiscalData() {
        Fiscaldata fiscaldata = new Fiscaldata();

        fiscaldata.setData(Arrays.asList(defaultFiscalDatum1(), defaultFiscalDatum2()));

        return fiscaldata;
    }

    public static Datum defaultFiscalDatum1() {
        Datum datum = new Datum();
        datum.setCountry(DEFAULT_PAYMENT_EXCHANGE_RATE_COUNTRY);
        datum.setCurrency(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY);
        datum.setCountryCurrencyDesc(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC);
        datum.setExchangeRate("82.086");
        datum.setRecordDate("2023-06-30");

        return datum;
    }

    public static Datum defaultFiscalDatum2() {
        Datum datum = new Datum();
        datum.setCountry(DEFAULT_PAYMENT_EXCHANGE_RATE_COUNTRY);
        datum.setCurrency(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY);
        datum.setCountryCurrencyDesc(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC);
        datum.setExchangeRate("82.19");
        datum.setRecordDate("2023-03-31");

        return datum;
    }

    public static PaymentQueryDTO defaultPaymentQueryDTO() {
        return PaymentQueryDTO.builder()
                .transactionId(DEFAULT_PAYMENT_TRANSACTION_ID)
                .convertedCurrencyDesc(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC)
                .build();
    }

    public static CurrencyExchangeDTO defaultCurrencyExchangeDTO() {
        return CurrencyExchangeDTO.builder()
                .exchangeRate(new BigDecimal("82.09"))
                .originalPurchaseAmount(defaultPaymentDTO().getPurchaseAmount())
                .convertedCountryCurrency(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC)
                .convertedPurchaseAmount(new BigDecimal("1013419.11"))
                .build();
    }

    public static PaymentQueryResponseDTO defaultPaymentQueryResponseDTO() {
        return PaymentQueryResponseDTO.builder()
                .currencyExchangeDTO(defaultCurrencyExchangeDTO())
                .paymentDTO(defaultPaymentDTO())
                .build();
    }

    public static Set<String> defaultCountryCurrencyDesc() {
        Set<String> countryCurrencyDesc = new HashSet<>();
        countryCurrencyDesc.add(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC.toUpperCase());
        countryCurrencyDesc.add(AUD_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC.toUpperCase());

        return countryCurrencyDesc;
    }
}
