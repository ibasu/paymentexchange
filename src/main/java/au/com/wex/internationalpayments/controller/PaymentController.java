package au.com.wex.internationalpayments.controller;

import au.com.wex.internationalpayments.dto.PaymentDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryDTO;
import au.com.wex.internationalpayments.dto.PaymentQueryResponseDTO;
import au.com.wex.internationalpayments.exception.ApiError;
import au.com.wex.internationalpayments.exception.BusinessException;
import au.com.wex.internationalpayments.service.IPaymentService;
import au.com.wex.internationalpayments.validator.CountryCurrency;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/payments")
@Slf4j
@AllArgsConstructor
@Validated
public class PaymentController {

    private final IPaymentService paymentService;

    @Operation(summary = "Store Purchase Transaction")
    @PostMapping(value = "", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentDTO payment(@NotNull @Valid @RequestBody final PaymentDTO paymentDTO) {
        return paymentService.persist(paymentDTO);
    }

    @Operation(summary = "Get Payment Transaction converted to target currency other than USD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Payment Transaction",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Validation Failure", content = @Content),
            @ApiResponse(responseCode = "404", description = "When either transaction does not exist or the country currency is unsupported/invalid ", content = @Content)})
    @GetMapping("/{transactionId}/{desiredCountryCurrency}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<PaymentQueryResponseDTO> payment(@NotNull @PathVariable String transactionId, @NotNull @CountryCurrency @PathVariable String desiredCountryCurrency) {
        return paymentService.fetch(PaymentQueryDTO.builder().transactionId(transactionId).convertedCurrencyDesc(desiredCountryCurrency).build());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleItemNotFoundException(BusinessException exception) {
        log.error("Failed to find the requested element", exception);
        return new ResponseEntity<>(exception.getApiError(), HttpStatus.NOT_FOUND);
    }
}
