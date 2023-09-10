package au.com.wex.internationalpayments.controller;

import au.com.wex.internationalpayments.cache.CountryCurrencyCache;
import au.com.wex.internationalpayments.dto.CurrencyEnum;
import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryDTO;
import au.com.wex.internationalpayments.exception.ResourceNotFoundException;
import au.com.wex.internationalpayments.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static common.internationalpayments.helper.PaymentDataHelper.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(PaymentController.class)
class PaymentControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private CountryCurrencyCache countryCurrencyCache;

    @BeforeEach
    void beforeEach() {
        when(countryCurrencyCache.retrieveCache()).thenReturn(defaultCountryCurrencyDesc());
    }

    @Test
    public void testShouldGetPaymentTransaction() throws ResourceNotFoundException {
        PaymentQueryDTO paymentQueryDTO = defaultPaymentQueryDTO();
        when(paymentService.fetch(paymentQueryDTO)).thenReturn(Mono.just(defaultPaymentQueryResponseDTO()));
        when(countryCurrencyCache.retrieveCache()).thenReturn(defaultCountryCurrencyDesc());

        BigDecimal expectedExchangeCurrency = new BigDecimal(82.09);

        webTestClient
                .get().uri("/api/v1/payments/" + paymentQueryDTO.getTransactionId() + "/" + paymentQueryDTO.getConvertedCurrencyDesc())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.paymentTransactionDetails.transactionId").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_ID)
                .jsonPath("$.paymentTransactionDetails.purchaseAmount").isEqualTo(DEFAULT_PAYMENT_TRANSACTION.doubleValue())
                .jsonPath("$.paymentTransactionDetails.originalCurrency").isEqualTo(CurrencyEnum.USD.name())
                .jsonPath("$.paymentTransactionDetails.transactionDescription").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_DESC)
                .jsonPath("$.paymentTransactionDetails.createdUser").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_USER)
                .jsonPath("$.exchangeRateDetails.exchangeRate").isEqualTo(new BigDecimal(82.09).doubleValue())
                .jsonPath("$.exchangeRateDetails.originalPurchaseAmount").isEqualTo(new BigDecimal(12345.50).doubleValue())
                .jsonPath("$.exchangeRateDetails.convertedPurchaseAmount").isEqualTo(new BigDecimal(1013442.10).doubleValue())
                .jsonPath("$.exchangeRateDetails.convertedCountryCurrency").isEqualTo(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC);
    }

    @Test
    public void testGetPaymentTransactionNotFoundError() throws ResourceNotFoundException {
        PaymentQueryDTO paymentQueryDTO = defaultPaymentQueryDTO();
        when(countryCurrencyCache.retrieveCache()).thenReturn(defaultCountryCurrencyDesc());
        when(paymentService.fetch(paymentQueryDTO)).thenThrow(new ResourceNotFoundException("No transaction found by the id: " + paymentQueryDTO.getTransactionId(), "transactionId"));

        webTestClient
                .get().uri("/api/v1/payments/" + paymentQueryDTO.getTransactionId() + "/" + paymentQueryDTO.getConvertedCurrencyDesc())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].errorId").isEqualTo("404 NOT_FOUND")
                .jsonPath("$[0].errorMessage").isEqualTo("No transaction found by the id: " + paymentQueryDTO.getTransactionId())
                .jsonPath("$[0].fieldName").isEqualTo("transactionId");

    }

    @Test
    public void testGetPaymentTransactionValidationError() throws ResourceNotFoundException {
        PaymentQueryDTO paymentQueryDTO = defaultPaymentQueryDTO();
        when(countryCurrencyCache.retrieveCache()).thenReturn(defaultCountryCurrencyDesc());

        webTestClient
                .get().uri("/api/v1/payments/" + paymentQueryDTO.getTransactionId() + "/Invalid-Currency")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$[0].errorId").isEqualTo("400 BAD_REQUEST")
                .jsonPath("$[0].errorMessage").isEqualTo(DEFAULT_INVALID_COUNTRY_CURRENCY_ERROR_MESSAGE)
                .jsonPath("$[0].fieldName").isEqualTo("payment.desiredCountryCurrency");

    }

    @Test
    public void testShouldCreatePaymentTransaction() {
        PaymentDTO paymentDTO = defaultPaymentDTO();
        given(paymentService.persist(any(PaymentDTO.class)))
                .willReturn(paymentDTO);

        webTestClient
                .post().uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(paymentDTO), PaymentDTO.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.transactionId").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_ID)
                .jsonPath("$.purchaseAmount").isEqualTo(DEFAULT_PAYMENT_TRANSACTION.doubleValue())
                .jsonPath("$.originalCurrency").isEqualTo(CurrencyEnum.USD.name())
                .jsonPath("$.transactionDescription").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_DESC)
                .jsonPath("$.createdUser").isEqualTo(DEFAULT_PAYMENT_TRANSACTION_USER);

    }
}