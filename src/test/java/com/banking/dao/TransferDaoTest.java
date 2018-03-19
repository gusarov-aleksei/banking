package com.banking.dao;

import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.*;


public class TransferDaoTest {

    @Mock
    private BankingDataStorage bankingDataStorage;
    @InjectMocks
    private TransferDaoImpl transferDao;

    TransferRequest request = new TransferRequest(TransferRequest.Type.DEPOSIT, BigDecimal.TEN, Long.valueOf(1), Long.valueOf(2));
    Transfer transferRecord = new Transfer(request);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_nullIsPassed_throwsIllegalArgumentException(){
        transferDao.create(null);
    }

    @Test
    public void testCreate_transferRequestIsPassed_transferRecordIsCreatedAndStored(){
        doNothing().when(bankingDataStorage).storeTransfer(any(Transfer.class));
        Transfer transfer = transferDao.create(request);
        verify(bankingDataStorage).storeTransfer(transfer);
    }

    @Test
    public void testGetTransfersByAccountId_nonNullIsPassed_callIsDelegatedToStorage(){
        when(bankingDataStorage.getTransfersByAccountId(Long.valueOf(1))).thenReturn(List.of(transferRecord));
        Collection<Transfer> transfers = transferDao.getTransfersByAccountId(Long.valueOf(1));
        verify(bankingDataStorage).getTransfersByAccountId(any(Long.class));
        assertThat(transfers, containsInAnyOrder(transferRecord));
    }

    @Test
    public void testGetTransfersByAccountId_nullIsPassed_callIsNotDelegatedToStorage(){
        Collection<Transfer> transfers = transferDao.getTransfersByAccountId(null);
        verify(bankingDataStorage, never()).getTransfersByAccountId(any());
        assertThat(transfers, containsInAnyOrder());
    }


}
