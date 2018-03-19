package com.banking.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

import static java.util.Objects.isNull;


@XmlRootElement
public class TransferRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Type type;

    private BigDecimal amount;

    private Long accountId;

    private Long targetAccountId;

    private TransferRequest() {
    }

    public TransferRequest(Type type, BigDecimal amount, Long accountId) {
        this(type, amount, accountId, null);
    }

    public TransferRequest(Type type, BigDecimal amount, Long accountId, Long targetAccountId) {
        if (isNull(type)) {
            throw new IllegalArgumentException("Type must be not null");
        }
        if (isNull(amount) || BigDecimal.ZERO.compareTo(amount) >= 0) {
            throw new IllegalArgumentException("Amount is expected greater than 0");
        }
        if (isNull(accountId)) {
            throw new IllegalArgumentException("AccountId must be not null");
        }
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.targetAccountId = targetAccountId;//optional, required for Type.TRANSFER
    }

    public Type getType() {
        return type;
    }

    public BigDecimal getAmount() { return amount; }

    public Long getAccountId() {
        return accountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    @Override
    public String toString() {
        return "TransferRequest{" +
                "type=" + type +
                ", amount=" + amount +
                ", accountId='" + accountId + '\'' +
                ", targetAccountId='" + targetAccountId + '\'' +
                '}';
    }

    public enum Type {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
}
