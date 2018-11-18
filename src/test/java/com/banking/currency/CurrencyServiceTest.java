package com.banking.currency;

import com.banking.currency.exception.CurrencyRateNotFoundException;
import com.banking.currency.stub.CurrencyRatesResourceStub;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.banking.currency.CurrencyServiceImpl.DEFAULT_ACCURACY;
import static com.banking.currency.Currency.EUR;
import static com.banking.currency.Currency.USD;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

public class CurrencyServiceTest {

    private CurrencyService service;
    private ReentrantReadWriteLock lock;
    private CurrencyRatesResourceStub currencyRates = new CurrencyRatesResourceStub();

    @Before
    public void setUp() {
        lock = spy(new ReentrantReadWriteLock());
        service = new CurrencyServiceImpl(lock, currencyRates.prepareRates());
    }

    @Test
    public void testGetRate_getRateAudToEur_returns0point63816() {
        BigDecimal rate = service.getRate(Currency.AUD, Currency.EUR);
        assertThat(rate, is(BigDecimal.valueOf(0.63816)));
    }

    @Test(expected = CurrencyRateNotFoundException.class)
    public void testGetRate_fromChfToEurWhenCacheHasNoChf_throwsCurrencyRateNotFoundException() {
        service.getRate(Currency.CHF, Currency.EUR);
    }

    @Test(expected = CurrencyRateNotFoundException.class)
    public void testGetRate_fromEurToChfWhenCacheHasNoChf_throwsCurrencyRateNotFoundException() {
        service.getRate(Currency.EUR, Currency.CHF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRate_fromIsNull_throwsIllegalArgumentException() {
        service.getRate(null, Currency.CHF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRate_targetIsNull_throwsIllegalArgumentException() {
        service.getRate(null, Currency.CHF);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdate_ratesMapIsNull_throwsIllegalArgumentException() {
        service.update(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvert_convertRequestIsNull_throwsIllegalArgumentException() {
        service.convert(null);
    }

    @Test
    public void testConvert_10EurToUsd_returns12point3659() {
        BigDecimal amount = service.convert(new ConvertRequest(EUR, USD, BigDecimal.TEN));
        assertThat(amount.setScale(DEFAULT_ACCURACY), is(BigDecimal.valueOf(12.3659).setScale(DEFAULT_ACCURACY)));
    }

    @Test
    public void testUpdate_EurRatesIsUpdated_convertUsesUpdatedRate() {
        service.update(currencyRates.prepareAnotherRates());
        BigDecimal amount = service.convert(new ConvertRequest(EUR, USD, BigDecimal.TEN));
        assertThat(amount.setScale(DEFAULT_ACCURACY), is(BigDecimal.valueOf(13.4567).setScale(DEFAULT_ACCURACY)));
    }

    /*@Test(expected = CurrencyRateNotFoundException.class)
    public void testConstructor_defaultParameters_ratesCacheIsEmpty() {
        CurrencyService service = new CurrencyServiceImpl();
        service.getRate(Currency.EUR, Currency.USD);
    }*/

}
