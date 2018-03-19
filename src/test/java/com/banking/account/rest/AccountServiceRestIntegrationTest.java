package com.banking.account.rest;

import com.banking.currency.Currency;
import com.banking.model.Account;
import com.banking.model.Transfer;
import com.banking.model.TransferRequest;
import com.banking.model.TransferResponse;
import com.banking.rest.AccountServiceResourceConfig;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import static com.banking.model.TransferRequest.Type.DEPOSIT;
import static com.banking.model.TransferRequest.Type.TRANSFER;
import static com.banking.model.TransferResponse.Status.*;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.*;

//TODO  test data is not being removed after every test running. test doesn't work in parallel way test methods execution
/**
 * Integration tests of AccountServiceRest.
 * <p>
 * It initializes server and client and executes some requests.
 */
public class AccountServiceRestIntegrationTest {

    private static String SERVER_URI = "http://localhost:9099/";
    private static String ACCOUNT_API_URI = "http://localhost:9099/api/account";

    private static HttpServer server;
    private static Client client;

    @BeforeClass
    public static void initTransferService() throws IOException, URISyntaxException {
        server = JdkHttpServerFactory.createHttpServer(
                new URI(SERVER_URI),
                new AccountServiceResourceConfig()
        );
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void shutdownTransferService() throws IOException, URISyntaxException {
        client.close();
        server.stop(0);
    }

    private static WebTarget getClientTarget() {
        return client.target(ACCOUNT_API_URI);
    }

    private static boolean assertResponseIsOK(Response response) {
        return response.getStatus() == Response.Status.OK.getStatusCode();
    }

    /**
     * Positive case
     * <p>
     * Create account with initialized by default parameters
     * Check if account has been created
     * Check account default parameters
     */
    @Test
    public void testCreateAccount() {
        Response response = getClientTarget().path("create").request().get(Response.class);
        assertEquals("REST Response status code :",
                Response.Status.OK.getStatusCode(), response.getStatus());
        Account account = response.readEntity(Account.class);
        //check if account was created
        assertNotNull("Account ", account);
        assertNotNull("Account id ", account.getId());
        assertEquals("Account balance ", BigDecimal.ZERO, account.getBalance());
        assertEquals("Account currency ", Currency.USD, account.getCurrency());
        assertEquals("Account status ", Account.Status.ACTIVE, account.getStatus());
    }

    /**
     * Positive case
     * <p>
     * Create account in system.
     * Request account by id (http://localhost:9099/api/account/{accountId}/)
     * Check getting information about account.
     * request example http://localhost:9099/api/account/{accountId}/
     */
    @Test
    public void testGetAccount() {
        //create account
        Account account = getClientTarget().path("create").request().get(Account.class);
        //request account
        Response response = getClientTarget().path(account.getId().toString()).request().get(Response.class);
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        Account receiveAccount = response.readEntity(Account.class);
        assertNotNull("Account is null", receiveAccount);
        assertEquals("Account id :", account.getId(), receiveAccount.getId());
        assertEquals("Account balance :", account.getBalance(), receiveAccount.getBalance());
        assertEquals("Account currency :", account.getCurrency(), receiveAccount.getCurrency());
        assertEquals("Account status :", account.getStatus(), receiveAccount.getStatus());
    }

    /**
     * Negative case.
     * <p>
     * Get non non-existent account
     * Check if system respond correctly
     */
    @Test
    public void testGetAccountIfAccountDoesNotExist() {
        //request non-existent account
        Response response = getClientTarget().path("-1").request().get(Response.class);
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        assertNull("Nothing expected", response.readEntity(Account.class));
    }

    /**
     * Positive case
     * <p>
     * Create account in USD(currency by default).
     * Put 150 USD in account.
     * Check updated account balance.
     * Check transfer record created
     */
    @Test
    public void testDeposit() {
        //create account
        Account account = getClientTarget().path("create").request().get(Account.class);
        //make a deposit 150.00 in currency of account
        TransferRequest deposit = new TransferRequest(DEPOSIT, BigDecimal.valueOf(150.00), account.getId(), null);
        Response response = getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //check if server answered without internal error
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        TransferResponse transferResponse = response.readEntity(TransferResponse.class);
        //check if making a deposit is successful
        assertEquals("Deposit status :", SUCCESS, transferResponse.getStatus());
        //check balance
        response = getClientTarget().path(account.getId().toString()).request().get(Response.class);
        assertEquals("Account balance :", BigDecimal.valueOf(150.00), response.readEntity(Account.class).getBalance());
        //check transfer record
        Collection<Transfer> transfers = readTransfersFromResponse(requestGetTransfers(account.getId().toString()));
        assertEquals("Account transfer records :", 1, transfers.size());
        Transfer depositRecord = transfers.iterator().next();
        //depositRecord.getAmount();
        assertEquals("Deposit amount :", BigDecimal.valueOf(150.00), depositRecord.getAmount());
        assertEquals("Deposit to account reference :", account.getId(), depositRecord.getAccountId());
        assertEquals("Deposit type :", DEPOSIT, depositRecord.getType());
    }

    /**
     * Negative case
     * <p>
     * Don't create account
     * Make a deposit to non-existent account
     * Check response status
     */
    @Test
    public void testDepositIfNonExistentAccount() {
        TransferRequest deposit = new TransferRequest(DEPOSIT, BigDecimal.valueOf(150.00), Long.valueOf(-1), null);
        Response response = getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //check if server answered without internal error
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        TransferResponse transferResponse = response.readEntity(TransferResponse.class);
        //check if making a deposit is successful
        assertEquals("Deposit status :", ACCOUNT_DOES_NOT_EXIST, transferResponse.getStatus());
    }

    /**
     * Positive case
     * <p>
     * Create account in USD(currency by default).
     * Put 150 USD in account.
     * Withdraw 149.01 from account
     * Check updated account balance should be 0.99.
     */
    @Test
    public void testWithdrawIfSufficientFunds() {
        //create account
        Account account = getClientTarget().path("create").request(MediaType.APPLICATION_JSON).get(Account.class);
        //make a deposit
        TransferRequest deposit = new TransferRequest(DEPOSIT,
                BigDecimal.valueOf(150.00), account.getId(), null);
        getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));//APPLICATION_XML
        //make a withdrawal
        TransferRequest withdrawal = new TransferRequest(TransferRequest.Type.WITHDRAWAL,
                BigDecimal.valueOf(149.01), account.getId(), null);
        Response response = getClientTarget().path("transfer").
                request().post(Entity.entity(withdrawal, MediaType.APPLICATION_JSON));
        //check if server answered without internal error
        assertEquals("REST Response status code :",
                Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Withdraw status :",
                SUCCESS, response.readEntity(TransferResponse.class).getStatus());
        //check balance
        account = getClientTarget().path(account.getId().toString()).
                request().get(Account.class);
        assertEquals("Account balance :", BigDecimal.valueOf(0.99), account.getBalance());
    }


    /**
     * Negative case
     * <p>
     * Create account in USD(currency by default).
     * Put 150 USD in account.
     * Withdraw 150.01 from account
     * System don't allow user withdraw money
     */
    @Test
    public void testWithdrawIfInsufficientFunds() {
        //create account
        Account account = getClientTarget().path("create").request(MediaType.APPLICATION_JSON).get(Account.class);
        //make a deposit
        TransferRequest deposit = new TransferRequest(DEPOSIT,
                BigDecimal.valueOf(150.00), account.getId(), null);
        getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //make a withdrawal
        TransferRequest withdrawal = new TransferRequest(TransferRequest.Type.WITHDRAWAL,
                BigDecimal.valueOf(150.01), account.getId(), null);
        Response response = getClientTarget().path("transfer").
                request().post(Entity.entity(withdrawal, MediaType.APPLICATION_JSON));
        //check if server answered without internal error, check withdrawal result
        assertEquals("REST Response status code :",
                Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("Withdraw status :",
                INSUFFICIENT_FUNDS, response.readEntity(TransferResponse.class).getStatus());
    }

    /**
     * Positive case
     * <p>
     * Create source account in USD(currency by default).
     * Put 150 USD in account.
     * Create target account in USD(currency by default).
     * Transfer 10.00 from source account to target account
     * Check source and target balance
     * Check if transfer record created
     */
    @Test
    public void testTransferToAnotherAccountIfSufficientFunds() {
        //create source account
        Account account = getClientTarget().path("create").request().get(Account.class);
        //make a deposit to source
        TransferRequest deposit = new TransferRequest(DEPOSIT, BigDecimal.valueOf(150.00), account.getId(), null);
        getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //create target account
        Account targetAccount = getClientTarget().path("create").request().get(Account.class);
        //make a 10.00 transfer to target
        TransferRequest transfer = new TransferRequest(TRANSFER, BigDecimal.valueOf(10.00), account.getId(), targetAccount.getId());
        Response response = getClientTarget().path("transfer").request().
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON));
        //check if server answered without internal error, check withdrawal result
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        assertEquals("Withdraw status :",
                SUCCESS, response.readEntity(TransferResponse.class).getStatus());
        //check account and target account balance
        account = getClientTarget().path(account.getId().toString()).request().get(Account.class);
        assertEquals("Account balance :",
                BigDecimal.valueOf(140.00), account.getBalance());
        targetAccount = getClientTarget().path(targetAccount.getId().toString()).request().get(Account.class);
        assertEquals("Target account balance :", BigDecimal.valueOf(10.00), targetAccount.getBalance());
        //transfer record should be created
        //request example http://localhost:9099/api/account/f37ec11f-7fb7-4cc2-84be-090dc7fc3b5d/transfers
        response = requestGetTransfers(account.getId().toString());
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        Collection<Transfer> transfers = readTransfersFromResponse(response);
        assertEquals("Account transfer records :", 2, transfers.size());
    }

    private Response requestGetTransfers(String accountId) {
        return getClientTarget().path(accountId).path("transfers").request().get(Response.class);
    }

    private static Collection<Transfer> readTransfersFromResponse(Response response) {
        return response.readEntity(new GenericType<Collection<Transfer>>() {
        });
    }

    /**
     * Negative case
     * <p>
     * Create source account
     * Make a deposit with 150 amount
     * Check transfer record (must be 1)
     * Create target account
     * Make a transfer to target account with 160.00 amount
     * System doesn't allow transfer
     * Transfer record isn't created for source account
     */
    @Test
    public void testTransferToAnotherAccountIfInsufficientFunds() {
        //create source account
        Account account = getClientTarget().path("create").request().get(Account.class);
        //make a deposit to source
        TransferRequest deposit = new TransferRequest(DEPOSIT, BigDecimal.valueOf(150.00), account.getId(), null);
        getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //check if transfer record is created
        Collection<Transfer> transfers = readTransfersFromResponse(requestGetTransfers(account.getId().toString()));
        assertEquals("Account transfer records :", 1, transfers.size());
        //create target account
        Account targetAccount = getClientTarget().path("create").request().get(Account.class);
        //make a 160.00 transfer to target
        TransferRequest transfer = new TransferRequest(TRANSFER, BigDecimal.valueOf(160.00), account.getId(), targetAccount.getId());
        Response response = getClientTarget().path("transfer").request().
                post(Entity.entity(transfer, MediaType.APPLICATION_JSON));
        //check if server answered without internal error, check withdrawal result
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        assertEquals("Withdraw status :", INSUFFICIENT_FUNDS, response.readEntity(TransferResponse.class).getStatus());
        //check transfer record isn't created
        transfers = readTransfersFromResponse(requestGetTransfers(account.getId().toString()));
        assertEquals("Account transfer records :", 1, transfers.size());
    }

    /**
     * Negative case
     * <p>
     * Create account in USD(currency by default).
     * Put 150 USD in account.
     * Make a transfer to non-existent target account
     * Check correct response
     * Source account balance should be unchanged
     */
    @Test
    public void testTransferToNonExistentAccount() {
        //create source account
        Account account = getClientTarget().path("create").request().get(Account.class);
        //make a deposit to source
        TransferRequest deposit = new TransferRequest(DEPOSIT, BigDecimal.valueOf(150.00), account.getId(), null);
        getClientTarget().path("transfer").request().post(Entity.entity(deposit, MediaType.APPLICATION_JSON));
        //make a 10.00 transfer to target
        TransferRequest transfer = new TransferRequest(TRANSFER, BigDecimal.valueOf(10.00), account.getId(), Long.valueOf(-1));
        Response response = getClientTarget().path("transfer").request().post(Entity.entity(transfer, MediaType.APPLICATION_JSON));
        //check if server answered without internal error, check withdrawal result
        assertEquals("REST Response status code :", OK.getStatusCode(), response.getStatus());
        assertEquals("Withdraw status :",
                TARGET_ACCOUNT_DOES_NOT_EXIST, response.readEntity(TransferResponse.class).getStatus());
        //check source account balance wasn't changed
        account = getClientTarget().path(account.getId().toString()).request().get(Account.class);
        assertEquals("Account balance :", BigDecimal.valueOf(150.00), account.getBalance());
    }

    @Test
    public void testTransferWithNullTransferRequest() {
        TransferRequest transferRequest = null;
        Response response = getClientTarget().path("transfer").request().post(Entity.entity(transferRequest, MediaType.APPLICATION_JSON));
        assertEquals(INVALID_REQUEST, response.readEntity(TransferResponse.class).getStatus());
    }

}
