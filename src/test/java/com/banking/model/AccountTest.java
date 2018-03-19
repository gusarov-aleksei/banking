package com.banking.model;

import com.banking.currency.Currency;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class AccountTest {

    private Account account = new Account();

    @Test
    public void testConstructor_noParameters_defaultBalanceIsZero(){
        assertEquals(BigDecimal.ZERO, account.getBalance());
    }

    @Test
    public void testConstructor_noParameters_defaultCurrencyIsUsd(){
        assertEquals(Currency.USD, account.getCurrency());
    }

    @Test
    public void testConstructor_noParameters_defaultStatusIsActive(){
        assertEquals(Account.Status.ACTIVE, account.getStatus());
    }

    @Test
    public void testConstructor_noParameters_IdIsNotNull(){
        assertNotNull(account.getId());
    }

    @Test
    public void testEquals_accountsAreTheSame_equalsReturnsTrue(){
        assertEquals(true, account.equals(account));
    }

    @Test
    public void testEquals_idsAreDifferent_equalsReturnsFalse(){
        assertFalse( account.equals(new Account()));
    }

    @Test
    public void testEquals_accountIsNull_equalsReturnsFalse(){
        assertFalse( account.equals(null));
    }

    @Test
    public void testEquals_idsAreTheSame_equalsReturnsTrue(){
        Account account = new AccountMockedGetId();
        Account other = new AccountMockedGetId();
        assertEquals(true, account.equals(other));
        assertEquals(true, other.equals(account));
    }

    @Test
    public void testHashCode_allFieldsAreTheSame_hashCodeIsTheSame(){
        Account account = new AccountMockedGetId();
        Account other = new AccountMockedGetId();
        assertEquals(account.hashCode(), other.hashCode());
    }

    @Test
    public void testHashCode_idsAreDifferent_hashCodesAreDifferent(){
        Account other = new Account();
        assertNotEquals(account.hashCode(), other.hashCode());
    }

    //account.getId returns constant value for equals, hashcode check
    class AccountMockedGetId extends Account {
        @Override
        public Long getId() { return Long.valueOf(1); }
    }

}
