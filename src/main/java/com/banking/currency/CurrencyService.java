package com.banking.currency;


import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyService {

    BigDecimal getRate(Currency source, Currency target);

    BigDecimal convert(ConvertRequest request);

    void update(Map<Currency, Map<Currency, BigDecimal>> actualCurrencyRates);

}
