package slowserver;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.MyManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

import static slowserver.Server.PORT;

public class ResilientClient {

    private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10);

    public static void main(String... args) {
        final CloseableHttpClient client = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setSocketTimeout(TIMEOUT)
                        .setConnectTimeout(TIMEOUT)
                        .setConnectionRequestTimeout(TIMEOUT)
                        .build())
                .setConnectionManager(new PoolingHttpClientConnectionManager(new MyManagedHttpClientConnectionFactory()))
                .build();
        final RestTemplate rest = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        final ResponseEntity<String> entity = rest.getForEntity("http://localhost:" + PORT + "/dummy", String.class);
        System.out.println(entity);
    }


}
