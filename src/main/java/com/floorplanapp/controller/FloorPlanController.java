package com.floorplanapp.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.floorplanapp.config.GlobalProperties;
import com.floorplanapp.domain.FloorPlans;
import com.floorplanapp.domain.Projects;
import com.floorplanapp.domain.UploadModel;
import com.floorplanapp.domain.Versions;
import com.floorplanapp.dto.ResponseDTO;
import com.floorplanapp.dto.FloorPlanDTO;
import com.floorplanapp.exceptions.UnauthorizedAccessException;
import com.floorplanapp.service.S3Services;

@RestController
public class FloorPlanController {

	private static final Logger log = LoggerFactory.getLogger(FloorPlanController.class);
	private GlobalProperties global;

	@Autowired
	private S3Services service;

	@Autowired
	public void setGlobal(GlobalProperties global) {
		this.global = global;
	}

	/**
	 * This function validates the request
	 * 
	 * @param secretKey
	 * @return
	 */
	private boolean validate(String secretKey) {
		if (secretKey.equals(global.getSecretKey())) {
			return true;
		}
		return false;
	}

	/**
	 * This is the API endpoint for uploading projects to database
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/floorplans/upload", headers = { "Accept=application/json" })

	public ResponseEntity<?> uploadProjectData(@RequestHeader(value = "secret-key") String secretKey,
			@RequestParam("id") String id, @RequestParam("projectName") String projectName,
			@RequestParam("displayName") String displayName, @RequestParam("fileType") String fileType,
			@RequestParam("replaceFlag") String replaceFlag, @RequestParam("file") MultipartFile file)			
					throws Exception {

		boolean valid = validate(secretKey);

		FloorPlanDTO floorPlans = null;
		if (valid) {
			log.info("Client request :: upload data ");
			floorPlans = new FloorPlanDTO(Long.parseLong(id), projectName, displayName, fileType,
					Boolean.parseBoolean(replaceFlag), file.getBytes());
			ResponseDTO error = service.uploadFile(floorPlans);
			if (error == null) {
				return new ResponseEntity<>(new ResponseDTO("Success", "File added successfully"), HttpStatus.OK);
			}

			if (error.getDesc().equalsIgnoreCase("Internal Server Error"))
				return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
			else
				return new ResponseEntity<>(error, HttpStatus.OK);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/projects/add", headers = { "Accept=application/json" })

	public ResponseEntity<?> addProject(@RequestHeader(value = "secret-key") String secretKey, @RequestParam("projectName") String projectName) throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("TODO");
			List<Projects> projects = service.addProject(projectName);
			if (projects != null) {
				return new ResponseEntity<>(projects, HttpStatus.OK);
			}
			return new ResponseEntity<>(new ResponseDTO("Error", "Project Already Exists"), HttpStatus.OK);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/projects", headers = { "Accept=application/json" })

	public ResponseEntity<?> getProjects(@RequestHeader(value = "secret-key") String secretKey) throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("TODO");
			List<Projects> projects = service.getProjects();
			return new ResponseEntity<>(projects, HttpStatus.OK);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/floorplans", headers = { "Accept=application/json" })

	public ResponseEntity<?> getFloorPlans(@RequestHeader(value = "secret-key") String secretKey, 
			@RequestParam("projectId") String projectId) throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("TODO");
			List<FloorPlans> floorPlans = service.getFloorPlansOfProject(Long.valueOf(projectId));
			return new ResponseEntity<>(floorPlans, HttpStatus.OK);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/floorHistory", headers = { "Accept=application/json" })

	public ResponseEntity<?> getFloorPlanHistory(@RequestHeader(value = "secret-key") String secretKey, 
			@RequestParam("id") String id, @RequestParam("projectId") String projectId, 
			@RequestParam("displayName") String displayName, @RequestParam("type") String type, 
			@RequestParam("fileName") String fileName) throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("TODO");
			Long vId = Long.parseLong(id);
			List<Versions> versions = service.getVersions(vId);
			return new ResponseEntity<>(versions, HttpStatus.OK);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
}
