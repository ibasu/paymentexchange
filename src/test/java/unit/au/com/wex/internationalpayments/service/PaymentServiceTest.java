package au.com.wex.internationalpayments.service;

import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.exception.ResourceNotFoundException;
import au.com.wex.internationalpayments.repository.PaymentRepository;
import au.com.wex.internationalpayments.repository.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static common.internationalpayments.helper.PaymentDataHelper.*;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private TreasuryService treasuryService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    public void testShouldFetchPaymentTransaction() throws ResourceNotFoundException {
        PaymentQueryDTO paymentQueryDTO = defaultPaymentQueryDTO();

        when(paymentRepository.findById(paymentQueryDTO.getTransactionId())).thenReturn(Optional.of(defaultPaymentEntity()));
        when(treasuryService.fetchExchangeRate(any())).thenReturn(Mono.just(defaultFiscalData()));

        Mono<PaymentQueryResponseDTO> responseMono = paymentService.fetch(paymentQueryDTO);

        StepVerifier
                .create(responseMono)
                .consumeNextWith(response -> {
                    assertEquals(response.getCurrencyExchangeDTO(), defaultCurrencyExchangeDTO());
                })
                .verifyComplete();
    }

    @Test
    void testPersistNewPaymentTransaction() {
        PaymentDTO paymentDTO = defaultPaymentDTO();

        when(paymentRepository.save(any(PaymentEntity.class))).thenReturn(defaultPaymentEntity());
        this.paymentService.persist(paymentDTO);
        verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
    }

    @Test
    public void shouldGetUserNotFound() {
        when(paymentRepository.findById(DEFAULT_PAYMENT_TRANSACTION_ID)).thenReturn(empty());

        assertThatThrownBy(() -> paymentService.fetch(defaultPaymentQueryDTO())).isInstanceOf(ResourceNotFoundException.class);
    }
}
