package com.banking.account;

import com.banking.account.exception.InsufficientFundsException;
import com.banking.model.Account;

import java.math.BigDecimal;


public class AmountMover {

    private boolean isAmountEnough(Account from, BigDecimal amount) {
        return from.getBalance().compareTo(amount) != -1;
    }

    public void moveAmount(Account from, Account to, BigDecimal amount) {
        moveAmount(from, to, amount, amount);
    }

    public void moveAmount(Account from, Account to, BigDecimal amountFrom, BigDecimal amountTo) {
        if (isAmountEnough(from, amountFrom)) {
            from.setBalance(from.getBalance().subtract(amountFrom));
            to.setBalance(to.getBalance().add(amountTo));
        } else {
            throw new InsufficientFundsException();
        }
    }

    public void moveAmountSynchronously(Account from, Account to, BigDecimal amountFrom, BigDecimal amountTo) {
        //resource ordering when locking to avoid deadlock
        if (from.getId().compareTo(to.getId()) == -1) {
            synchronized (from) {
                synchronized (to) {
                    moveAmount(from, to, amountFrom, amountTo);
                }
            }
        } else {
            synchronized (to) {
                synchronized (from) {
                    moveAmount(from, to, amountFrom, amountTo);
                }
            }
        }
    }

    public void moveAmountSynchronously(Account from, Account to, BigDecimal amount) {
        moveAmountSynchronously(from, to, amount, amount);
    }

    public void withdraw(Account account, BigDecimal amount) {
        if (isAmountEnough(account, amount)) {
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            throw new InsufficientFundsException();
        }
    }

    public void deposit(Account account, BigDecimal amount) {
        account.setBalance(account.getBalance().add(amount));
    }

    public void depositSynchronously(Account account, BigDecimal amount) {
        synchronized (account) {
            deposit(account, amount);
        }
    }

    public void withdrawSynchronously(Account account, BigDecimal amount) {
        synchronized (account) {
            withdraw(account, amount);
        }
    }


    /**
     * Synchronized wrapper for withdraw/deposit calling
     * @param account
     * @param amount
     * @param operation withdraw/deposit methods
     */

    /*public void changeBalanceSynchronously(Account account, BigDecimal amount, BiConsumer<Account, BigDecimal> operation) {
        synchronized (account) {
            operation.accept(account, amount);
        }
    }*/


}
