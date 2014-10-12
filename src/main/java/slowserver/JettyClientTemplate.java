package slowserver;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import slowserver.jetty.JettyClientHttpRequestFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static slowserver.Server.PORT;

public class JettyClientTemplate {

    private static final long TIMEOUT = SECONDS.toMillis(10);

    public static void main(String... args) throws Exception {
        HttpClient httpClient = new HttpClient(new SslContextFactory());
        httpClient.setConnectTimeout(TIMEOUT);
        httpClient.setIdleTimeout(TIMEOUT);


        // todo so timeout?
        // todo httpClient.setMaxConnectionsPerDestination();
        //httpClient.setExecutor(java.util.concurrent.Executors.newFixedThreadPool()); default is 200
        httpClient.start();

        final JettyClientHttpRequestFactory requestFactory = new JettyClientHttpRequestFactory(httpClient);
        requestFactory.setTimeout(10);
        final RestTemplate rest = new RestTemplate(requestFactory);

        try {
            final ResponseEntity<String> entity = rest.getForEntity("http://localhost:" + PORT + "/dummy", String.class);
            System.out.println(entity);
        } finally {
            httpClient.stop();
        }

    }
}
