package com.banking.model.generator;

import com.banking.util.ConcurrencyUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IdGeneratorTest {

    private IdGenerator idGenerator = new IdGenerator();

    @Test
    public void testGetNextId_whenNewlyIdGeneratorCreated_getNextIdReturns1(){
        assertEquals(Long.valueOf(1), idGenerator.getNextId());
    }

    @Test
    public void testIdGetNextId_when10000timesRequested_getNextIdReturns10001(){
        ConcurrencyUtils.executeAndWait(() -> {for(int i = 0; i<100; i++) idGenerator.getNextId();});
        assertEquals(Long.valueOf(10001), idGenerator.getNextId());
    }

}
