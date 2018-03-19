package com.banking.dao;

import com.banking.model.Transfer;
import com.banking.model.TransferRequest;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

public class TransferDaoImpl implements TransferDao {

    @Inject
    private BankingDataStorage bankingDataStorage;

    @Override
    public Transfer create(final TransferRequest transferRequest) {
        Transfer transfer = new Transfer(transferRequest);
        save(transfer);
        return transfer;
    }

    private void save(final Transfer transfer) {
        bankingDataStorage.storeTransfer(transfer);
    }

    @Override
    public Collection<Transfer> getTransfersByAccountId(final Long accountId) {
        if (accountId == null) {
            return List.of();
        }
        return bankingDataStorage.getTransfersByAccountId(accountId);
    }


}
