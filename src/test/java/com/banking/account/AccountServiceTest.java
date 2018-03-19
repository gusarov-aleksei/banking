package com.banking.account;

import com.banking.account.exception.InsufficientFundsException;
import com.banking.currency.ConvertRequest;
import com.banking.currency.Currency;
import com.banking.currency.CurrencyService;
import com.banking.dao.AccountDao;
import com.banking.dao.TransferDao;
import com.banking.model.Account;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import com.banking.model.TransferResponse;

import static com.banking.model.TransferRequest.Type.DEPOSIT;
import static com.banking.model.TransferRequest.Type.WITHDRAWAL;
import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.banking.model.TransferRequest.Type.TRANSFER;
import static com.banking.model.TransferResponse.Status.*;
import static org.mockito.Mockito.*;


public class AccountServiceTest {

    @Mock
    private TransferDao mockedTransferDao;
    @Mock
    private AccountDao mockedAccountDao;
    @Mock
    private AmountMover mockedAmountMover;
    @Mock
    private CurrencyService mockedCurrencyService;

    @InjectMocks
    private AccountService accountService = new AccountServiceImpl();

    private Account source = new Account();
    private Account target = new Account();
    private TransferRequest transfer = new TransferRequest(TRANSFER, new BigDecimal(10), source.getId(), target.getId());
    private TransferRequest withdrawal = new TransferRequest(WITHDRAWAL, new BigDecimal(10), source.getId());
    private TransferRequest deposit = new TransferRequest(DEPOSIT, new BigDecimal(10), source.getId());

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mockedAccountDao.getAccountById(source.getId())).thenReturn(source);
        when(mockedAccountDao.getAccountById(target.getId())).thenReturn(target);
    }

    @Test
    public void testGetAccountById_existedAccountIdIsPassed_accountIsRetrieved() {
        when(mockedAccountDao.getAccountById(source.getId())).thenReturn(source);
        Account returned = accountService.getAccountById(source.getId());
        verify(mockedAccountDao).getAccountById(source.getId());
        assertNotNull(returned);
        assertEquals(returned.getId(), source.getId());
    }

    @Test
    public void testGetAccountById_nonExistedAccountIdIsPassed_nothingIsRetrieved() {
        when(mockedAccountDao.getAccountById(source.getId())).thenReturn(null);
        Account returned = accountService.getAccountById(source.getId());
        verify(mockedAccountDao).getAccountById(source.getId());
        assertNull(returned);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetAccountById_idIsNull_exceptionIsThrown() {
        accountService.getAccountById(null);
    }

    @Test
    public void testGetTransfersByAccountId_idIsNull_emptyCollectionIsReturned(){
        Assert.assertEquals(Collections.EMPTY_LIST, accountService.getTransfersByAccountId(null));
    }

    @Test
    public void testGetTransfersByAccountId_existedAccountIdPassed_sizeOfTransfersIs1(){
        Transfer transferRecord = new Transfer(transfer);
        when(mockedTransferDao.getTransfersByAccountId(source.getId())).thenReturn(List.of(transferRecord));
        Assert.assertEquals(List.of(transferRecord), accountService.getTransfersByAccountId(source.getId()));
    }

    @Test
    public void testTransfer_idIsNull_responseWithInvalidRequest() {
        TransferResponse response = accountService.transfer(null);
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_sourceAccountDoesNotExist_responseWithAccountDoesNotExistStatus(){
        when(mockedAccountDao.getAccountById(source.getId())).thenReturn(null);
        TransferRequest request = new TransferRequest(DEPOSIT, new BigDecimal(10), source.getId());
        TransferResponse response = accountService.transfer(request);
        verify(mockedAccountDao).getAccountById(source.getId());
        assertEquals(ACCOUNT_DOES_NOT_EXIST, response.getStatus());
    }

    @Test
    public void testTransfer_targetAccountDoesNotExist_responseWithTargetAccountDoesNotExistStatus(){
        when(mockedAccountDao.getAccountById(target.getId())).thenReturn(null);
        TransferResponse response = accountService.transfer(transfer);
        verify(mockedAccountDao).getAccountById(target.getId());
        assertEquals(TARGET_ACCOUNT_DOES_NOT_EXIST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestHasNoTargetAccountId_responseWithTargetAccountDoesNotExistStatus(){
        TransferRequest transfer = new TransferRequest(TRANSFER, new BigDecimal(10), source.getId(), null);
        TransferResponse response = accountService.transfer(transfer);
        verify(mockedAccountDao, never()).getAccountById(null);
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_targetAccountIsTheSame_responseWithTargetAccountIsTheSameStatus(){
        when(mockedAccountDao.getAccountById(source.getId())).thenReturn(source);
        TransferRequest request = new TransferRequest(TRANSFER, new BigDecimal(10), source.getId(), source.getId());
        TransferResponse response = accountService.transfer(request);
        verify(mockedAccountDao, times(2)).getAccountById(source.getId());
        assertEquals(TARGET_ACCOUNT_IS_THE_SAME, response.getStatus());
    }

    @Test
    public void testTransfer_sourceHasZeroAmount_responseWithInsufficientFunds(){
        doThrow(InsufficientFundsException.class).when(mockedAmountMover).moveAmountSynchronously(source, target, transfer.getAmount(),transfer.getAmount());
        TransferResponse response = accountService.transfer(transfer);
        verify(mockedAmountMover).moveAmountSynchronously(source, target, transfer.getAmount(), transfer.getAmount());
        assertEquals(INSUFFICIENT_FUNDS, response.getStatus());
    }

    @Test
    public void testTransfer_sourceHasEnoughAmount_responseWithSuccess(){
        source.setBalance(BigDecimal.valueOf(10));
        doNothing().when(mockedAmountMover).moveAmountSynchronously(source, target, transfer.getAmount(), transfer.getAmount());
        TransferResponse response = accountService.transfer(transfer);
        verify(mockedAmountMover).moveAmountSynchronously(source, target, transfer.getAmount(),transfer.getAmount());
        assertEquals(SUCCESS, response.getStatus());
    }

    @Test
    public void testWithdraw_sourceHasEnoughAmount_responseWithSuccess(){
        doNothing().when(mockedAmountMover).withdrawSynchronously(source, withdrawal.getAmount());
        TransferResponse response = accountService.transfer(withdrawal);
        verify(mockedAmountMover).withdrawSynchronously(source, withdrawal.getAmount());
        assertEquals(SUCCESS, response.getStatus());
    }

    @Test
    public void testWithdraw_amountMoverThrowsInsufficientFundsException_responseWithInsufficientFunds(){
        doThrow(InsufficientFundsException.class).when(mockedAmountMover).withdrawSynchronously(source, withdrawal.getAmount());
        TransferResponse response = accountService.transfer(withdrawal);
        verify(mockedAmountMover).withdrawSynchronously(source, withdrawal.getAmount());
        assertEquals(INSUFFICIENT_FUNDS, response.getStatus());
    }

    @Test
    public void testDeposit_amountMoverIsPerformedWithNoError_responseWithSuccessIsReturned(){
        doNothing().when(mockedAmountMover).depositSynchronously(source, deposit.getAmount());
        TransferResponse response = accountService.transfer(deposit);
        verify(mockedAmountMover).depositSynchronously(source, deposit.getAmount());
        assertEquals(SUCCESS, response.getStatus());
    }


    //check logic with calling currency service

    @Test
    public void testTransfer_currencyIsTheSame_currencyServiceIsNotCalled() {
        source.setBalance(BigDecimal.valueOf(100));
        doNothing().when(mockedAmountMover).moveAmountSynchronously(source, target, transfer.getAmount(), transfer.getAmount());
        accountService.transfer(transfer);
        verify(mockedCurrencyService, never()).convert(any(ConvertRequest.class));
    }

    @Test
    public void testTransfer_currenciesAreDifferent_currencyServiceIsCalled() {
        Account accountTo = new Account(Currency.GBP);
        TransferRequest request = new TransferRequest(TRANSFER, BigDecimal.valueOf(100), source.getId(), accountTo.getId());
        when(mockedAccountDao.getAccountById(accountTo.getId())).thenReturn(accountTo);
        ConvertRequest convertRequest = new ConvertRequest(Currency.USD, Currency.GBP, request.getAmount());
        accountService.transfer(request);
        verify(mockedCurrencyService).convert(convertRequest);
    }

    @Test
    public void testTransfer_currenciesAreDifferent_moveAmountIsCalledWithConvertedAmount() {
        Account accountTo = new Account(Currency.GBP);
        TransferRequest request = new TransferRequest(TRANSFER, BigDecimal.valueOf(100), source.getId(), accountTo.getId());
        when(mockedAccountDao.getAccountById(accountTo.getId())).thenReturn(accountTo);
        ConvertRequest convertRequest = new ConvertRequest(Currency.USD, Currency.GBP, request.getAmount());
        BigDecimal convertedAmount = BigDecimal.valueOf(70);
        when(mockedCurrencyService.convert(convertRequest)).thenReturn(convertedAmount);
        doNothing().when(mockedAmountMover).moveAmountSynchronously(source, accountTo, request.getAmount(), convertedAmount);
        accountService.transfer(request);
        verify(mockedAmountMover).moveAmountSynchronously(source, accountTo, request.getAmount(), convertedAmount);
    }

    private TransferRequest createTransferRequestFromJson(String requestJson){
        try {
            return objectMapper.readValue(requestJson, TransferRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * It is possible to avoid validation in TransferRequest constructor using creation via json and ObjectMapper.
     * Need to validate TransferRequest validation in accountService.transfer.
     */
    @Test
    public void testTransfer_transferRequestFromJsonWithNullType_responseWithInvalidRequest() {
        String requestJson = "{\"amount\" : 5, \"accountId\" : 1, \"targetAccountId\" : 2}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestFromJsonWithNullAmount_responseWithInvalidRequest() {
        String requestJson = "{\"type\" : \"WITHDRAWAL\", \"amount\" : null, \"accountId\" : 1, \"targetAccountId\" : 2}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestFromJsonWithNegativeAmount_responseWithInvalidRequest() {
        String requestJson = "{\"type\" : \"WITHDRAWAL\", \"amount\" : -5, \"accountId\" : 1, \"targetAccountId\" : 2}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestFromJsonWithNullAccount_responseWithInvalidRequest() {
        String requestJson = "{\"type\" : \"WITHDRAWAL\", \"amount\" : 5, \"accountId\" : null, \"targetAccountId\" : 2}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestFromJsonWithNullTargetAccount_responseWithInvalidRequest() {
        String requestJson = "{\"type\" : \"TRANSFER\", \"amount\" : 5, \"accountId\" : 1, \"targetAccountId\" : null}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

    @Test
    public void testTransfer_transferRequestFromJsonWithZeroAmount_responseWithInvalidRequest() {
        String requestJson = "{\"type\" : \"TRANSFER\", \"amount\" : 0, \"accountId\" : 1, \"targetAccountId\" : 2}";
        TransferResponse response = accountService.transfer(createTransferRequestFromJson(requestJson));
        assertEquals(INVALID_REQUEST, response.getStatus());
    }

}