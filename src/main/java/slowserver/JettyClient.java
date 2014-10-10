/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package slowserver;

import org.eclipse.jetty.client.HttpClient;

import java.util.concurrent.TimeUnit;

public class JettyClient {

    private static final String URI = "http://localhost:" + Server.PORT + "/path";

    public static void main(String... args) throws Exception {
        HttpClient httpClient = new HttpClient();
        httpClient.start();
        System.out.println("Done: " + httpClient.newRequest(URI).timeout(10, TimeUnit.SECONDS).send());
        httpClient.stop();
    }
}
