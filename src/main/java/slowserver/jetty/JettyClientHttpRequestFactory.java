/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package slowserver.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.Assert.notNull;

/**
 */
public class JettyClientHttpRequestFactory implements ClientHttpRequestFactory, InitializingBean, DisposableBean {

    private final HttpClient httpClient;
    private TimeUnit timeoutUnit = TimeUnit.SECONDS;
    private int timeout = 0;

    public JettyClientHttpRequestFactory(final HttpClient httpClient) {
        notNull(httpClient, "httpClient can't be null");
        this.httpClient = httpClient;
    }

    @Override
    public ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod) throws IOException {
        final Request request = httpClient.newRequest(uri)
                .method(httpMethod.toString());
        if (timeout > 0) {
           request.timeout(timeout, timeoutUnit);
        }
        return new JettyClientHttpRequest(httpClient, request);
    }

    public void setTimeoutUnit(final String timeoutUnit) {
        notNull(timeoutUnit, "timeoutUnit can't be null");
        this.timeoutUnit = TimeUnit.valueOf(timeoutUnit.toUpperCase());
    }

    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void destroy() throws Exception {
        httpClient.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        httpClient.start();
    }
}
