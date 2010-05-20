package com.opencalais.client.model;

import java.io.Serializable;

public class MetadataElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5962376946728788483L;

	String name;

	Integer count;

	Double relevance;

	String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
