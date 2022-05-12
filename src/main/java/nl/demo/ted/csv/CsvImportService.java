package nl.demo.ted.csv;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import javax.transaction.Transactional;

import org.apache.commons.csv.CSVFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import nl.demo.ted.repository.TedRecord;
import nl.demo.ted.repository.TedRepository;

@Component
public class CsvImportService {

	@Value("${ted.initial-db-file-csv:}")
	private String importFilePath;
	private final TedRepository repository;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	CsvImportService(TedRepository repository) {
		this.repository = repository;
	}

	private long parseLongNoException(String s) {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException _ex) {
			logger.warn(String.format("Unable to parse '%s' as long integer. Zero value is used.", s));
			return 0L;
		}
	}

	private Date parseDateNoException(String s) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("MMM y", Locale.ENGLISH);
			return format.parse(s);
		} catch (ParseException _ex) {
			logger.warn(String.format("Unable to parse '%s' as date. NULL value is used.", s));
			return null;
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	@Transactional
	public void checkImportFileOnStartup() throws IOException {
		var path = Path.of(importFilePath);
		if (StringUtils.hasText(importFilePath) && Files.exists(path)) {
			var count = 0;
			logger.info(String.format("Start import from CSV file %s...", path));
			try(FileReader reader = new FileReader(importFilePath)) {
				var csvFormat = CSVFormat.Builder.create()
						.setHeader("title", "author", "date", "views", "likes", "link")
						.setSkipHeaderRecord(true)
						.setAllowMissingColumnNames(true)
						.setDelimiter(',')
						.setCommentMarker('#')
						.setIgnoreEmptyLines(true)
						.setIgnoreSurroundingSpaces(true)
						.build();
				for(var csvRecord : csvFormat.parse(reader)) {
					count++;
					var record = new TedRecord();
					record.setId(UUID.randomUUID().toString());
					record.setTitle(csvRecord.get("title"));
					record.setAuthor(csvRecord.get("author"));
					record.setLikes(parseLongNoException(csvRecord.get("likes")));
					record.setViews(parseLongNoException(csvRecord.get("views")));
					record.setDate(parseDateNoException(csvRecord.get("date")));
					record.setLink(csvRecord.get("link"));
					repository.save(record);
				}

			}
			Files.delete(path);
			logger.info(String.format("Import CSV file complete. %d total records imported", count));
		}
	}
}
