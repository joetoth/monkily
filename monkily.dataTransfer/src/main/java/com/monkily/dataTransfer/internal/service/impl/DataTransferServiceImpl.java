package com.monkily.dataTransfer.internal.service.impl;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.monkily.content.model.ContentSummary;
import com.monkily.content.service.ContentService;
import com.monkily.dataTransfer.model.LastExport;
import com.monkily.dataTransfer.service.DataTransferService;
import com.monkily.prediction.model.Prediction;
import com.monkily.prediction.service.PredictionService;
import com.monkily.user.model.User;
import com.monkily.utils.DatabaseUtils;
import com.monkily.utils.HttpUtils;
import com.monkily.utils.JsonUtils;
import com.monkily.utils.StringUtils;

@Service
public class DataTransferServiceImpl implements DataTransferService {

	// public static final String DATA_TRANSFER_URL =
	// "http://monkily.appspot.com/dataTransfer";

	public static final String DATA_TRANSFER_URL = "http://localhost:8080/dataTransfer";

	public static final String PASSWORD = "89getuf54";

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	PredictionService predictionService;

	@Autowired
	ContentService contentService;

	@Transactional
	public void transfer() {
		LastExport lastExport = getLastExport();
		Date from = lastExport.getLastExport();
		Date to = new Date();

		importUsers(from, to);
		exportContent(from, to);
		exportPredictions(from, to);

		// If no exceptions
		lastExport.setLastExport(to);
		entityManager.persist(lastExport);
	}

	private LastExport getLastExport() {
		LastExport lastExport = entityManager.find(LastExport.class,
				LastExport.ID);

		if (lastExport == null) {
			lastExport = new LastExport();
			lastExport.setId(LastExport.ID);
			lastExport.setLastExport(new Date(0l));
		}

		return lastExport;
	}

	private void exportContent(Date from, Date to) {

		List<ContentSummary> contents = contentService.findContentSummaries(
				from, to);

		Gson gson = JsonUtils.createDefaultGson();
		String json = gson.toJson(contents);
		HttpUtils.post(DATA_TRANSFER_URL + "/password/" + PASSWORD
				+ "/class/com.monkily.model.ContentSummary", json);
	}

	private void exportPredictions(Date from, Date to) {

		List<Prediction> predictions = predictionService.findPredictions(from,
				to);

		Gson gson = JsonUtils.createDefaultGson();
		String json = gson.toJson(predictions);
		HttpUtils.post(DATA_TRANSFER_URL + "/password/" + PASSWORD
				+ "/class/com.monkily.model.User", json);

	}

	@Transactional
	private void importUsers(Date from, Date to) {

		String criteria = "WHERE o.createdDate < " + to.getTime()
				+ " AND o.createdDate > " + from.getTime();

		InputStream stream = HttpUtils.get(DATA_TRANSFER_URL + "/password/"
				+ PASSWORD + "/class/com.monkily.model.User/criteria/"
				+ URLEncoder.encode(criteria));

		String json = StringUtils.convertStreamToString(stream);

		Type type = new TypeToken<List<User>>() {
		}.getType();

		Gson gson = JsonUtils.createDefaultGson();
		List<User> users = gson.fromJson(json, type);

		for (User user : users) {
			DatabaseUtils.forcePersist(entityManager, user.getId(), user);
		}

	}
}
