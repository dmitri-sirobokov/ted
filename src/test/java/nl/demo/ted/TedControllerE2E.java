package nl.demo.ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import nl.demo.ted.model.TedTalk;

public class TedControllerE2ETests extends ApiBaseTests {

	@Test
	public void basicCrudTest() {
		// CREATE
		var ted = new TedTalk();
		ted.setId(UUID.randomUUID().toString());
		ted.setViews(2);
		ted.setLikes(3);
		ted.setTitle("new_title");
		ted.setAuthor("new_author");

		var createResponse = this.post("/ted-talks", ted, Void.class);
		assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

		// READ
		var getByIdResponse = this.get("/ted-talks/" + ted.getId(), TedTalk.class);
		assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
		var tedResult = getByIdResponse.getBody();
		assertNotNull(tedResult);
		assertEquals(ted.getId(), tedResult.getId());
		assertEquals(2, tedResult.getViews());
		assertEquals(3, tedResult.getLikes());
		assertEquals("new_title", tedResult.getTitle());
		assertEquals("new_author", tedResult.getAuthor());

		// UPDATE
		ted.setTitle("updated_title");
		var putResponse = this.put("/ted-talks/" + ted.getId(), ted, Void.class);
		assertEquals(HttpStatus.NO_CONTENT, putResponse.getStatusCode());

		getByIdResponse = this.get("/ted-talks/" + ted.getId(), TedTalk.class);
		assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
		tedResult = getByIdResponse.getBody();
		assertNotNull(tedResult);
		assertEquals(ted.getId(), tedResult.getId());
		assertEquals("updated_title", tedResult.getTitle());

		// SEARCH
		var getListResponse = this.get("/ted-talks?title=updated", (Class<List<HashMap>>)(Class)List.class);
		assertEquals(HttpStatus.OK, getByIdResponse.getStatusCode());
		var getListResult = getListResponse.getBody();
		assertFalse(getListResult.isEmpty());

		// list elements are deserialized as LinkedHashMap by default. Can be improved later by providing TypeReference for RestTemplate
		assertTrue(getListResult.stream().allMatch(t -> t.get("title").toString().contains("updated")));

		// DELETE
		var deleteResponse = this.delete("/ted-talks/" + ted.getId());
		assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

		var notFoundResponse = this.get("/ted-talks/" + ted.getId(), Void.class);
		assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
	}



}
