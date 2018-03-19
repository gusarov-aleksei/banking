package com.banking.currency;

import com.banking.currency.exception.CurrencyRateNotFoundException;
import com.banking.currency.stub.CurrencyRatesResourceStub;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CurrencyServiceImpl implements CurrencyService {

    public static final int DEFAULT_ACCURACY = 5;

    private ReadWriteLock lock;
    //cache must contain immutable entries
    private Map<Currency, Map<Currency, BigDecimal>> currencyRatesCache;

    private CurrencyRatesResourceStub ratesResource = new CurrencyRatesResourceStub();

    public CurrencyServiceImpl(){
        lock = new ReentrantReadWriteLock();
        currencyRatesCache = ratesResource.prepareRates();
    }

    public CurrencyServiceImpl(ReadWriteLock lock, Map<Currency, Map<Currency, BigDecimal>> currencyRatesCache) {
        this.lock = lock;
        this.currencyRatesCache = currencyRatesCache;
    }

    private Map<Currency, BigDecimal> get(Currency base) {
        lock.readLock().lock();
        try {
            return currencyRatesCache.get(base);
        } finally {
            lock.readLock().unlock();
        }
    }

    public BigDecimal getRate(Currency source, Currency target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source and target currencies must be non null");
        }
        Map<Currency, BigDecimal> sourceTargetRates = get(source);
        if (sourceTargetRates == null) {
            throw new CurrencyRateNotFoundException("Rate not found for source "+ source);
        }
        BigDecimal rate = sourceTargetRates.get(target);
        if (rate==null) {
            throw new CurrencyRateNotFoundException("Rate not found for target "+ target);
        }
        return rate;
    }

    public BigDecimal convert(ConvertRequest request){
        if (request == null) {
            throw new IllegalArgumentException("ConvertRequest must be non-null");
        }
        BigDecimal rate = getRate(request.getSource(), request.getTarget());
        return request.getAmount().multiply(rate);
    }

    //call this method when server is being initialized
    public void update(Map<Currency, Map<Currency, BigDecimal>> actualCurrencyRates) {
        if (actualCurrencyRates == null) {
            throw new IllegalArgumentException("Rates must be non-null");
        }
        lock.writeLock().lock();
        try {
            currencyRatesCache = actualCurrencyRates;
        } finally {
            lock.writeLock().unlock();
        }
    }

}
