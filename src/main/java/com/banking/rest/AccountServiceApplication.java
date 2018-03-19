package com.banking.rest;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class AccountServiceApplication {

    static final String BASE_URI = "http://localhost:9099/";


    public static void main(String[] args) throws IOException, java.net.URISyntaxException {
        HttpServer server = JdkHttpServerFactory.createHttpServer(
                new URI(BASE_URI), new AccountServiceResourceConfig());
        System.out.println("Press any key to stop the server...");
        System.in.read();
        server.stop(0);
    }

}
