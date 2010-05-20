package com.monkily.content.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ContentEntity implements Serializable {

	@Id
	@Column(columnDefinition = "CHAR (36)")
	String id;

	@ManyToOne
	Content content;

	@Column(nullable = false)
	String name;

	@Column(nullable = false)
	Integer occurrences;

	@Column(nullable = false)
	Double relevance;

	@Column(nullable = false)
	String value;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getReferences() {
		return occurrences;
	}

	public void setReferences(Integer references) {
		this.occurrences = references;
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
