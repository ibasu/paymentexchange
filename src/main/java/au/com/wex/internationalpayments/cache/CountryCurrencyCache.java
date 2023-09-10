package au.com.wex.internationalpayments.cache;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@NoArgsConstructor
@Slf4j
public class CountryCurrencyCache implements Cache<Set<String>> {
    private Set<String> countryCurrencySet = new HashSet<>();

    @Override
    public Set<String> retrieveCache() {
        return this.countryCurrencySet;
    }

    @Override
    public void addToCache(Set<String> dataToCache) {
        this.countryCurrencySet.addAll(dataToCache);
    }
}
