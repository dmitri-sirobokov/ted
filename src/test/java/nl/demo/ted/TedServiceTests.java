package nl.demo.ted;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import nl.demo.ted.model.TedTalk;
import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;
import nl.demo.ted.service.TedService;

public class TedServiceTests {

	private TedRepository repository;
	private TedService service;
	private List<TedRecord> records;

	@BeforeEach
	public void setup() {
		records = new ArrayList<>();
		repository = Mockito.mock(TedRepository.class);

		var record1 = new TedRecord();
		record1.setId("1");
		record1.setAuthor("Author 1");
		record1.setTitle("Title 1");
		record1.setLikes(1);
		record1.setViews(2);
		records.add(record1);
		Mockito.when(repository.findById("1")).thenReturn(Optional.of(record1));

		service = new TedService(repository);
		Mockito.when(repository.findAll(Mockito.any(Specification.class))).thenReturn(records);
	}

	@Test
	public void getTedTalkById_existing() {
		var ted = service.getTedTalkById("1");
		assertNotNull(ted);
		assertEquals("1", ted.getId());
		assertEquals("Author 1", ted.getAuthor());
		assertEquals("Title 1", ted.getTitle());
		assertEquals(1, ted.getLikes());
		assertEquals(2, ted.getViews());
	}

	@Test
	public void getTedTalkById_nonExisting() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> service.getTedTalkById("2"));
	}

	@Test
	public void getTedTalks_all() {
		var talks = service.getTedTalks(null);
		assertNotNull(talks);
		assertFalse(talks.isEmpty());
	}

	@Test
	public void createTedTalk() {
		var record = ArgumentCaptor.forClass(TedRecord.class);
		var ted = new TedTalk();
		ted.setId("2");

		service.createTedTalk(ted);

		Mockito.verify(repository).save(record.capture());
		assertEquals("2", record.getValue().getId());
	}


}
