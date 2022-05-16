package nl.demo.ted;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiBaseTests {
	@LocalServerPort
	private int port;
	private final HttpHeaders headers = new HttpHeaders();
	private final TestRestTemplate restTemplate = new TestRestTemplate();
	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

	protected  <T> ResponseEntity<T> get(String url, Class<T> responseType) {
		HttpEntity<Void> httpEntity = new HttpEntity<>(null, headers);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.GET, httpEntity, responseType);
	}

	protected <T, V> ResponseEntity<T> post(String url, V entity, Class<T> responseType) {
		HttpEntity<V> httpEntity = new HttpEntity<>(entity);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.POST, httpEntity, responseType);
	}

	protected <T, V> ResponseEntity<T> put(String url, V entity, Class<T> responseType) {
		HttpEntity<V> httpEntity = new HttpEntity<>(entity);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.PUT, httpEntity, responseType);
	}

	protected ResponseEntity<Void> delete(String url) {
		HttpEntity<Void> httpEntity = new HttpEntity<>(null);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.DELETE, httpEntity, Void.class);
	}

}
