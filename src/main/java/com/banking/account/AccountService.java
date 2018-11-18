package com.banking.account;


import com.banking.model.Account;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import com.banking.model.TransferResponse;

import java.util.Collection;

public interface AccountService {

    TransferResponse transfer(TransferRequest transferRequest);

    Account getAccountById(Long id);

    Account createAccount();

    Collection<Transfer> getTransfersByAccountId(Long accountId);

}
