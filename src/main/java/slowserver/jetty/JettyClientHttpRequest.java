/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package slowserver.jetty;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static org.springframework.util.Assert.notNull;

/**
 */
public class JettyClientHttpRequest extends AbstractClientHttpRequest {

    private final HttpClient httpClient;
    // todo test request with body
    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream();

    private final ContentExchange request;

    JettyClientHttpRequest(final HttpClient httpClient, final ContentExchange request) {
        notNull(httpClient, "httpClient can't be null");
        notNull(request, "request can't be null");
        this.httpClient = httpClient;
        this.request = request;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod());
    }

    @Override
    public URI getURI() {
        return URI.create(request.getRequestURI());
    }

    @Override
    protected OutputStream getBodyInternal(final HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    @Override
    protected ClientHttpResponse executeInternal(final HttpHeaders headers) throws IOException {
        request.setRequestContent(new ByteArrayBuffer(bufferedOutput.toByteArray()));
        httpClient.send(request);
        try {
            final int status = request.waitForDone();
            if (status == HttpExchange.STATUS_COMPLETED) {
                return new JettyClientHttpResponse(request);
            } else {
                throw new IOException("Invalid status: " + status);
            }
        } catch (InterruptedException e) {
            throw new IOException("interrupted", e);
        }
    }
}
