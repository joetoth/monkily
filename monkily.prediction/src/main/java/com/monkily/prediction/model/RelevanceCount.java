package com.monkily.prediction.model;

import java.io.Serializable;

public class RelevanceCount implements Serializable {

	private Integer count;

	private Double relevance;

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Double getRelevance() {
		return relevance;
	}

	public void setRelevance(Double relevance) {
		this.relevance = relevance;
	}

}
