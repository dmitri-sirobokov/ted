package nl.demo.ted.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TedTalk {
	private String id;
	private String author;
	private String title;
	private long views;
	private long likes;
	private String link;
	private Long date;
}
