package com.banking.rest.exception;

import com.banking.model.TransferResponse;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UncaughtInternalException extends Throwable implements ExceptionMapper<Throwable> {

    private static final long serialVersionUID = 1L;

    @Override
    public Response toResponse(final Throwable exception) {
        //log exception and do not pass details into response
        //System.out.println(exception);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                entity(new TransferResponse(TransferResponse.Status.INTERNAL_ERROR)).build();
    }

}