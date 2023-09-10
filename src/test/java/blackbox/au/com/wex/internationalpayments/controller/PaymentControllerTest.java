package au.com.wex.internationalpayments.controller;

import au.com.wex.internationalpayments.cache.CountryCurrencyCache;
import au.com.wex.internationalpayments.configuration.PaymentsConfiguration;
import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.util.MathUtil;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static common.internationalpayments.helper.PaymentDataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class PaymentControllerTest {

    public static final String WEX_PAYMENT_ROUTE = "/api/v1/payments";
    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private PaymentsConfiguration paymentsConfiguration;

    @Autowired
    private CountryCurrencyCache countryCurrencyCache;

    private String exchangeRateRoute;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("services.treasury.base-url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void init() {
        exchangeRateRoute = paymentsConfiguration.getTreasuryExchangeRateUrl().concat(String.format("?filter=record_date:gte:%s,country_currency_desc:in:(%s)", LocalDate.now().plusMonths(-6), DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC));
        countryCurrencyCache.addToCache(defaultCountryCurrencyDesc());
    }

    @AfterEach
    void resetAll() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldReturn404ForUnknownTransactionId() {

        this.webTestClient
                .get()
                .uri(WEX_PAYMENT_ROUTE + "/abcde/India-Rupee")
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(NOT_FOUND)
                .expectBody()
                .jsonPath("$[0].errorId").isEqualTo("404 NOT_FOUND")
                .jsonPath("$[0].errorMessage").isEqualTo("No transaction found by the id: abcde")
                .jsonPath("$[0].fieldName").isEqualTo("transactionId");
    }

    @Test
    void testGetPaymentTransactionShouldError5xx() {
        PaymentDTO paymentDTO = createNewPaymentTransaction();

        wireMockServer.stubFor(
                WireMock.get(exchangeRateRoute)
                        .willReturn(aResponse()
                                .withStatus(INTERNAL_SERVER_ERROR.value())
        ));

        this.webTestClient
                .get()
                .uri(WEX_PAYMENT_ROUTE + "/" + paymentDTO.getTransactionId() + "/" + DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC)
                .exchange()
                .expectStatus()
                .isEqualTo(INTERNAL_SERVER_ERROR)
                .expectBody()
                .jsonPath("$.errorId").isEqualTo("500 INTERNAL_SERVER_ERROR");
    }

    @Test
    void testShouldCreateNewPaymentTransaction() throws Exception {
        PaymentDTO paymentDTO = createNewPaymentTransaction();

        assertNotNull(paymentDTO.getTransactionId());
        assertEquals(paymentDTO.getCreatedUser(), defaultPaymentDTO().getCreatedUser());
        assertEquals(paymentDTO.getTransactionDescription(), defaultPaymentDTO().getTransactionDescription());
        assertEquals(paymentDTO.getPurchaseAmount(), defaultPaymentDTO().getPurchaseAmount());
    }

    @Test
    void testGetPaymentTransaction() {
        wireMockServer.stubFor(
                WireMock.get(WireMock.urlEqualTo(exchangeRateRoute))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("treasury/200_exchange_rate_inr.json"))
        );

        PaymentDTO paymentDTO = createNewPaymentTransaction();

        EntityExchangeResult<PaymentQueryResponseDTO> paymentQueryResponse = this.webTestClient
                .get()
                .uri("/api/v1/payments/" + paymentDTO.getTransactionId() + "/" + DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(OK)
                .expectBody(PaymentQueryResponseDTO.class)
                .returnResult();

        PaymentQueryResponseDTO paymentQueryResponseDTO = paymentQueryResponse.getResponseBody();
        assertEquals(paymentQueryResponseDTO.getPaymentDTO().getTransactionId(), paymentDTO.getTransactionId());
        assertEquals(paymentQueryResponseDTO.getCurrencyExchangeDTO().getConvertedPurchaseAmount(), MathUtil.multiplyAndScale(paymentDTO.getPurchaseAmount(), paymentQueryResponseDTO.getCurrencyExchangeDTO().getExchangeRate(), 2));
        assertEquals(paymentQueryResponseDTO.getCurrencyExchangeDTO().getConvertedCountryCurrency(), "India-Rupee");
    }

    private PaymentDTO createNewPaymentTransaction() {
        PaymentDTO paymentDTO = defaultPaymentDTO();

        EntityExchangeResult<PaymentDTO> paymentCreatedResponse = this.webTestClient
                .post()
                .uri(WEX_PAYMENT_ROUTE)
                .body(Mono.just(paymentDTO), PaymentDTO.class)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isEqualTo(CREATED)
                .expectBody(PaymentDTO.class)
                .returnResult();

        paymentDTO = paymentCreatedResponse.getResponseBody();
        return paymentDTO;
    }
}