package com.monkily.interest.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ContentView implements Serializable {

	@Id
	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String id;

	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String userId;

	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String contentId;

	@Column(nullable = false)
	private Long timeViewing;

	@Column(nullable = false)
	private Date timeRead;

	@Column(columnDefinition = "VARCHAR (15)", nullable = false)
	private String ip;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Long getTimeViewing() {
		return timeViewing;
	}

	public void setTimeViewing(Long timeViewing) {
		this.timeViewing = timeViewing;
	}

	public Date getTimeRead() {
		return timeRead;
	}

	public void setTimeRead(Date timeRead) {
		this.timeRead = timeRead;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof ContentView)) {

			return false;
		}

		ContentView other = (ContentView) o;

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
