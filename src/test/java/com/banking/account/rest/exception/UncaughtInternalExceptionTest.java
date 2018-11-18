package com.banking.account.rest.exception;

import com.banking.model.TransferResponse;
import com.banking.rest.exception.UncaughtInternalException;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.*;
import static com.banking.model.TransferResponse.Status.*;

public class UncaughtInternalExceptionTest {

    private UncaughtInternalException uncaughtInternalException = new UncaughtInternalException();

    @Test
    public void testToResponse_throwableIsPassed_responseWithInternalServerErrorIsReturned(){
        Response response = uncaughtInternalException.toResponse(new Throwable());
        Assert.assertEquals(INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testToResponse_throwableIsPassed_transferResponseWithInternalErrorIsReturned(){
        Response response = uncaughtInternalException.toResponse(new Throwable());
        Assert.assertEquals(INTERNAL_ERROR, ((TransferResponse)response.getEntity()).getStatus());
    }

}
