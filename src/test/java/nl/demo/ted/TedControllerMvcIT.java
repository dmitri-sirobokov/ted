package nl.demo.ted;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import nl.demo.ted.model.TedTalk;
import nl.demo.ted.service.TedService;

@WebMvcTest
public class TedControllerMvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TedService service;

	@BeforeEach
	public void setup() {
		final TedTalk tedTalk = new TedTalk();
		tedTalk.setId("1");
		tedTalk.setTitle("title 1");
		tedTalk.setAuthor("author 1");
		Mockito.when(service.getTedTalkById("1")).thenReturn(tedTalk);

		Mockito.when(service.getTedTalkById("2")).thenThrow(new EntityNotFoundException());

		var allTedTalks = new ArrayList<TedTalk>();
		allTedTalks.add(tedTalk);
		Mockito.when(service.getTedTalks(Mockito.any())).thenReturn(allTedTalks);
	}

	@Test
	public void getExistingTedTalk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/ted-talks/1"))
				.andExpect(status().isOk());
	}

	@Test
	public void getNonExistingTedTalk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/ted-talks/2"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAll() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/ted-talks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id", is("1")));
	}

	@Test
	public void updateExisting() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.put("/ted-talks/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"id\": \"1\", \"title\": \"updated title\"}"))
				.andExpect(status().isNoContent());
		final TedTalk tedTalk = new TedTalk();
		tedTalk.setId("1");
		tedTalk.setTitle("updated title");
		Mockito.verify(service).createOrUpdateTedTalk(tedTalk);
	}

	@Test
	public void deleteTed() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/ted-talks/1"))
				.andExpect(status().isOk());
		Mockito.verify(service).deleteTedTalkById("1");
	}
}
