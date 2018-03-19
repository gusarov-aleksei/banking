package com.banking.dao;


import com.banking.model.Transfer;
import com.banking.model.TransferRequest;

import java.util.Collection;

public interface TransferDao {

    Transfer create(TransferRequest transferRequest);

    /**
     * Returns all transfer records. It is reasonable to add filter by date, count of records, etc.
     *
     * @param accountId
     * @return all transfer records regarding to account
     */
    Collection<Transfer> getTransfersByAccountId(Long accountId);

}
