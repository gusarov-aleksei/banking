package com.banking.dao;

import com.banking.model.Account;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


public class BankingDataStorageTest {

    private Map<Long, Account> spyAccounts;
    private Map<Long, Transfer> spyTransfers;

    private BankingDataStorage storage;

    private Account account = new Account();
    private Account anotherAccount = new Account();
    private Transfer transfer = new Transfer(new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.valueOf(10), Long.valueOf(1)));

    @Before
    public void setUp() {
        spyAccounts = spy(new ConcurrentHashMap<>());
        spyTransfers = spy(new ConcurrentHashMap<>());
        storage = new BankingDataStorageImpl(spyAccounts, spyTransfers);
    }

    @Test
    public void testStoreAccount_accountIsPassed_accountIsStored(){
        storage.storeAccount(account);
        verify(spyAccounts).put(account.getId(), account);
    }

    @Test
    public void testStoreTransfer_transferIsPassed_transferIsStored(){
        storage.storeTransfer(transfer);
        verify(spyTransfers).put(transfer.getId(), transfer);
    }

    @Test
    public void testRetrieveAccountById_idIsNotNull_accountIsReturnedFromInternalMap(){
        storage.retrieveAccountById(account.getId());
        verify(spyAccounts).get(account.getId());
    }

    @Test
    public void testRetrieveAccountById_idOfStoredAccount_accountIsReturned(){
        storage.storeAccount(account);
        Assert.assertThat(storage.retrieveAccountById(account.getId()), is(account));
    }

    @Test
    public void testRetrieveAccountById_idOfNotStoredAccount_nullIsReturned(){
        Assert.assertThat(storage.retrieveAccountById(account.getId()), nullValue());
    }

    @Test
    public void testGetTransfersByAccountId_idIsNotNull_transfersListIsReturned(){
        Transfer t1 = new Transfer(new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.valueOf(10), account.getId()));
        Transfer t2 = new Transfer(new TransferRequest(TransferRequest.Type.WITHDRAWAL, BigDecimal.valueOf(9), account.getId()));
        Transfer t3 = new Transfer(new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.valueOf(10), anotherAccount.getId()));
        storage.storeTransfer(t1);
        storage.storeTransfer(t2);
        storage.storeTransfer(t3);
        assertThat(storage.getTransfersByAccountId(account.getId()), containsInAnyOrder(t1, t2));
    }

    @Test
    public void testGetTransfersByAccountId_idIsNull_emptyListIsReturned(){
        storage.storeTransfer(transfer);
        assertThat(storage.getTransfersByAccountId(null), containsInAnyOrder());
    }

}
