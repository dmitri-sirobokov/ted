package nl.demo.ted;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import nl.demo.ted.repository.TedRepository;

@DataJpaTest
@AutoConfigureTestDatabase
@Sql("/inserts.sql")
public class TedRepositoryTests {
	@Autowired
	private TedRepository repository;

	@Test
	public void testFindById() {
		var record = repository.findById("1");
		assertTrue(record.isPresent());
	}

	@Test
	public void testFindAll() {
		var records = repository.findAll();
		assertFalse(records.isEmpty());
	}

}
