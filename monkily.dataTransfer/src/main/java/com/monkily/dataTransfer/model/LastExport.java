package com.monkily.dataTransfer.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class LastExport {

	public final static Integer ID = 1;

	@Id
	Integer id;

	Date lastExport;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getLastExport() {
		return lastExport;
	}

	public void setLastExport(Date lastExport) {
		this.lastExport = lastExport;
	}

}
