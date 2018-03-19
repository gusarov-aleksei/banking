package com.banking.model;

import org.junit.Test;

import java.math.BigDecimal;

import static com.banking.model.TransferRequest.Type.DEPOSIT;
import static com.banking.model.TransferRequest.Type.TRANSFER;
import static com.banking.model.TransferRequest.Type.WITHDRAWAL;
import static org.junit.Assert.assertEquals;

public class TransferRequestTest {

    TransferRequest request = new TransferRequest(TRANSFER, BigDecimal.valueOf(100), Long.valueOf(1), Long.valueOf(2));

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_typeIsNull_throwsIllegalArgumentException(){
        new TransferRequest(null, BigDecimal.valueOf(100), Long.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_amountIsNull_throwsIllegalArgumentException(){
        new TransferRequest(DEPOSIT, null, Long.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_amountIsNegative_throwsIllegalArgumentException(){
        new TransferRequest(DEPOSIT, new BigDecimal("-100"), Long.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_amountIsZero_throwsIllegalArgumentException(){
        new TransferRequest(WITHDRAWAL, BigDecimal.ZERO, Long.valueOf(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_accountIdIsNull_throwsIllegalArgumentException(){
        new TransferRequest(DEPOSIT, BigDecimal.valueOf(100), null);
    }

    @Test
    public void testConstructor_targetAccountIdIsNotPassed_getTargetAccountIdReturnsNull(){
        TransferRequest request = new TransferRequest(DEPOSIT, BigDecimal.valueOf(100), Long.valueOf(1));
        assertEquals(null, request.getTargetAccountId());
    }

    @Test
    public void testConstructor_targetAccountIdIsNull_getTargetAccountIdReturnsNull(){
        TransferRequest request = new TransferRequest(DEPOSIT, BigDecimal.valueOf(100), Long.valueOf(1), null);
        assertEquals(null, request.getTargetAccountId());
    }

    @Test
    public void testConstructor_typeIsTRANSFER_getTypeReturnsTRANSFER(){
        assertEquals(TRANSFER, request.getType());
    }

    @Test
    public void testConstructor_amountIs100_getAmountReturns100(){
        assertEquals(BigDecimal.valueOf(100), request.getAmount());
    }

    @Test
    public void testConstructor_accountIdIs1_getAccountIdReturns1(){
        assertEquals(Long.valueOf(1), request.getAccountId());
    }

    @Test
    public void testConstructor_targetAccountIdIs1_getTargetAccountIdReturns1(){
        assertEquals(Long.valueOf(2), request.getTargetAccountId());
    }

}
