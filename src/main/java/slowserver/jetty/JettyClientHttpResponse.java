/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package slowserver.jetty;

import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.http.HttpFields;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.util.Collections.list;
import static org.springframework.util.Assert.notNull;

/**
 */
public class JettyClientHttpResponse extends AbstractClientHttpResponse {

    private final ContentExchange response;

    JettyClientHttpResponse(final ContentExchange response) {
        notNull(response, "httpMethod can't be null");
        this.response = response;
    }

    @Override
    public int getRawStatusCode() throws IOException {
        return response.getResponseStatus();
    }

    @Override
    public String getStatusText() throws IOException {
        return ""; // todo
    }

    @Override
    public void close() {
    }

    @Override
    public InputStream getBody() throws IOException {
        return new ByteArrayInputStream(response.getResponseContentBytes());
    }

    @Override
    public HttpHeaders getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        final HttpFields fields = response.getResponseFields();
        if (fields != null) {
            for (final String name: list(fields.getFieldNames())) {
                headers.put(name, new ArrayList<>(fields.getValuesCollection(name)));
            }
        }
        return headers;
    }
}
