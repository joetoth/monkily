package com.monkily.prediction.service;

import java.util.Date;
import java.util.List;

import com.monkily.prediction.model.Prediction;

public interface PredictionService {

	/**
	 * Predict interest of content for all registered users
	 * 
	 * @param contentId
	 */
	public void predictInterest(String contentId);

	// /**
	// * Predict interest of content, between start and end date, for the given
	// * userId<br>
	// * (Used for newly registered users)
	// *
	// * @param userId
	// * @param startDate
	// * @param endDate
	// */
	// public void predictInterestByUserId(String userId, Date startDate,
	// Date endDate);

	/**
	 * 
	 * @param userId
	 * @param contentId
	 */
	public void predictInterest(String userId, String contentId);

	/**
	 * Find predictions
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public List<Prediction> findPredictions(Date from, Date to);
	
	public void predictAllInterest();
}
