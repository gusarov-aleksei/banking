package com.banking.model;


import com.banking.model.generator.IdGenerator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import static java.util.Objects.*;

/**
 * Entity represents account balance changing.
 */
public class Transfer implements Serializable {

    private Long id;

    private TransferRequest.Type type;

    private BigDecimal amount;

    private Long accountId;

    private Long targetAccountId;

    private Date date;

    private static final IdGenerator idGenerator = new IdGenerator();

    private Transfer() {
    }

    public Transfer(TransferRequest request) {
        if(isNull(request)) {
            throw new IllegalArgumentException("TransferRequest can't be null");
        }
        this.type = request.getType();
        this.amount = request.getAmount();
        this.accountId = request.getAccountId();
        this.targetAccountId = request.getTargetAccountId();
        date = new Date();//TODO consider immutable type
        this.id = idGenerator.getNextId();
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public TransferRequest.Type getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transfer transfer = (Transfer) o;

        return getId().equals(transfer.getId());
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + accountId.hashCode();
        result = 31 * result + (targetAccountId != null ? targetAccountId.hashCode() : 0);
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id='" + id + '\'' +
                '}';
    }

}
