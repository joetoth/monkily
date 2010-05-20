package com.monkily.interest.service;

import java.util.List;

import com.monkily.interest.model.ContentView;

public interface InterestService {

	public final static String BUCKET_LOGS_NAME = "monkily-logs";

	public final static String BUCKET_LOGS_PROCESSED_NAME = "monkily-logs-processed";

	public final static String UNKNOWN_USER_ID = "00000000-0000-0000-0000-000000000000";

	public void processLogs();

	public List<ContentView> getViewsByUserId(String userId);

}
