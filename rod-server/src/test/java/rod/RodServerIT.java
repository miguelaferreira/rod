package rod;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.web.client.RestTemplate;

public class RodServerIT extends RodServer {

    @Test
    public void testHealthIndicator() {
        RodServer.main(new String[] {});

        final RestTemplate template = new TestRestTemplate();
        final String body = template.getForEntity("http://localhost:8080/health", String.class).getBody();

        assertThat(body, equalTo("{\"status\":\"UP\"}"));
    }

}
