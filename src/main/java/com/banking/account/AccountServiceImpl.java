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

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;

import static com.banking.model.TransferRequest.Type.*;
import static com.banking.model.TransferResponse.Status.*;
import static java.util.Objects.isNull;

public class AccountServiceImpl implements AccountService {

    @Inject
    private TransferDao transferDao;
    @Inject
    private AccountDao accountDao;
    @Inject
    private AmountMover amountMover;
    @Inject
    private CurrencyService currencyService;


    private BigDecimal calculateTargetAmount(Currency from, Currency to, BigDecimal amount) {
        if (!from.equals(to)) {
            return currencyService.convert(new ConvertRequest(from, to, amount));
        }
        return amount;
    }

    private TransferResponse performTransfer(Account from,
                                             Account to,
                                             TransferRequest request) {
        BigDecimal targetAmount = calculateTargetAmount(from.getCurrency(), to.getCurrency(), request.getAmount());
        try {
            amountMover.moveAmountSynchronously(from, to, request.getAmount(), targetAmount);
            //moveAmountSynch generates TransferNotification with new balance and details
            //TransferNotification is queued to the Account repository
            //and is persisted in data base in order of its generation
            transferDao.create(request);
            return new TransferResponse(SUCCESS);
        } catch (InsufficientFundsException e) {
            return new TransferResponse(INSUFFICIENT_FUNDS);
        }
    }

    private TransferResponse performWithdraw(Account account,
                                             TransferRequest transferRequest) {
        try {
            amountMover.withdrawSynchronously(account, transferRequest.getAmount());
            transferDao.create(transferRequest);
            return new TransferResponse(SUCCESS);
        } catch (InsufficientFundsException e) {
            return new TransferResponse(INSUFFICIENT_FUNDS);
        }

    }

    private TransferResponse performDeposit(Account account,
                                            TransferRequest transferRequest) {
        amountMover.depositSynchronously(account, transferRequest.getAmount());
        transferDao.create(transferRequest);
        return new TransferResponse(SUCCESS);
    }

    private boolean validate(TransferRequest request) {
        if (isNull(request) || isNull(request.getAccountId()) || isNull(request.getAmount()) ||
                isNull(request.getType()) || BigDecimal.ZERO.compareTo(request.getAmount()) >= 0 ||
                TRANSFER.equals(request.getType()) && isNull(request.getTargetAccountId())) {
            return false; //wrong request
        }
        return true;
    }

    @Override
    public TransferResponse transfer(TransferRequest transferRequest) {
        if (!validate(transferRequest)) {
            return new TransferResponse(INVALID_REQUEST);
        }
        Account account = accountDao.getAccountById(transferRequest.getAccountId());
        if (account == null) {
            return new TransferResponse(ACCOUNT_DOES_NOT_EXIST);
        }
        if (WITHDRAWAL.equals(transferRequest.getType())) {
            return performWithdraw(account, transferRequest);
        } else if (DEPOSIT.equals(transferRequest.getType())) {
            return performDeposit(account, transferRequest);
        }
        //TransferRequest.Type.TRANSFER: Transfer to another account case
        Account accountTo = accountDao.getAccountById(transferRequest.getTargetAccountId());
        if (accountTo == null) {
            return new TransferResponse(TARGET_ACCOUNT_DOES_NOT_EXIST);
        }
        if (account.getId().equals(accountTo.getId())) {
            return new TransferResponse(TARGET_ACCOUNT_IS_THE_SAME);
        }
        //TODO return error transfer type unrecognized
        return performTransfer(account, accountTo, transferRequest);
    }

    @Override
    public Account createAccount() {
        return accountDao.create();
    }

    @Override
    public Collection<Transfer> getTransfersByAccountId(Long accountId) {
        if (isNull(accountId)) {
            return Collections.EMPTY_LIST;
        }
        return transferDao.getTransfersByAccountId(accountId);
    }

    @Override
    public Account getAccountById(Long id) {
        if (isNull(id)) {
            throw new IllegalArgumentException("Account id can't be null");
        }
        return accountDao.getAccountById(id);
    }
}
