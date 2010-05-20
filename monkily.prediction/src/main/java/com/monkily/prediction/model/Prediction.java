package com.monkily.prediction.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userId",
		"contentId" }))
public class Prediction implements Serializable {

	@Id
	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String id;

	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String userId;

	@Column(columnDefinition = "CHAR (36)", nullable = false)
	private String contentId;

	@Column(nullable = false)
	private Double interestScore;

	private boolean viewed;

	@Column(nullable = false)
	private Date createdDate;

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

	public Double getInterestScore() {
		return interestScore;
	}

	public void setInterestScore(Double interestScore) {
		this.interestScore = interestScore;
	}

	public boolean isViewed() {
		return viewed;
	}

	public void setViewed(boolean viewed) {
		this.viewed = viewed;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Prediction)) {

			return false;
		}

		Prediction other = (Prediction) o;

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
