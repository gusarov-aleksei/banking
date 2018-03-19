package com.banking.model;

import org.junit.Test;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.*;
import static com.banking.model.TransferResponse.Status.*;

public class TransferResponseTest {

    @Test
    public void testStatus_statusIsSUCCESS_getCodeIs1000(){
        assertEquals(1000, SUCCESS.getCode());
    }

    @Test
    public void testStatus_statusIsACCOUNT_DOES_NOT_EXIST_getCodeIs4001(){
        assertEquals(4001, ACCOUNT_DOES_NOT_EXIST.getCode());
    }

    @Test
    public void tesStatus_ACCOUNT_DOES_NOT_EXIST_getReason(){
        assertThat(ACCOUNT_DOES_NOT_EXIST.getReason(), is("Account doesn't exist"));
    }


    @Test
    public void testStatus_statusIsINSUFFICIENT_FUNDS_getCodeIs4002(){
        assertEquals(4003, INSUFFICIENT_FUNDS.getCode());
    }

    @Test
    public void testStatus_statusIsTARGET_ACCOUNT_DOES_NOT_EXIST_getCodeIs4003(){
        assertEquals(4002, TARGET_ACCOUNT_DOES_NOT_EXIST.getCode());
    }

    @Test
    public void testStatus_statusIsTARGET_ACCOUNT_IS_THE_SAME_getCodeIs4004(){
        assertEquals(4004, TARGET_ACCOUNT_IS_THE_SAME.getCode());
    }

    @Test
    public void testStatus_statusIsINVALID_REQUEST_getCodeIs4006(){
        assertEquals(4006, INVALID_REQUEST.getCode());
    }

    @Test
    public void testStatus_statusIsINTERNAL_ERROR_getCodeIs5000(){
        assertEquals(5000, INTERNAL_ERROR.getCode());
    }

    @Test
    public void testConstructor_responseIsCreatedWithSomeStatus_getStatusReturnsTheSameValue() {
        TransferResponse response = new TransferResponse(SUCCESS);
        assertEquals(SUCCESS, response.getStatus());
        assertThat(response.getDate(), isA(Date.class));
    }

}
