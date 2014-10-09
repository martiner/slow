package org.apache.http.impl.conn;

import org.apache.commons.logging.Log;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.MessageConstraints;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

import java.io.IOException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class MyLoggingManagedHttpClientConnection extends LoggingManagedHttpClientConnection {
    public MyLoggingManagedHttpClientConnection(final String id, final Log log, final Log headerlog, final Log wirelog, final int buffersize, final int fragmentSizeHint, final CharsetDecoder chardecoder, final CharsetEncoder charencoder, final MessageConstraints constraints, final ContentLengthStrategy incomingContentStrategy, final ContentLengthStrategy outgoingContentStrategy, final HttpMessageWriterFactory<HttpRequest> requestWriterFactory, final HttpMessageParserFactory<HttpResponse> responseParserFactory) {
        super(id, log, headerlog, wirelog, buffersize, fragmentSizeHint, chardecoder, charencoder, constraints, incomingContentStrategy, outgoingContentStrategy, requestWriterFactory, responseParserFactory);
    }

    @Override
    public boolean isResponseAvailable(final int timeout) throws IOException {
        System.out.println("Is response available: " + timeout);
        return super.isResponseAvailable(timeout);
    }
}
