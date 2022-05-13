package nl.demo.ted;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import nl.demo.ted.model.TedTalk;
import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;

@RestController()
@RequestMapping(value = "/ted-talks", produces = "application/json")
public class TedController {

	private TedRepository repository;

	TedController(TedRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/{id}")
	public ResponseEntity<TedTalk> getTedTalk(
			@PathVariable String id
	) {
		var record = this.repository.findById(id).orElse(null);
		if (record == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		var result = new TedTalk();
		mapJpaToModel(record, result);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<List<TedTalk>> getTedTalks(
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) Long minViews,
			@RequestParam(required = false) Long minLikes
	) {
		// Todo: Need a custom query implementation by Specification/Example for query parameters
		var records = repository.findAll();
		var tedTalks = records.stream().map(this::mapJpaToModel).collect(Collectors.toList());
		return new ResponseEntity<>(tedTalks, HttpStatus.OK);
	}

	@PostMapping()
	public ResponseEntity<Void> postTedTalk(@RequestBody TedTalk ted) {
		var existingRecord = this.repository.findById(ted.getId());
		if (existingRecord.isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		var newRecord = new TedRecord();
		mapModelToJpa(ted, newRecord);
		repository.save(newRecord);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> putTedTalk(@PathVariable String id, @RequestBody TedTalk ted) {
		ted.setId(id);
		var record = this.repository.findById(ted.getId()).orElse(null);
		var isNew = record == null;
		if (isNew) {
			record = new TedRecord();
		}
		mapModelToJpa(ted, record);
		repository.save(record);
		var responseCode = isNew ? HttpStatus.CREATED : HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(responseCode);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> putTedTalk(@PathVariable String id) {
		this.repository.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void mapModelToJpa(TedTalk ted, TedRecord tedRecord) {
		tedRecord.setId(ted.getId());
		tedRecord.setAuthor(ted.getAuthor());
		tedRecord.setTitle(ted.getTitle());
		tedRecord.setLikes(ted.getLikes());
		tedRecord.setViews(ted.getViews());
		tedRecord.setLink(ted.getLink());
		var date = ted.getDate() == null ? null : new Date(ted.getDate());
		tedRecord.setDate(date);
	}

	private void mapJpaToModel(TedRecord tedRecord, TedTalk ted) {
		ted.setId(tedRecord.getId());
		ted.setAuthor(tedRecord.getAuthor());
		ted.setTitle(tedRecord.getTitle());
		ted.setLikes(tedRecord.getLikes());
		ted.setViews(tedRecord.getViews());
		ted.setLink(tedRecord.getLink());
		var date = tedRecord.getDate() == null ? null : tedRecord.getDate().getTime();
		ted.setDate(date);
	}

	private TedTalk mapJpaToModel(TedRecord tedRecord) {
		var ted = new TedTalk();
		mapJpaToModel(tedRecord, ted);
		return ted;
	}

}
