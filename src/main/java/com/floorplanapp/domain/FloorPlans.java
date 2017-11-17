package com.floorplanapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name = "FloorPlans", schema = "FloorPlanDb")
public class FloorPlans extends ResourceSupport implements Serializable {

	@Id
	@Column(name = "id")
	@GeneratedValue
	private Long index;

	@Column(name = "project_id")
	private Long projectId;

	@Column(name = "display_name")
	private String displayName;

	@Column(name = "type")
	private String type;
	
	@Column(name = "file_name")
	private String fileName;
	
	public FloorPlans() {
		super();
	}

	public FloorPlans(Long projectId, String displayName, String type, String fileName) {
		super();
		this.projectId = projectId;
		this.displayName = displayName;
		this.type = type;
		this.fileName = fileName;
	}

	private static final long serialVersionUID = 1L;



	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
