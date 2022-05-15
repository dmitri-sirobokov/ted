package nl.demo.ted.repository;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ted_talks")
@Getter
@Setter
@NoArgsConstructor
public class TedRecord {
	@Id
	private String id;
	private String author;
	private String title;
	private long views;
	private long likes;
	private String link;
	private Date date;
}
