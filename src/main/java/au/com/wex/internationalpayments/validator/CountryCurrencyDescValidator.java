package au.com.wex.internationalpayments.validator;

import au.com.wex.internationalpayments.cache.CountryCurrencyCache;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class CountryCurrencyDescValidator implements ConstraintValidator<CountryCurrency, String> {

    @Autowired
    private CountryCurrencyCache countryCurrencyCache;

    @Override
    public void initialize(CountryCurrency constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            return countryCurrencyCache.retrieveCache().contains(value.toUpperCase());
        } catch (final Exception e) {
            log.error("Severe error while validating country currency code", e);
            return false;
        }
    }
}
