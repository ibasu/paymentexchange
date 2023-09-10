package au.com.wex.internationalpayments.service;

import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.exception.ResourceNotFoundException;
import reactor.core.publisher.Mono;

public interface IPaymentService {

    PaymentDTO persist(PaymentDTO paymentDTO);

    Mono<PaymentQueryResponseDTO> fetch(PaymentQueryDTO paymentQueryDTO) throws ResourceNotFoundException;

}
