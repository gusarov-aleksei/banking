package com.banking.currency;

import com.banking.currency.stub.CurrencyRatesResourceStub;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CurrencyServiceConcurrencyTest {

    private CurrencyService service;
    private ReentrantReadWriteLock lock;
    private CurrencyRatesResourceStub currencyRates = new CurrencyRatesResourceStub();

    @Before
    public void setUp() {
        lock = spy(new ReentrantReadWriteLock());
        service = new CurrencyServiceImpl(lock, currencyRates.prepareRates());
    }

    @Test
    public void testGetRate_getRateForExistedInCacheCurrencies_readLockIsCalled2times() {
        service.getRate(Currency.AUD, Currency.EUR);
        verify(lock, times(2)).readLock();//one for lock, one for unlock
    }

    @Test
    public void testUpdate_ratesCacheIsChanging_writeLockIsCalled2times() {
        service.update(currencyRates.prepareAnotherRates());
        verify(lock, times(2)).writeLock();//one for lock, one for unlock
    }

    @Test(timeout = 2000)
    public void testGetRate_writeLockIsAcquired_allReadingThreadsAreInWaitingQueue() {
        lock.writeLock().lock(); //acquire write lock by current thread
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        IntStream.range(0, 5).forEach((i) -> executor.execute(() -> service.getRate(Currency.AUD, Currency.EUR)));
        while (lock.getQueueLength()<5) { } //wait till all threads be in waiting gueue
        int readingThreadsQueueSize = lock.getQueueLength();
        lock.writeLock().unlock();
        executor.shutdown();
        assertThat(readingThreadsQueueSize, is(5));
    }


    @Test(timeout = 3000)
    public void testGetRate_writeLockIsAcquired_readingThreadWaitsAndRetrievesUpdatedRatesFinally()
            throws ExecutionException, InterruptedException {
        //here are outdated rates. system is going to update them.
        lock.writeLock().lock(); //acquire write lock by current thread
        ExecutorService exe = Executors.newSingleThreadExecutor();
        Future<BigDecimal> rate = exe.submit(() -> service.getRate(Currency.EUR, Currency.USD));
        //here system starts reading rates. reading threads are put into waiting queue because of locked writeLock
        while (lock.getQueueLength()<1) { }//wait till all threads will be in waiting queue
        assertThat(lock.hasQueuedThreads(), is(true));//one thread of service executor is in waiting queue
        //update currency rates in current thread
        service.update(currencyRates.prepareAnotherRates()); //updated Eur->Usd rate is 1.34567 (old was 1.23659)
        lock.writeLock().unlock();//allow waiting threads read rates
        exe.shutdown();
        Assert.assertThat(rate.get(), is(BigDecimal.valueOf(1.34567)));//check results of reading thread tasks.
    }


}
