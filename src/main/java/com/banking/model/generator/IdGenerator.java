package com.banking.model.generator;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private final AtomicLong counter = new AtomicLong();

    public Long getNextId() {
        return counter.incrementAndGet();
    }

}
