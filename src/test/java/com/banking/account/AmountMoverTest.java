package com.banking.account;

import com.banking.account.exception.InsufficientFundsException;
import com.banking.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;


public class AmountMoverTest {
    //TODO move to junit 5.0

    private Account accountFrom = new Account();
    private Account accountTo = new Account();
    private AmountMover amountMover = new AmountMover();

    @Before
    public void setUp() {
        accountFrom.setBalance(BigDecimal.valueOf(1000));
        accountTo.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    public void testMoveAmount_transfer100FromAccount1000_fromAccountBalanceIs900() {
        amountMover.moveAmount(accountFrom, accountTo, BigDecimal.valueOf(100));
        Assert.assertEquals(BigDecimal.valueOf(900), accountFrom.getBalance());
    }

    @Test
    public void testMoveAmount_transfer100FromAccount1000_toAccountBalanceIs1100() {
        amountMover.moveAmount(accountFrom, accountTo, BigDecimal.valueOf(100));
        Assert.assertEquals(BigDecimal.valueOf(1100), accountTo.getBalance());
    }

    @Test
    public void testMoveAmount_get100FromAccountAndPut130ToAnother_fromAccountBalanceIs900() {
        //when amountTo<>amountFrom it means different currencies
        amountMover.moveAmount(accountFrom, accountTo, BigDecimal.valueOf(100), BigDecimal.valueOf(130));
        Assert.assertEquals(BigDecimal.valueOf(900), accountFrom.getBalance());
    }

    @Test
    public void testMoveAmount_get100FromAccountAndPut130ToAnother_fromAccountBalanceIs1130() {
        //when amountTo<>amountFrom it means different currencies
        amountMover.moveAmount(accountFrom, accountTo, BigDecimal.valueOf(100), BigDecimal.valueOf(130));
        Assert.assertEquals(BigDecimal.valueOf(1130), accountTo.getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void testMoveAmount_transfer100FromAccountWith90_throwsInsufficientFunds() {
        accountFrom.setBalance(BigDecimal.valueOf(90));
        amountMover.moveAmount(accountFrom, accountTo, BigDecimal.valueOf(100));
    }

    @Test
    public void testWithdraw_withdraw10FromAccountWith90_balanceBecomes80() {
        accountFrom.setBalance(BigDecimal.valueOf(90));
        amountMover.withdraw(accountFrom, BigDecimal.valueOf(10));
        assertEquals(BigDecimal.valueOf(80),accountFrom.getBalance());
    }

    @Test(expected = InsufficientFundsException.class)
    public void testWithdraw_withdraw100FromAccountWith90_throwsInsufficientFunds() {
        accountFrom.setBalance(BigDecimal.valueOf(90));
        amountMover.withdraw(accountFrom, BigDecimal.valueOf(100));
    }

    @Test
    public void testDeposit_deposit6point49ToAccountWith10point83_balanceBecomes17point32() {
        //10.83 + 6.49 = 17.32
        accountFrom.setBalance(BigDecimal.valueOf(10.83));
        amountMover.deposit(accountFrom, BigDecimal.valueOf(6.49));
        assertEquals(BigDecimal.valueOf(17.32), accountFrom.getBalance());
    }

}
