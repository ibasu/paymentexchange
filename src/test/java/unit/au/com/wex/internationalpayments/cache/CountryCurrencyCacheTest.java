package au.com.wex.internationalpayments.cache;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static common.internationalpayments.helper.PaymentDataHelper.DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC;
import static common.internationalpayments.helper.PaymentDataHelper.defaultCountryCurrencyDesc;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CountryCurrencyCacheTest {

    private CountryCurrencyCache cache = new CountryCurrencyCache();

    @Test
    public void testShouldAddToCache() {
        Set<String> countryCurrencyCache = defaultCountryCurrencyDesc();
        cache.addToCache(countryCurrencyCache);

        assertEquals(countryCurrencyCache.size(), cache.retrieveCache().size());
        assertEquals(Boolean.TRUE, cache.retrieveCache().contains(DEFAULT_PAYMENT_EXCHANGE_RATE_CURRENCY_DESC.toUpperCase()));
        assertEquals(countryCurrencyCache.size(), cache.retrieveCache().size());
    }

}
