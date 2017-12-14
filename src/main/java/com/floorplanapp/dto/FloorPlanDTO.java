package com.floorplanapp.dto;

public class FloorPlanDTO {
	
	private Long id;
	private String projectName;
	private String displayName;
	private String fileType;
	private boolean replaceFlag;
	private byte[] bytes;

	public FloorPlanDTO() {
		super();
	}
	
	public FloorPlanDTO(Long id, String projectName, String displayName, String fileType, boolean replaceFlag, byte[] bytes) {
		super();
		this.id = id;
		this.projectName = projectName;
		this.displayName = displayName;
		this.fileType = fileType;
		this.replaceFlag = replaceFlag;
		this.bytes = bytes;
	}
	
	public FloorPlanDTO(FloorPlanDTO object) {
		super();
		this.id = object.getId();
		this.projectName = object.getProjectName();
		this.displayName = object.getDisplayName();
		this.fileType = object.getFileType();
		this.replaceFlag = true;
		this.bytes = object.getBytes();
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isReplaceFlag() {
		return replaceFlag;
	}

	public void setReplaceFlag(boolean replaceFlag) {
		this.replaceFlag = replaceFlag;
	}
	
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
