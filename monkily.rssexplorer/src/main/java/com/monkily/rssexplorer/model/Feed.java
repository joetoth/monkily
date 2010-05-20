package com.monkily.rssexplorer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Entity
public class Feed implements Serializable {

	public final static Integer UNKNOWN = 1;
	public final static Integer DIGG = 2;

	@Id
	@Column(columnDefinition = "CHAR (36)")
	private String id;

	@ManyToOne
	private FeedSource feedSource;

	private String url;

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

	public FeedSource getFeedSource() {
		return feedSource;
	}

	public void setFeedSource(FeedSource feedSource) {
		this.feedSource = feedSource;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Feed)) {

			return false;
		}

		Feed other = (Feed) o;

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
