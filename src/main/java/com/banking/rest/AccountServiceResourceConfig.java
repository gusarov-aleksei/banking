package com.banking.rest;

import com.banking.account.AccountService;
import com.banking.account.AccountServiceImpl;
import com.banking.account.AmountMover;
import com.banking.currency.CurrencyService;
import com.banking.currency.CurrencyServiceImpl;
import com.banking.dao.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Singleton;

public class AccountServiceResourceConfig extends ResourceConfig {
    public AccountServiceResourceConfig() {
        packages(true, "com.banking");
        register(new BankingApplicationBinder());
        register(AccountRestService.class);
        //init data base here
    }

    class BankingApplicationBinder extends AbstractBinder {
        @Override
        protected void configure() {
            bind(AccountDaoImpl.class).to(AccountDao.class).in(Singleton.class);
            bind(TransferDaoImpl.class).to(TransferDao.class).in(Singleton.class);
            bind(BankingDataStorageImpl.class).to(BankingDataStorage.class).in(Singleton.class);
            //bind(new ReentrantReadWriteLock()).to(ReadWriteLock.class).named("name");
            bind(CurrencyServiceImpl.class).to(CurrencyService.class).in(Singleton.class);
            bind(AccountServiceImpl.class).to(AccountService.class).in(Singleton.class);
            bind(AmountMover.class).to(AmountMover.class).in(Singleton.class);
        }
    }
}