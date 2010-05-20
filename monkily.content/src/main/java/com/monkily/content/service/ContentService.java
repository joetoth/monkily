package com.monkily.content.service;

import java.util.Date;
import java.util.List;

import com.monkily.content.model.Content;
import com.monkily.content.model.ContentSummary;
import com.monkily.content.model.ContentType;

public interface ContentService {

	public String submitContent(String url, String title, Date publishedDate,
			ContentType contentType, String description, byte[] data);

	public String submitUrl(String url);

	public Content getContent(String id);

	public List<ContentSummary> findContentSummaries(Date from, Date to);

	public List<Content> findContents(Date from, Date to);

}
