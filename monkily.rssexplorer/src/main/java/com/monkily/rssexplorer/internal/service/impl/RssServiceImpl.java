package com.monkily.rssexplorer.internal.service.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monkily.content.model.ExtractedContent;
import com.monkily.content.service.ContentService;
import com.monkily.content.service.ExtractionService;
import com.monkily.rssexplorer.model.Feed;
import com.monkily.rssexplorer.model.FeedSource;
import com.monkily.rssexplorer.service.RssService;
import com.monkily.utils.AfterInitialization;
import com.monkily.utils.DatabaseUtils;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;

@Service
public class RssServiceImpl implements RssService, AfterInitialization {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ContentService contentService;

	@Autowired
	ExtractionService extractionService;

	@Override
	@Transactional
	public void afterInitialization() {
		DatabaseUtils.forcePersist(entityManager, FeedSource.UNKNOWN,
				new FeedSource(FeedSource.UNKNOWN, "Unknown"));
		DatabaseUtils.forcePersist(entityManager, FeedSource.DIGG,
				new FeedSource(FeedSource.DIGG, "Digg"));
	}

	@Override
	public void checkAll() {

		List<Feed> feeds = entityManager.createQuery(
				"SELECT o FROM " + Feed.class.getName() + " o ")
				.getResultList();

		for (Feed feed : feeds) {
			checkFeed(feed);
		}
	}

	public void checkFeed(Feed feed) {

		try {
			SyndFeedInput input = new SyndFeedInput();
			InputStream stream = get(feed.getUrl());
			InputStreamReader reader = new InputStreamReader(stream);
			SyndFeed syndFeed = input.build(reader);
			List<SyndEntry> entries = syndFeed.getEntries();
			for (SyndEntry entry : entries) {

				// TODO: (Performance) do not process already processed entries

				if (feed.getFeedSource().getId().equals(FeedSource.UNKNOWN)) {
					submitUnknownEntry(entry);
				} else if (feed.getFeedSource().getId().equals(FeedSource.DIGG)) {
					submitDiggEntry(entry);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	public static InputStream get(String url) {

		try {
			// Send data
			HttpClient client = new HttpClient();

			GetMethod method = new GetMethod(url);
			method.setFollowRedirects(true);
			client.executeMethod(method);

			return method.getResponseBodyAsStream();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void submitUnknownEntry(SyndEntry entry) {

		String title = entry.getTitle();
		String link = entry.getLink();
		Date publishedDate = entry.getPublishedDate();
		String summary = entry.getDescription().getValue();

		ExtractedContent extractedContent = extractionService
				.extractContent(link);

		contentService.submitContent(link, title, publishedDate,
				extractedContent.getContentType(), summary, extractedContent
						.getData());
	}

	public void submitDiggEntry(SyndEntry entry) {

		String title = entry.getTitle();
		String link = entry.getLink();
		Date publishedDate = entry.getPublishedDate();
		String summary = entry.getDescription().getValue();

		ExtractedContent extractedContent = extractionService
				.extractContent(link);

		contentService.submitContent(link, title, publishedDate,
				extractedContent.getContentType(), summary, extractedContent
						.getData());
	}

}
