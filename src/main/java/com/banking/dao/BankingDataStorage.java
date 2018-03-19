package com.banking.dao;


import com.banking.model.Account;
import com.banking.model.Transfer;

import java.util.Collection;

/**
 * It represents data base
 */
public interface BankingDataStorage {

    void storeTransfer(Transfer transfer);

    void storeAccount(Account account);

    Account retrieveAccountById(Long id);

    Collection<Transfer> getTransfersByAccountId(final Long accountId);

}
