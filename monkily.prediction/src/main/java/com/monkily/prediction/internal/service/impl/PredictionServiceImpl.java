package com.monkily.prediction.internal.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monkily.content.model.Content;
import com.monkily.content.model.ContentEntity;
import com.monkily.content.model.ContentTopic;
import com.monkily.content.service.ContentService;
import com.monkily.interest.model.ContentView;
import com.monkily.interest.service.InterestService;
import com.monkily.prediction.model.Prediction;
import com.monkily.prediction.model.RelevanceCount;
import com.monkily.prediction.service.PredictionService;
import com.monkily.user.model.User;
import com.monkily.user.service.UserService;
import com.monkily.utils.GUID;

@Service
public class PredictionServiceImpl implements PredictionService {

	public static Log log = LogFactory.getLog(PredictionServiceImpl.class);

	@Autowired
	ContentService contentService;

	@Autowired
	InterestService interestService;

	@Autowired
	UserService userService;

	@PersistenceContext
	EntityManager entityManager;

	public Prediction getPrediction(String userId, String contentId) {
		Prediction prediction = null;

		Query query = entityManager.createQuery("SELECT o FROM "
				+ Prediction.class.getName()
				+ " o WHERE o.userId=:userId AND o.contentId=:contentId");
		List<Prediction> result = query.getResultList();

		if (!result.isEmpty()) {
			prediction = result.get(0);
		}

		return prediction;
	}

	@Override
	@Transactional
	public void predictInterest(String userId, String contentId) {
		Content content = contentService.getContent(contentId);

		// Find all previously read content
		List<ContentView> views = interestService.getViewsByUserId(userId);

		// TODO: Incorporate reading time into the algorithm. Adjust the
		// 'influence' of the content on the index based on the time spent
		// reading.

		// Count Unique Entities and Topics
		Map<String, RelevanceCount> entityRelevance = new HashMap<String, RelevanceCount>();
		Map<String, RelevanceCount> topicRelevance = new HashMap<String, RelevanceCount>();

		for (ContentView contentView : views) {
			Content contentViewed = contentService.getContent(contentView
					.getContentId());

			// Count Entities
			for (ContentEntity entity : contentViewed.getEntities()) {
				String value = entity.getValue();
				RelevanceCount relevanceCount = entityRelevance.get(value);
				if (relevanceCount == null) {
					relevanceCount = new RelevanceCount();
					relevanceCount.setCount(0);
					relevanceCount.setRelevance(0d);
					entityRelevance.put(value, relevanceCount);
				}

				relevanceCount.setCount(relevanceCount.getCount() + 1);
				relevanceCount.setRelevance(relevanceCount.getRelevance()
						+ entity.getRelevance());
			}

			// Count Topics
			for (ContentTopic topic : contentViewed.getTopics()) {
				String value = topic.getName();
				RelevanceCount relevanceCount = topicRelevance.get(value);
				if (relevanceCount == null) {
					relevanceCount = new RelevanceCount();
					relevanceCount.setCount(0);
					relevanceCount.setRelevance(0d);
					topicRelevance.put(value, relevanceCount);
				}

				relevanceCount.setCount(relevanceCount.getCount() + 1);
				relevanceCount.setRelevance(relevanceCount.getRelevance()
						+ topic.getRelevance());
			}
		}

		// Create User Indexes
		Map<String, Double> entityIndex = new HashMap<String, Double>();
		Map<String, Double> topicIndex = new HashMap<String, Double>();

		// Create Entity Index
		for (String name : entityRelevance.keySet()) {
			RelevanceCount rc = entityRelevance.get(name);
			Double value = rc.getRelevance() / rc.getCount();
			entityIndex.put(name, value);
			log.debug("Entity Index: " + name + ", " + value);
		}

		// Create Topic Index
		for (String name : topicRelevance.keySet()) {
			RelevanceCount rc = topicRelevance.get(name);
			Double value = rc.getRelevance() / rc.getCount();
			topicIndex.put(name, value);
			log.debug("Topic Index: " + name + ", " + value);
		}

		// Predict Interest
		Double interestScore = predictInterest(content, entityIndex, topicIndex);

		Prediction prediction = new Prediction();
		prediction.setContentId(content.getId());
		prediction.setId(GUID.generate());
		prediction.setUserId(userId);
		prediction.setInterestScore(interestScore);
		prediction.setCreatedDate(new Date());

		entityManager.persist(prediction);
	}

	private Double predictInterest(Content content,
			Map<String, Double> entityIndex, Map<String, Double> topicIndex) {
		// TODO: Incorporate entities into interest calculation

		Double interest = 0.0;
		for (ContentTopic topic : content.getTopics()) {
			String name = topic.getName();

			Double userRelevance = topicIndex.get(name);
			if (userRelevance != null) {
				Double topicRelevance = topic.getRelevance();
				interest += topicRelevance / userRelevance;
			}
		}

		interest = interest / content.getTopics().size();

		return interest;
	}

	@Override
	@Transactional
	public void predictInterest(String contentId) {

		List<User> users = userService.getAllUsers();
		for (User user : users) {
			predictInterest(user.getId(), contentId);
		}

	}

	@Override
	public List<Prediction> findPredictions(Date from, Date to) {

		Query query = entityManager.createQuery("SELECT o FROM "
				+ Prediction.class.getName()
				+ " o WHERE o.createdDate > :from AND o.createdDate <= :to");

		query.setParameter("from", from);
		query.setParameter("to", to);

		return query.getResultList();
	}

	@Override
	@Transactional
	public void predictAllInterest() {
		List<Content> contents = contentService.findContents(new Date(System
				.currentTimeMillis()
				- (1000 * 60 * 60 * 24 * 365)), new Date());
		for (Content content : contents) {
			predictInterest(content.getId());
		}
	}

}
