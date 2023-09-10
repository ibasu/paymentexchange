package au.com.wex.internationalpayments.exception.handler;

import au.com.wex.internationalpayments.exception.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private void logException(Throwable throwable) {
        log.error("Exception : {}", throwable);
    }

    private List<ApiError> processErrors(BaseException ex, HttpStatus httpStatus) {
        return Arrays.asList(ApiError.builder()
                .fieldName(ex.getFieldName())
                .errorId(httpStatus.toString())
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private List<ApiError> processFieldErrors(Set<ConstraintViolation<?>> fieldErrors, HttpStatus httpStatus) {
        List<ApiError> errorsList = new ArrayList<>();

        fieldErrors.forEach(e -> errorsList.add(ApiError.builder()
                .fieldName(e.getPropertyPath().toString())
                .errorId(httpStatus.toString())
                .timestamp(LocalDateTime.now())
                .errorMessage(e.getMessage()).build()));

        log.error(errorsList.toString());

        return errorsList;
    }

    @ExceptionHandler({ExchangeRateNotFoundException.class, ResourceNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    List<ApiError> onNotFoundException(BaseException ex) {
        return processErrors(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    List<ApiError> onBusinessException(BusinessException ex) {
        return processErrors(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    List<ApiError> onConstraintValidationException(ConstraintViolationException e) {
        logException(e);
        List<ApiError> apiErrors = processFieldErrors(e.getConstraintViolations(), HttpStatus.BAD_REQUEST);
        log.error("Api Error : {}", apiErrors);

        return apiErrors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    List<ApiError> onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ApiError> apiErrors = new ArrayList<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            apiErrors.add(ApiError.builder()
                    .fieldName(fieldError.getField())
                    .errorId(HttpStatus.BAD_REQUEST.toString())
                    .timestamp(LocalDateTime.now())
                    .errorMessage(fieldError.getDefaultMessage()).build());
        }
        return apiErrors;
    }
}
