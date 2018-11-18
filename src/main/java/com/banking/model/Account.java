package com.banking.model;


import com.banking.currency.Currency;
import com.banking.model.generator.IdGenerator;

import java.math.BigDecimal;

/**
 * Entity represents account of customer (kind of wallet).
 * In model it refers to <Customer> entity (many to one relationship).
 * In scope of this solution <Customer> entity was omitted.
 */
public class Account {

    private Long id;

    private BigDecimal balance;

    private Status status;

    private Currency currency;

    private static final IdGenerator idGenerator = new IdGenerator();


    //TODO add limit oneTimeLimitAmount dailyLimit

    public Account() {
        id = idGenerator.getNextId();;
        balance = BigDecimal.ZERO;
        status = Status.ACTIVE;
        currency = Currency.USD; //assign currency by default
    }

    public Account(Currency currency) {
        id = idGenerator.getNextId();;
        balance = BigDecimal.ZERO;
        status = Status.ACTIVE;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(final BigDecimal balance) {
        this.balance = balance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return getId().equals(account.getId());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + balance.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + currency.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", balance=" + balance +
                ", status=" + status +
                ", currency=" + currency +
                '}';
    }

    public enum Status {
        ACTIVE, SUSPENDED, DEACTIVATED
    }

}
