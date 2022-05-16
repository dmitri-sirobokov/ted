package nl.demo.ted.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TedTalk {
	private String id;
	private String author;
	private String title;
	private long views;
	private long likes;
	private String link;
	private Long date;
}
