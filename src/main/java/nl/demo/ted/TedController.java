package nl.demo.ted;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import nl.demo.ted.exception.ApiError;
import nl.demo.ted.exception.TedExistsException;
import nl.demo.ted.exception.TedNotFoundException;
import nl.demo.ted.model.TedQueryParams;
import nl.demo.ted.model.TedTalk;
import nl.demo.ted.service.TedService;

@RestController()
@RequestMapping(value = "/ted-talks", produces = "application/json")
public class TedController {

	private final TedService tedService;

	TedController(final TedService tedService) {
		this.tedService = tedService;
	}

	@GetMapping("/{id}")
	public TedTalk getTedTalk(
			@PathVariable String id
	) {
		return tedService.getTedTalkById(id);
	}

	@GetMapping()
	public List<TedTalk> getTedTalks(
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String title,
			@RequestParam(required = false) Long minLikes,
			@RequestParam(required = false) Long maxLikes,
			@RequestParam(required = false) Long minViews,
			@RequestParam(required = false) Long maxViews) {
		var queryParams = new TedQueryParams();
		queryParams.setAuthor(author);
		queryParams.setTitle(title);
		queryParams.setMinLikes(minLikes);
		queryParams.setMaxLikes(maxLikes);
		queryParams.setMinViews(minViews);
		queryParams.setMaxViews(maxViews);
		return tedService.getTedTalks(queryParams);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void postTedTalk(@RequestBody TedTalk ted) {
		tedService.createTedTalk(ted);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Void> putTedTalk(@PathVariable String id, @RequestBody TedTalk ted) {
		ted.setId(id);
		boolean created = tedService.createOrUpdateTedTalk(ted);
		var responseCode = created ? HttpStatus.CREATED : HttpStatus.NO_CONTENT;
		return new ResponseEntity<>(responseCode);
	}

	@DeleteMapping("/{id}")
	public void putTedTalk(@PathVariable String id) {
		tedService.deleteTedTalkById(id);
	}

	@ExceptionHandler(TedNotFoundException.class)
	private ResponseEntity handleEntityNotFoundException(TedNotFoundException ex) {
		return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(TedExistsException.class)
	private ResponseEntity handleEntityExistsException(TedExistsException ex) {
		return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
	}

	private ResponseEntity<ApiError> createErrorResponse(HttpStatus status, String message) {
		var apiError = new ApiError(status, message);
		return new ResponseEntity<>(apiError, status);
	}

}
