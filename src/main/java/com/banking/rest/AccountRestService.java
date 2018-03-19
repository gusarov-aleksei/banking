package com.banking.rest;

import com.banking.account.AccountService;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/api/account/")
@Produces(MediaType.APPLICATION_JSON)
public class AccountRestService {

    @Inject
    private AccountService accountService;

    @POST
    @Path("transfer")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Response transfer(TransferRequest request) {
        return Response.status(Response.Status.OK).entity( accountService.transfer(request)).build();
    }

    @GET
    @Path("{id}")
    public Response getAccountById(@PathParam("id") Long id) {
        return Response.status(Response.Status.OK).entity(accountService.getAccountById(id)).build();
    }

    @GET
    @Path("create")
    public Response create() {
        return Response.status(Response.Status.OK).entity(accountService.createAccount()).build();
    }

    @GET
    @Path("{id}/transfers")
    public Response getTransfersById(@PathParam("id") Long id) {
        GenericEntity<Collection<Transfer>> entity =
                new GenericEntity<Collection<Transfer>>(accountService.getTransfersByAccountId(id)) {};
        return Response.status(Response.Status.OK).entity(entity).build();
    }

}
