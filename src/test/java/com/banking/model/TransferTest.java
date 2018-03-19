package com.banking.model;

import org.junit.Test;

import java.math.BigDecimal;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TransferTest {

    TransferRequest request = new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.TEN, Long.valueOf(1), Long.valueOf(2));


    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_nullTransferRequest_iexception() {
        new Transfer(null);
    }

    @Test
    public void testConstructor_nonNullTransferRequest_transferTypeEqualsRequestType() {
        Transfer transfer = new Transfer(request);
        assertThat(transfer.getType(), is(request.getType()));
    }

    @Test
    public void testConstructor_nonNullTransferRequest_transferAmountIsTakenFromRequest() {
        Transfer transfer = new Transfer(request);
        assertThat(transfer.getAmount(), is(request.getAmount()));
    }

    @Test
    public void testConstructor_nonNullTransferRequest_transferAccountIdIsTakenFromRequest() {
        Transfer transfer = new Transfer(request);
        assertThat(transfer.getAccountId(), is(request.getAccountId()));
    }

    @Test
    public void testConstructor_nonNullTransferRequest_transferTargetAccountIdIsTakenFromRequest() {
        Transfer transfer = new Transfer(request);
        assertThat(transfer.getTargetAccountId(), is(request.getTargetAccountId()));
    }

    @Test
    public void testEquals_idsAreDifferent_hashCodesAreDifferent() {
        Transfer transfer1 = new Transfer(request);
        Transfer transfer2 = new Transfer(request);
        assertThat(transfer1.hashCode() == transfer2.hashCode(), is(false));
    }


}
