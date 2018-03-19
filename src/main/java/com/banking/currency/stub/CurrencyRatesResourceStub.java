package com.banking.currency.stub;

import com.banking.currency.Currency;

import java.math.BigDecimal;
import java.util.Map;

import static com.banking.currency.Currency.*;
import static java.math.BigDecimal.valueOf;

public class CurrencyRatesResourceStub {
    //1 USD 	USD 1.00000 EUR 0.80857 GBP 0.71620 AUD 1.26720
    //1 EUR 	USD 1.23659 EUR 1.00000 GBP 0.88577	AUD	1.56701
    //1 GBP 	USD 1.39607	EUR 1.12897 GBP 1.00000 AUD 1.76910
    //1 AUD 	USD 0.78914 EUR 0.63816 GBP 0.56526	AUD	1.00000
    public Map<Currency, Map<Currency, BigDecimal>> prepareRates() {
        return Map.of( USD, Map.of(USD, valueOf(1.00000), EUR, valueOf(0.80857), GBP, valueOf(0.71620), AUD, valueOf(1.26720)),
                EUR, Map.of(USD, valueOf(1.23659), EUR, valueOf(1.00000), GBP, valueOf(0.88577), AUD, valueOf(1.56701)),
                GBP, Map.of(USD, valueOf(1.39607), EUR, valueOf(1.12897), GBP, valueOf(1.00000), AUD, valueOf(1.76910)),
                AUD, Map.of(USD, valueOf(0.78914), EUR, valueOf(0.63816), GBP, valueOf(0.56526), AUD, valueOf(1.00000)));
    }

    public Map<Currency, Map<Currency, BigDecimal>> prepareAnotherRates() {
        //EUR->USD is changed to 1.34567
        return Map.of( USD, Map.of(USD, valueOf(1.00000), EUR, valueOf(0.80857), GBP, valueOf(0.71620), AUD, valueOf(1.26720)),
                EUR, Map.of(USD, valueOf(1.34567), EUR, valueOf(1.00000), GBP, valueOf(0.88577), AUD, valueOf(1.56701)),
                GBP, Map.of(USD, valueOf(1.39607), EUR, valueOf(1.12897), GBP, valueOf(1.00000), AUD, valueOf(1.76910)),
                AUD, Map.of(USD, valueOf(0.78914), EUR, valueOf(0.63816), GBP, valueOf(0.56526), AUD, valueOf(1.00000)));
    }

}
