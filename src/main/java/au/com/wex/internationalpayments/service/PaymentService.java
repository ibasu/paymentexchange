package au.com.wex.internationalpayments.service;

import au.com.wex.generated.fiscaldata.pojo.Fiscaldata;
import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.dto.RateExchangeDTO;
import au.com.wex.internationalpayments.exception.ApiError;
import au.com.wex.internationalpayments.exception.BusinessException;
import au.com.wex.internationalpayments.exception.ResourceNotFoundException;
import au.com.wex.internationalpayments.exception.ServerException;
import au.com.wex.internationalpayments.mapper.PaymentMapper;
import au.com.wex.internationalpayments.repository.PaymentRepository;
import au.com.wex.internationalpayments.repository.entity.PaymentEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
public class PaymentService implements IPaymentService {

    private final PaymentRepository paymentRepository;
    private final ITreasuryService treasuryService;

    private final PaymentMapper paymentMapper = new PaymentMapper();

    public PaymentService(PaymentRepository paymentRepository, TreasuryService treasuryService) {
        this.paymentRepository = paymentRepository;
        this.treasuryService = treasuryService;
    }

    public PaymentDTO persist(PaymentDTO paymentDTO) {
        log.debug("Persist : {}", paymentDTO);
        PaymentEntity paymentEntity = paymentMapper.mapToModel(paymentDTO);
        paymentEntity = paymentRepository.save(paymentEntity);

        log.debug("Done persisting : {}", paymentEntity);
        return paymentMapper.mapToDto(paymentEntity);
    }

    public Mono<PaymentQueryResponseDTO> fetch(PaymentQueryDTO paymentQueryDTO) throws ResourceNotFoundException {
        Mono<PaymentDTO> paymentDTO = Mono.just(paymentRepository.findById(paymentQueryDTO.getTransactionId())
                .map(m -> paymentMapper.mapToDto(m))
                .orElseThrow(() -> new ResourceNotFoundException("No transaction found by the id: " + paymentQueryDTO.getTransactionId(), "transactionId")));

        LocalDate exchangeRateDate = LocalDate.now().plusMonths(-6);
        log.info("Query Exchange Date: {}", exchangeRateDate);

        Mono<Fiscaldata> fiscalDataMono = treasuryService.fetchExchangeRate(RateExchangeDTO.builder()
                        .convertedCurrencyCode(paymentQueryDTO.getConvertedCurrencyDesc())
                        .rateExchangeDate(exchangeRateDate).build())
                .onErrorResume(BusinessException.class, throwable -> Mono.error(new BusinessException(ApiError.builder()
                        .fieldName("start_date")
                        .errorId(HttpStatus.BAD_REQUEST.name())
                        .timestamp(LocalDateTime.now())
                        .errorMessage(throwable.getMessage()).build())))
                .onErrorResume(ServerException.class, throwable -> Mono.error(new ServerException(ApiError.builder()
                        .fieldName("start_date")
                        .errorId(HttpStatus.BAD_REQUEST.name())
                        .timestamp(LocalDateTime.now())
                        .errorMessage(throwable.getMessage()).build())))
                .doOnSuccess(m -> log.info(String.format("Exchange Rate Successfully received for %s", paymentQueryDTO)));

        return Mono.zip(paymentDTO, fiscalDataMono, paymentMapper.mergeExchangeRate);
    }
}
