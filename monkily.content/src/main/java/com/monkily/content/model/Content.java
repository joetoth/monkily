package com.monkily.content.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.monkily.utils.GUID;

@Entity
public class Content implements Serializable {

	@Id
	@Column(columnDefinition = "CHAR (36)")
	private String id;

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
	private List<ContentUrl> urls = new ArrayList<ContentUrl>();

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
	private List<ContentTopic> topics = new ArrayList<ContentTopic>();

	@OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
	private List<ContentEntity> entities = new ArrayList<ContentEntity>();

	@Column(nullable = false, columnDefinition = "VARCHAR(1024)")
	private String title;

	@Column(nullable = false, columnDefinition = "VARCHAR(2048)")
	private String description;

	@Column(nullable = false)
	private ContentType contentType;

	@Column(columnDefinition = "LONGBLOB")
	private byte[] data;

	@Column(nullable = false)
	private Integer wordCount;

	@Column(nullable = false)
	private Date publishedDate;

	@Column(nullable = false)
	private Date createdDate;

	@Column(nullable = false)
	@Version
	private Date updatedDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}

	public Integer getWordCount() {
		return wordCount;
	}

	public void setWordCount(Integer wordCount) {
		this.wordCount = wordCount;
	}

	public void addUrl(String url) {
		ContentUrl contentUrl = new ContentUrl();
		contentUrl.setId(GUID.generate());
		contentUrl.setContent(this);
		contentUrl.setUrl(url);
		contentUrl.setCreatedDate(new Date());
		getUrls().add(contentUrl);
	}

	public List<ContentUrl> getUrls() {
		return urls;
	}

	public void setUrls(List<ContentUrl> urls) {
		this.urls = urls;
	}

	public void addTopic(String name, Double relevance) {
		ContentTopic topic = new ContentTopic();
		topic.setId(GUID.generate());
		topic.setContent(this);
		topic.setName(name);
		topic.setRelevance(relevance);
		getTopics().add(topic);
	}

	public List<ContentTopic> getTopics() {
		return topics;
	}

	public void setTopics(List<ContentTopic> topics) {
		this.topics = topics;
	}

	public void addEntity(String name, String value, Integer references,
			Double relevance) {
		ContentEntity entity = new ContentEntity();
		entity.setId(GUID.generate());
		entity.setContent(this);
		entity.setReferences(references);
		entity.setName(name);
		entity.setRelevance(relevance);
		entity.setValue(value);
		getEntities().add(entity);
	}

	public List<ContentEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<ContentEntity> entities) {
		this.entities = entities;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Content)) {

			return false;
		}

		Content other = (Content) o;

		// if the id is missing, return false
		if (id == null)
			return false;

		// equivalence by id
		return id.equals(other.getId());
	}

	public int hashCode() {
		if (id != null) {
			return id.hashCode();
		} else {
			return super.hashCode();
		}
	}

	public String toString() {
		return this.getClass().getName() + "[id=" + id + "]";
	}

}
