package au.com.wex.internationalpayments.exception;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String message, String fieldName) {
        super(message, fieldName);
    }
}
