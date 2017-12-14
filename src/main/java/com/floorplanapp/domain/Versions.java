package com.floorplanapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name = "Versions", schema = "FloorPlanDb")
public class Versions extends ResourceSupport implements Serializable {


	@Id
	@Column(name = "id")
	@GeneratedValue
	private Long index;

	@Column(name = "version_id")
	@GeneratedValue
	private Long version_id;
	
	@Column(name = "v_number")
	@GeneratedValue
	private Long v_number;

	@Column(name = "file_name")
	@GeneratedValue
	private String file_name;
	
	public Versions() {
		super();
	}

	public Versions(Long version_id, Long v_number) {
		super();
		this.version_id = version_id;
		this.v_number = v_number;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public Long getVersion_id() {
		return version_id;
	}

	public void setVersion_id(Long version_id) {
		this.version_id = version_id;
	}

	public Long getV_number() {
		return v_number;
	}

	public void setV_number(Long v_number) {
		this.v_number = v_number;
	}

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

}
