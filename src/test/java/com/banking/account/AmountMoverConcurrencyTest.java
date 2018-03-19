package com.banking.account;

import com.banking.account.exception.InsufficientFundsException;
import com.banking.model.Account;
import com.banking.util.ConcurrencyUtils;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import static com.banking.util.ConcurrencyUtils.executeAndWait;
import static org.junit.Assert.assertEquals;

/**
 * Concurrency validation tests.
 */
public class AmountMoverConcurrencyTest {

    private Account accountFrom = new Account();
    private Account accountTo = new Account();
    private AmountMover amountMover = new AmountMover();

    /**
     * Test detects deadlock if it exists inside moveAmountSynchronously
     * It also validate concurrent access to Account shared data
     */
    @Test(timeout = 3000)
    public void testMoveAmountSynchronously_mutualTransferConcurrentlyManyTimes_sourceBalanceIs8000() {
        accountFrom.setBalance(new BigDecimal(8000));
        accountTo.setBalance(new BigDecimal(8000));
        //transfer 5*10*100 amount from source to target
        Runnable directTransfer = () -> {
            for (int i=0; i<10; i++)
                amountMover.moveAmountSynchronously(accountFrom, accountTo, new BigDecimal(5));
        };
        //transfer 5*10*100 amount back from target to source
        Runnable backwardTransfer = () -> {
            for (int i=0; i<10; i++)
                amountMover.moveAmountSynchronously(accountTo, accountFrom, new BigDecimal(5));
        };

        executeAndWait(List.of(directTransfer, backwardTransfer));
        assertEquals(new BigDecimal(8000), accountFrom.getBalance());
    }

    @Test(timeout = 3000)
    public void testMoveAmountSynchronously_mutualTransferConcurrentlyManyTimes_targetBalanceIs8000() {
        accountFrom.setBalance(new BigDecimal(8000));
        accountTo.setBalance(new BigDecimal(8000));
        //transfer 4*10*100 amount from source to target
        Runnable directTransfer = () -> {
            for (int i=0; i<10; i++) {
                amountMover.moveAmountSynchronously(accountFrom, accountTo, new BigDecimal(4));
            }
        };
        //transfer 4*10*100 amount back from target to source
        Runnable backwardTransfer = () -> {
            for (int i=0; i<10; i++) {
                amountMover.moveAmountSynchronously(accountTo, accountFrom, new BigDecimal(4));
            }
        };
        ConcurrencyUtils.executeAndWait(List.of(directTransfer, backwardTransfer));
        Assert.assertEquals(new BigDecimal(8000), accountTo.getBalance());
    }

    @Test
    public void testChangeBalanceSynchronously_withdrawAndDepositConcurrentlyManyTimesForAccountWith20000_accountBalanceIs20000(){
        accountFrom.setBalance(BigDecimal.valueOf(20000));

        Runnable withdrawOperations = () -> {
            for (int i=0; i<10; i++)
                amountMover.withdrawSynchronously(accountFrom, BigDecimal.valueOf(8));
        };

        Runnable depositOperations = () -> {
            for (int i=0; i<10; i++)
                amountMover.depositSynchronously(accountFrom, BigDecimal.valueOf(8));
        };

        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int j = 0; j <  100; j++) {
            executor.execute(withdrawOperations);
            executor.execute(depositOperations);
        }
        executor.shutdown();
        while (!executor.isTerminated()){ /* wait */ }

        assertEquals(BigDecimal.valueOf(20000), accountFrom.getBalance());
    }

    @Test
    public void testDepositSynchronously_accountIsLockedByThread_queueWaitsForAccountBeReleased() {
        //IntStream.range(0, 50).forEach((i) -> testChangeBalanceSynchronously(amountMover::depositSynchronously));
        testChangeBalanceSynchronously(amountMover::depositSynchronously);
    }

    @Test
    public void testWithdrawSynchronously_accountIsLockedByThread_queueWaitsForAccountBeReleased() {
        accountTo.setBalance(BigDecimal.valueOf(1010));
        testChangeBalanceSynchronously(amountMover::withdrawSynchronously);
        /*for (int i=0; i<50; i++) {
            accountTo.setBalance(BigDecimal.valueOf(1010));
            testChangeBalanceSynchronously(amountMover::withdrawSynchronously);
        }*/
    }

    /**
     *
     * check if every call of withdraw/deposit is synchronized by accountTo object
     * main point: inside sync section CompletedTaskCount is not changed
     * seems like test is redundant and not determined from visibility of executor parameters.
     * sometimes executor.getQueue() parameters(size()) are changed inside sync section because queue isn't sync
     * @param operation withdraw/deposit
     */
    private void testChangeBalanceSynchronously(BiConsumer<Account, BigDecimal> operation) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(1);//for temporary disabling threads
        Runnable changeBalanceTask = () -> {
            ConcurrencyUtils.wait(latch);//make thread wait
            ConcurrencyUtils.sleep(1);
            operation.accept(accountTo, BigDecimal.valueOf(1));
        };
        IntStream.range(0, 1000).forEach((i) -> executor.execute(changeBalanceTask));
        while (executor.getQueue().size() < 990) { } //wait till all tasks be in queue
        latch.countDown();//go progress. enable all threads
        ConcurrencyUtils.sleep(2);
        synchronized (accountTo) {
            //it locks accountTo and checks queue size isn't changing (it means threads with depositSynchronously/withdrawSynchronously are waiting for accountTo becomes released)
            ConcurrencyUtils.sleep(5);
            long completedTasks = executor.getCompletedTaskCount();
            //System.out.println("queue = "+ executor.getQueue().size());
            //System.out.println("completed = " +executor.getCompletedTaskCount());
            //System.out.println("task = "+ executor.getTaskCount());
            ConcurrencyUtils.sleep(100);
            //System.out.println("queue = "+ executor.getQueue().size());
            //System.out.println("completed = " +executor.getCompletedTaskCount());
            //System.out.println("task = "+ executor.getTaskCount());
            Assert.assertEquals(completedTasks, executor.getCompletedTaskCount());

        }
        executor.shutdown();
        while (!executor.isTerminated()){ /* wait */ }
    }

    @Test
    public void testDepositSynchronously_makeDepositConcurrently_accountBalanceIs3000() {
        accountTo.setBalance(BigDecimal.valueOf(20000));

        Runnable depositTasks = () -> {
            for (int i=0; i<10; i++)
                amountMover.depositSynchronously(accountTo, BigDecimal.valueOf(10));
        };

        executeAndWait(List.of(depositTasks));
        assertEquals(BigDecimal.valueOf(30000), accountTo.getBalance());
    }

    @Test
    public void testWithdrawSynchronously_makeWithdrawConcurrentlyWhenInsufficientFunds_accountBalanceIs4() {
        accountTo.setBalance(BigDecimal.valueOf(5004));
        Runnable depositTasks = () -> {
            for (int i=0; i<10; i++)
                try {
                    amountMover.withdrawSynchronously(accountTo, BigDecimal.valueOf(10));
                } catch(InsufficientFundsException e) {
                    /* a lot of exceptions are thrown here in this test. do nothing */
                }
        };
        executeAndWait(List.of(depositTasks));
        assertEquals(BigDecimal.valueOf(4), accountTo.getBalance());
    }

}
