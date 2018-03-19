package com.banking.dao;

import com.banking.model.Account;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountDaoTest {

    @Mock
    private BankingDataStorage bankingDataStorage;
    @InjectMocks
    private AccountDaoImpl accountDao;

    Account account = new Account();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountById_idIsNull_throwsIllegalArgumentException() {
        accountDao.getAccountById(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountById_idIsNonNull_callIsDelegatedToStorage() {
        accountDao.getAccountById(null);
        when(bankingDataStorage.retrieveAccountById(any(Long.class))).thenReturn(account);
        Account actual = accountDao.getAccountById(Long.valueOf(1));
        verify(bankingDataStorage).retrieveAccountById(Long.valueOf(1));
        assertThat(account, is(actual));
    }

    @Test
    public void testCreate_noParameters_accountIsCreatedAnPutInStorageAndReturned() {
        doNothing().when(bankingDataStorage).storeAccount(any(Account.class));
        Account actual = accountDao.create();
        verify(bankingDataStorage).storeAccount(any(Account.class));
        assertThat(actual, isA(Account.class));
    }


}
