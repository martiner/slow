/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package slowserver.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.springframework.util.Assert.notNull;

/**
 */
public class JettyClientHttpRequest extends AbstractClientHttpRequest {

    // todo test request with body
    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream();

    private final Request request;

    JettyClientHttpRequest(final HttpClient httpClient, final Request request) {
        notNull(httpClient, "httpClient can't be null");
        notNull(request, "request can't be null");
        this.request = request;
    }

    @Override
    public HttpMethod getMethod() {
        return HttpMethod.valueOf(request.getMethod());
    }

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    protected OutputStream getBodyInternal(final HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    @Override
    protected ClientHttpResponse executeInternal(final HttpHeaders headers) throws IOException {
        try {
            request.content(new BytesContentProvider(bufferedOutput.toByteArray()));
            return new JettyClientHttpResponse(request.send());
        } catch (InterruptedException e) {
            throw new IOException("Unable to process request - interrupted", e);
        } catch (TimeoutException e) {
            throw new IOException("Unable to process request - timeout", e);
        } catch (ExecutionException e) {
            throw new IOException("Unable to process request - execution exception", e);
        }
    }
}
