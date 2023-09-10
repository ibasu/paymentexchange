package au.com.wex.internationalpayments.exception;

import lombok.Getter;

@Getter
public class BusinessException extends BaseException {
    private ApiError apiError;

    public BusinessException(String message, String fieldName) {
        super(message, fieldName);
    }

    public BusinessException(ApiError apiError) {
        super(apiError.getErrorMessage(), apiError.getFieldName());
    }

}
