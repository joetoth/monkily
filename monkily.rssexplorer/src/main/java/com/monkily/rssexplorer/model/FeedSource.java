package com.monkily.rssexplorer.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class FeedSource {

	public final static Integer UNKNOWN = 1;

	public final static Integer DIGG = 2;

	@Id
	Integer id;

	String name;

	public FeedSource() {

	}

	public FeedSource(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
