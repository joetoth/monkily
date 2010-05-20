package com.monkily.content.internal.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monkily.content.model.Content;
import com.monkily.content.model.ContentSummary;
import com.monkily.content.model.ContentType;
import com.monkily.content.model.ContentUrl;
import com.monkily.content.model.ExtractedContent;
import com.monkily.content.service.ContentService;
import com.monkily.content.service.ExtractionService;
import com.monkily.utils.GUID;
import com.monkily.utils.StringUtils;
import com.opencalais.client.OpenCalaisClient;
import com.opencalais.client.model.Metadata;
import com.opencalais.client.model.MetadataElement;
import com.opencalais.client.model.Topic;

@Service
public class ContentServiceImpl implements ContentService {

	private final static Log log = LogFactory.getLog(ContentServiceImpl.class);

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	@Qualifier("extractionServiceImpl")
	ExtractionService extractionService;

	@Override
	public Content getContent(String id) {
		return entityManager.find(Content.class, id);
	}

	@Override
	@Transactional
	public String submitContent(String url, String title, Date publishedDate,
			ContentType contentType, String description, byte[] data) {

		// TODO: hash the data to ensure there is only 1 entry for each piece of
		// content add urls as neccessary

		Content content = findContentByUrl(url);
		if (content != null) {
			return content.getId();
		}

		Integer wordCount = 0;
		String processText = description;
		if (contentType.equals(ContentType.Text) && data != null) {
			processText = new String(data);
			wordCount = StringUtils.countWords(processText);
		}

		content = new Content();
		content.setContentType(contentType);
		content.setId(GUID.generate());
		content.setCreatedDate(new Date());
		content.setDescription(description);
		content.setPublishedDate(publishedDate);
		content.setTitle(title);
		content.setUpdatedDate(new Date());
		content.setData(data);
		content.setWordCount(wordCount);
		content.addUrl(url);

		if (processText != null) {
			OpenCalaisClient client = new OpenCalaisClient(
					"86azn8ab5pvjfp25u9g742zd");

			Metadata metadata = null;
			try {
				String output = client.query(processText);
				metadata = OpenCalaisClient.parseSimpleOutputFormat(output);

				for (MetadataElement element : metadata.getMetadataElements()
						.values()) {

					Double relevance = element.getRelevance();
					if (relevance == null) {
						relevance = new Double(0d);
					}

					content.addEntity(element.getName(), element.getValue(),
							element.getCount(), relevance);
				}

				for (Topic topic : metadata.getTopics().values()) {
					content.addTopic(topic.getName(), topic.getScore());
				}

			} catch (IOException e) {
				log.error("Retrieving Calais Semantics Content Id ("
						+ content.getId() + ")", e);
			}

		}

		entityManager.persist(content);

		return content.getId();
	}

	@Override
	public String submitUrl(String url) {
		ExtractedContent extracted = extractionService.extractContent(url);
		return submitContent(url, extracted.getTitle(), extracted
				.getPublishedDate(), extracted.getContentType(), extracted
				.getDescription(), extracted.getData());
	}

	private Content findContentByUrl(String url) {
		Query query = entityManager.createQuery("SELECT cu.content FROM "
				+ ContentUrl.class.getName() + " cu WHERE cu.url=:url");
		query.setParameter("url", url);

		List<Content> contents = query.getResultList();

		Content content = null;
		if (contents.size() > 0) {
			content = contents.get(0);
		}

		return content;
	}

	@Override
	public List<ContentSummary> findContentSummaries(Date from, Date to) {
		Query query = entityManager.createQuery("SELECT o FROM "
				+ ContentSummary.class.getName()
				+ " o WHERE o.createdDate > :from AND o.createdDate <= :to");
		query.setParameter("from", from);
		query.setParameter("to", to);

		return query.getResultList();
	}

	@Override
	public List<Content> findContents(Date from, Date to) {
		Query query = entityManager.createQuery("SELECT o FROM "
				+ Content.class.getName()
				+ " o WHERE o.createdDate > :from AND o.createdDate <= :to");
		query.setParameter("from", from);
		query.setParameter("to", to);

		return query.getResultList();
	}

}
