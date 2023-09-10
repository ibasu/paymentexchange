package au.com.wex.internationalpayments.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = CountryCurrencyDescValidator.class)
@Target({ElementType.PARAMETER, METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface CountryCurrency {

    String message() default "The Country Currency is unsupported. Please check the link https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange?fields=country,country_currency_desc";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

