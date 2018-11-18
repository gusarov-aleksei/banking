package com.banking.dao;

import com.banking.model.Account;
import com.banking.model.Transfer;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class BankingDataStorageImpl implements BankingDataStorage {

    private Map<Long, Account> accounts;
    private Map<Long, Transfer> transfers;

    public BankingDataStorageImpl() {
        this.accounts = new ConcurrentHashMap<>();
        this.transfers = new ConcurrentHashMap<>();
    }

    public BankingDataStorageImpl(Map<Long, Account> accounts, Map<Long, Transfer> transfers) {
        this.accounts = accounts;
        this.transfers = transfers;
    }

    @Override
    public void storeTransfer(final Transfer transfer) {
        transfers.put(transfer.getId(), transfer);
    }

    @Override
    public void storeAccount(final Account account) {
        accounts.put(account.getId(), account);
    }

    @Override
    public Account retrieveAccountById(final Long id) {
        return accounts.get(id);
    }

    @Override
    public Collection<Transfer> getTransfersByAccountId(final Long accountId) {
        //full scan
        return transfers.values().stream().filter(t -> t.getAccountId().equals(accountId)).collect(toList());
    }


    protected int getAccountsSize(){
        return accounts.size();
    }

}