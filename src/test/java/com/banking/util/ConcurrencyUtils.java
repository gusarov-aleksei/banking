package com.banking.util;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class ConcurrencyUtils {


    public static void executeAndWait(final Runnable task) {
        executeAndWait(List.of(task));
    }

    /**
     * Execute tasks in parallel way using ExecutorService 100 times and wait till it be finished
     * @param tasks
     */
    public static void executeAndWait(final Collection<Runnable> tasks) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.range(0,100).forEach((i)-> tasks.forEach(executor::execute));
        executor.shutdown();
        while (!executor.isTerminated()){ /* wait */ }
    }

    public static void wait(CountDownLatch latch){
        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    public static void sleep(long millis){
        try { Thread.sleep(millis); } catch (InterruptedException e) { e.printStackTrace(); }
    }

}
