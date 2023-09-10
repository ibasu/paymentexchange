package au.com.wex.internationalpayments.exception;

public class ExchangeRateNotFoundException extends BusinessException {

    public ExchangeRateNotFoundException(String fieldName, String message) {
        super(message, fieldName);
    }

}
