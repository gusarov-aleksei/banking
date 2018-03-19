package com.banking.dao;

import com.banking.model.Account;
import com.banking.util.ConcurrencyUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BankingDataStorageConcurrencyTest {

    private BankingDataStorageImpl storage = new BankingDataStorageImpl();

    @Test
    public void testStoreAccount_store10000AccountsConcurrently_allAccountsAreStored(){
        ConcurrencyUtils.executeAndWait(List.of(() -> {for(int i = 0; i<100; i++)  storage.storeAccount(new Account());}));
        assertEquals(10000, storage.getAccountsSize());
    }
    //TODO add test with execute store and get

}
