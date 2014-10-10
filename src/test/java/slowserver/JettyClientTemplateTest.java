package slowserver;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import slowserver.jetty.JettyClientHttpRequestFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.HttpStatus.ACCEPTED;

public class JettyClientTemplateTest {

    private static final int TIMEOUT = (int) SECONDS.toMillis(10);
    private static final String PATH = "/foo";

    private HttpClient httpClient;
    private RestTemplate rest;
    private String uri;

    @Before
    public void setUp() throws Exception {
        httpClient = new HttpClient(new SslContextFactory());
        httpClient.setConnectTimeout(TIMEOUT);
        httpClient.setIdleTimeout(TIMEOUT);
        // todo so timeout?
        // todo httpClient.setMaxConnectionsPerDestination();
        //httpClient.setExecutor(java.util.concurrent.Executors.newFixedThreadPool()); default is 200
        httpClient.start();

        final JettyClientHttpRequestFactory requestFactory = new JettyClientHttpRequestFactory(httpClient);
        requestFactory.setTimeout(10);
        rest = new RestTemplate(requestFactory);

        initJadler().that().respondsWithDefaultContentType("application/json");
        uri = "http://localhost:" + port() + PATH;

    }

    @After
    public void tearDown() throws Exception {
        httpClient.destroy();
        closeJadler();
    }

    @Test
    public void testName() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PATH)
            .respond()
                .withBody("bar")
                .withStatus(ACCEPTED.value())
        ;

        final ResponseEntity<String> entity = rest.getForEntity(uri, String.class);
        assertThat(entity.getStatusCode(), is(ACCEPTED));
        assertThat(entity.getBody(), is("bar"));
    }
}