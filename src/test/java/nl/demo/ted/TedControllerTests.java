package nl.demo.ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nl.demo.ted.model.TedTalk;
import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TedControllerTests {
	@LocalServerPort
	private int port;

	private final TestRestTemplate restTemplate = new TestRestTemplate();
	private final HttpHeaders headers = new HttpHeaders();
	private final List<TedRecord> tedRecords = new ArrayList<>();

	@MockBean
	private TedRepository repository;

	public TedControllerTests() {

	}

	@BeforeEach
	void setup() {
		tedRecords.clear();
		var record = new TedRecord();
		record.setId("1");
		record.setAuthor("author1");
		record.setTitle("title1");
		record.setLikes(1);
		record.setViews(1);
		tedRecords.add(record);

		// mock get record from database
		Mockito.when(this.repository.findById(Mockito.anyString())).thenAnswer((a) -> {
			String id = a.getArgument(0);
			return this.tedRecords.stream().filter(r -> Objects.equals(r.getId(), id)).findFirst();
		});

		Mockito.when(this.repository.findAll()).thenAnswer((a) -> this.tedRecords);

		// mock save record to database
		Mockito.when(this.repository.save(Mockito.any())).thenAnswer((a) -> {
			TedRecord ted_arg = a.getArgument(0);

			var existingTed = this.tedRecords.stream().filter(r -> Objects.equals(r.getId(), ted_arg.getId())).findFirst().orElse(null);
			if (existingTed == null) {
				existingTed = new TedRecord();
				tedRecords.add(existingTed);
			}
			existingTed.setId(ted_arg.getId());
			existingTed.setAuthor(ted_arg.getAuthor());
			existingTed.setTitle(ted_arg.getTitle());
			existingTed.setViews(ted_arg.getViews());
			existingTed.setLikes(ted_arg.getLikes());
			return existingTed;
		});

		// mock delete from database
		Mockito.doAnswer(a -> {
			String id = a.getArgument(0);
			this.tedRecords.removeIf(r -> Objects.equals(r.getId(), id));
			return null;
		}).when(this.repository).deleteById(Mockito.anyString());

	}

	@Test
	void getExisting() {
		var responseEntity = this.get("/ted-talks/1", TedTalk.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		var result = responseEntity.getBody();
		assertEquals("1", result.getId());
		assertEquals("title1", result.getTitle());
	}

	@Test
	void getAll() {
		var responseEntity = this.get("/ted-talks", (Class<List<TedTalk>>)(Class)List.class);
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		var result = responseEntity.getBody();
		assertEquals(1, result.size());
	}

	@Test
	void putExisting() {
		// existing record should be updated
		var ted = new TedTalk();
		ted.setViews(2);
		ted.setLikes(3);
		ted.setTitle("title_updated");
		ted.setAuthor("author_updated");

		var responseEntity = this.put("/ted-talks/1", ted, Void.class);
		assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());

		var result = tedRecords.stream().filter(r -> Objects.equals(r.getId(), "1")).findFirst().orElse(null);
		assertNotNull(result);
		assertEquals("1", result.getId());
		assertEquals(2, result.getViews());
		assertEquals(3, result.getLikes());
		assertEquals("title_updated", result.getTitle());
		assertEquals("author_updated", result.getAuthor());
	}

	@Test
	void putNonExisting() {
		// non-existing record should be created
		var ted = new TedTalk();
		ted.setViews(2);
		ted.setLikes(3);
		ted.setTitle("new_title");
		ted.setAuthor("new_author");

		var responseEntity = this.put("/ted-talks/2", ted, Void.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		var result = tedRecords.stream().filter(r -> Objects.equals(r.getId(), "2")).findFirst().orElse(null);
		assertNotNull(result);
		assertEquals("2", result.getId());
		assertEquals(2, result.getViews());
		assertEquals(3, result.getLikes());
		assertEquals("new_title", result.getTitle());
		assertEquals("new_author", result.getAuthor());
	}

	@Test
	void postNonExisting() {
		var ted = new TedTalk();
		ted.setId("2");
		ted.setViews(2);
		ted.setLikes(3);
		ted.setTitle("new_title");
		ted.setAuthor("new_author");

		var responseEntity = this.post("/ted-talks", ted, Void.class);
		assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

		var result = tedRecords.stream().filter(r -> Objects.equals(r.getId(), "2")).findFirst().orElse(null);
		assertNotNull(result);
		assertEquals("2", result.getId());
		assertEquals(2, result.getViews());
		assertEquals(3, result.getLikes());
		assertEquals("new_title", result.getTitle());
		assertEquals("new_author", result.getAuthor());
	}

	@Test
	void postExisting() {
		var ted = new TedTalk();
		ted.setId("1");
		ted.setViews(2);
		ted.setLikes(3);
		ted.setTitle("new_title");
		ted.setAuthor("new_author");

		var responseEntity = this.post("/ted-talks", ted, Void.class);
		assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
	}

	@Test
	void deleteTed() {
		var responseEntity = this.delete("/ted-talks/1");
		assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
		var result = tedRecords.stream().filter(r -> Objects.equals(r.getId(), "1")).findFirst().orElse(null);
		assertNull(result);

		// non-existing resource deletion should result in Http OK
		responseEntity = this.delete("/ted-talks/2");
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

	private <T> ResponseEntity<T> get(String url, Class<T> responseType) {
		HttpEntity<Void> httpEntity = new HttpEntity<>(null, headers);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.GET, httpEntity, responseType);
	}

	private <T, V> ResponseEntity<T> post(String url, V entity, Class<T> responseType) {
		HttpEntity<V> httpEntity = new HttpEntity<>(entity);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.POST, httpEntity, responseType);
	}

	private <T, V> ResponseEntity<T> put(String url, V entity, Class<T> responseType) {
		HttpEntity<V> httpEntity = new HttpEntity<>(entity);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.PUT, httpEntity, responseType);
	}

	private <T> ResponseEntity<T> patch(String url, T entity, Class<T> responseType) {
		HttpEntity<T> httpEntity = new HttpEntity<>(entity);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.PATCH, httpEntity, responseType);
	}

	private ResponseEntity<Void> delete(String url) {
		HttpEntity<Void> httpEntity = new HttpEntity<>(null);
		return restTemplate.exchange(createURLWithPort(url), HttpMethod.DELETE, httpEntity, Void.class);
	}

}
