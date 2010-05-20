package com.opencalais.client.model;

import java.io.Serializable;

public class Topic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3686167193746318672L;

	Double score;

	String taxonomy;

	String name;

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getTaxonomy() {
		return taxonomy;
	}

	public void setTaxonomy(String taxonomy) {
		this.taxonomy = taxonomy;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
