package com.banking.dao;


import com.banking.model.Account;

import javax.inject.Inject;
import static java.util.Objects.*;


public class AccountDaoImpl implements AccountDao {

    @Inject
    private BankingDataStorage bankingDataStorage;

    @Override
    public Account getAccountById(final Long id) {
        if (isNull(id)) {
            throw new IllegalArgumentException("Expected non-null value");
        }
        return bankingDataStorage.retrieveAccountById(id);
    }

    private void save(final Account account) {
        bankingDataStorage.storeAccount(account);
    }

    @Override
    public Account create() {
        Account account = new Account();
        save(account);
        return account;
    }

}
