package nl.demo.ted.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.criteria.Predicate;
import javax.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import nl.demo.ted.exception.TedExistsException;
import nl.demo.ted.exception.TedNotFoundException;
import nl.demo.ted.model.TedQueryParams;
import nl.demo.ted.model.TedTalk;
import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;

@Service
public class TedService {

	private final TedRepository repository;

	public TedService(final TedRepository repository) {
		this.repository = repository;
	}

	public TedTalk getTedTalkById(final String id) {
		return repository.findById(id)
				.map(this::mapJpaToModel)
				.orElseThrow(() -> new TedNotFoundException(String.format("Ted Talk %s is not found", id)));
	}

	public List<TedTalk> getTedTalks(TedQueryParams queryParams) {
		List<TedRecord> records = repository
				.findAll(getTedTalkSpecification(queryParams));
		return records
				.stream()
				.map(this::mapJpaToModel)
				.collect(Collectors.toList());
	}

	/**
	 * Create new Ted Talk. To update existing TedTalk, use {@link #createOrUpdateTedTalk}
	 *
	 * @param ted {@link TedTalk} instance to save.
	 *
	 * @exception EntityExistsException if TedTalk with specified id already exists.
	 *
	 */
	@Transactional
	public void createTedTalk(TedTalk ted) {
		if (repository.existsById(ted.getId())) {
			throw new TedExistsException(String.format("Ted Talk with specified id '%s' already exists", ted.getId()));
		}
		var newRecord = mapModelToJpa(ted);
		repository.save(newRecord);
	}

	/**
	 * Create new or update existing Ted Talk
	 *
	 * @param ted {@link TedTalk} instance to save.
	 * @return True if new entity is created, otherwise False.
	 */
	@Transactional
	public boolean createOrUpdateTedTalk(TedTalk ted) {
		var exists = repository.existsById(ted.getId());
		var record = mapModelToJpa(ted);
		repository.save(record);
		return !exists;
	}

	@Transactional
	public void deleteTedTalkById(String id) {
		repository.deleteById(id);
	}

	private TedRecord mapModelToJpa(TedTalk ted) {
		var tedRecord = new TedRecord();
		tedRecord.setId(ted.getId());
		tedRecord.setAuthor(ted.getAuthor());
		tedRecord.setTitle(ted.getTitle());
		tedRecord.setLikes(ted.getLikes());
		tedRecord.setViews(ted.getViews());
		tedRecord.setLink(ted.getLink());
		var date = ted.getDate() == null ? null : new Date(ted.getDate());
		tedRecord.setDate(date);
		return tedRecord;
	}

	private TedTalk mapJpaToModel(TedRecord tedRecord) {
		var ted = new TedTalk();
		ted.setId(tedRecord.getId());
		ted.setAuthor(tedRecord.getAuthor());
		ted.setTitle(tedRecord.getTitle());
		ted.setLikes(tedRecord.getLikes());
		ted.setViews(tedRecord.getViews());
		ted.setLink(tedRecord.getLink());
		var date = tedRecord.getDate() == null ? null : tedRecord.getDate().getTime();
		ted.setDate(date);
		return ted;
	}

	private Specification<TedRecord> getTedTalkSpecification(TedQueryParams params) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (params.getTitle() != null && !params.getTitle().isBlank()) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
						"%" + params.getTitle().toLowerCase() + "%"));
			}
			if (params.getAuthor() != null && !params.getAuthor().isBlank()) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("author")),
						"%" + params.getAuthor().toLowerCase() + "%"));
			}
			if (params.getMinLikes() != null && params.getMinLikes() > 0) {
				predicates.add(criteriaBuilder.ge(root.get("likes"), params.getMinLikes()));
			}
			if (params.getMaxLikes() != null && params.getMaxLikes() > 0) {
				predicates.add(criteriaBuilder.le(root.get("likes"), params.getMaxLikes()));
			}
			if (params.getMinViews() != null && params.getMinViews() > 0) {
				predicates.add(criteriaBuilder.ge(root.get("views"), params.getMinViews()));
			}
			if (params.getMaxViews() != null && params.getMaxViews() > 0) {
				predicates.add(criteriaBuilder.le(root.get("views"), params.getMaxViews()));
			}
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

}
