package nl.demo.ted;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;

@WebMvcTest
public class MvcTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TedRepository repository;

	@BeforeEach
	public void setup() {
		final TedRecord record = new TedRecord();
		record.setId("1");
		record.setTitle("title 1");
		record.setAuthor("author 1");
		Mockito.when(repository.findById("1")).thenReturn(Optional.of(record));
	}

	@Test
	public void getExistingTedTalk() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/ted-talks/1"))
				.andExpect(status().isOk());
	}
}
