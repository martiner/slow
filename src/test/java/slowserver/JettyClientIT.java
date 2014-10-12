package slowserver;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;
import slowserver.jetty.JettyClientHttpRequestFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = JettyClientIT.Config.class)
public class JettyClientIT {

    private static final long TIMEOUT = SECONDS.toMillis(10);
    private static final Integer PORT = Integer.getInteger("jettyPort", 8080);
    private static final String URL = "http://localhost:" + PORT + "/dump";

    @Autowired RestTemplate rest;

    @Test
    public void get() throws Exception {
        final ResponseEntity<String> entity = rest.getForEntity(URL, String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void post() throws Exception {
        final ResponseEntity<String> entity = rest.postForEntity(URL, "hello", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), is(containsString("body: hello")));
    }

    @Test
    public void postWithHeaders() throws Exception {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("foo", "bar");
        final ResponseEntity<String> entity = rest.exchange(URL, HttpMethod.POST, new HttpEntity<>("hello", headers), String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getBody(), is(containsString("body: hello")));
        assertThat(entity.getBody(), is(containsString("foo: [bar]")));
    }

    @Configuration
    static class Config {

        @Bean RestTemplate template() throws Exception {
            return new RestTemplate(factory());
        }

        @Bean ClientHttpRequestFactory factory() throws Exception {
            HttpClient httpClient = new HttpClient(new SslContextFactory());
            httpClient.setConnectTimeout(TIMEOUT);
            httpClient.setIdleTimeout(TIMEOUT);

            // todo so timeout?
            // todo httpClient.setMaxConnectionsPerDestination();
            //httpClient.setExecutor(java.util.concurrent.Executors.newFixedThreadPool()); default is 200
            httpClient.start();

            final JettyClientHttpRequestFactory requestFactory = new JettyClientHttpRequestFactory(httpClient);
            requestFactory.setTimeout(10);

            return requestFactory;
        }
    }
}
