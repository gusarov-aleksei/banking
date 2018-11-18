package com.banking.dao;

import com.banking.model.Account;

public interface AccountDao {

    Account getAccountById(Long id);

    Account create();


}
