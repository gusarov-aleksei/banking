package com.banking.account.rest;

import com.banking.account.AccountService;
import com.banking.model.Account;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import com.banking.model.TransferResponse;
import com.banking.rest.AccountRestService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class AccountServiceRestTest {


    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountRestService accountRestService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTransfer_transferRequestPassed_transferResponseReturned() {
        TransferRequest request = new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.valueOf(10), Long.valueOf(1));
        TransferResponse transferResponse = new TransferResponse(TransferResponse.Status.SUCCESS);
        when(accountService.transfer(request)).thenReturn(transferResponse);
        Response restResponse = accountRestService.transfer(request);
        Assert.assertTrue(restResponse.hasEntity());
        Assert.assertEquals(transferResponse ,restResponse.getEntity());
    }

    @Test
    public void testGetAccountById_accountIdIsPassed_returnsAccount(){
        Account account = new Account();
        when(accountService.getAccountById(anyLong())).thenReturn(account);
        Response restResponse = accountRestService.getAccountById(account.getId());
        Assert.assertTrue(restResponse.hasEntity());
        Assert.assertEquals(account,restResponse.getEntity());
    }

    @Test
    public void testCreateAccount_callForCreation_createsAndReturnsAccount(){
        Account account = new Account();
        when(accountService.createAccount()).thenReturn(account);
        Response restResponse = accountRestService.create();
        Assert.assertTrue(restResponse.hasEntity());
        Assert.assertEquals(account,restResponse.getEntity());
    }

    @Test
    public void testGetTransfersByAccountId_accountIdIsPassed_returnsTransfersCollectionForAccount(){
        Collection transfers = Collections.singleton(new Transfer(new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.valueOf(10), Long.valueOf(1))));
        when(accountService.getTransfersByAccountId(anyLong())).thenReturn(transfers);
        Response restResponse = accountRestService.getTransfersById(Long.valueOf(1));
        Assert.assertTrue(restResponse.hasEntity());
        Assert.assertEquals(transfers ,restResponse.getEntity());
    }

}
