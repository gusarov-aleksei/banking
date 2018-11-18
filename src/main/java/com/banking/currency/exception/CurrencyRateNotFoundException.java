package com.banking.currency.exception;

public class CurrencyRateNotFoundException extends RuntimeException {

    public CurrencyRateNotFoundException(String message) {
        super(message);
    }

}
