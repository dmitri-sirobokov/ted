package nl.demo.ted.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TedQueryParams {
	private String author;
	private String title;
	private Long minLikes;
	private Long maxLikes;
	private Long minViews;
	private Long maxViews;
}
