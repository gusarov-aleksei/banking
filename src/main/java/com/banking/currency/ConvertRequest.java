package com.banking.currency;

import java.math.BigDecimal;
import java.util.Objects;

public class ConvertRequest {

    private Currency source;
    private Currency target;
    private BigDecimal amount;


    public ConvertRequest(Currency source, Currency target, BigDecimal amount) {
        if (Objects.isNull(source)) {
            throw new IllegalArgumentException("Source currency can't be null");
        }
        if (Objects.isNull(target)) {
            throw new IllegalArgumentException("Target currency can't be null");
        }
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException("Source amount can't be null");
        }
        this.source = source;
        this.target = target;
        this.amount = amount;
    }

    public Currency getSource() {
        return source;
    }

    public Currency getTarget() {
        return target;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConvertRequest that = (ConvertRequest) o;

        if (source != that.source) return false;
        if (target != that.target) return false;
        return amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + amount.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConvertRequest{" +
                "source=" + source +
                ", target=" + target +
                ", amount=" + amount +
                '}';
    }
}
