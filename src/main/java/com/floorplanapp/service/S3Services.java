package com.floorplanapp.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.floorplanapp.config.AWSConfiguration;
import com.floorplanapp.config.GlobalProperties;
import com.floorplanapp.domain.FloorPlans;
import com.floorplanapp.domain.Projects;
import com.floorplanapp.domain.Versions;
import com.floorplanapp.dto.ResponseDTO;
import com.floorplanapp.dto.FloorPlanDTO;
import com.floorplanapp.repository.FloorPlanRepository;
import com.floorplanapp.repository.ProjectsRepository;
import com.floorplanapp.repository.VersionsRepository;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFRenderer;

@Service
public class S3Services {

	private static final Logger log = LoggerFactory.getLogger(S3Services.class);
	private GlobalProperties global;

	@Autowired
	private AWSConfiguration awsConfiguration;
	
	@Autowired
	private AmazonS3Client amazonS3Client;

	@Autowired
	private ProjectsRepository projectsRepository;

	@Autowired
	private FloorPlanRepository floorPlanRepository;

	@Autowired
	private VersionsRepository versionsRepository;
	
	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	private PutObjectResult upload(InputStream inputStream, String uploadKey) {
		
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, uploadKey, inputStream, new ObjectMetadata());
		putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
		PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
		IOUtils.closeQuietly(inputStream);
		return putObjectResult;
	}

	private InputStream getObject (String downloadKey) {
		AmazonS3 s3Client = awsConfiguration.amazonS3Client(awsConfiguration.basicAWSCredentials());
		S3Object object = s3Client.getObject(
		                  new GetObjectRequest(bucket, downloadKey));
		InputStream objectData = object.getObjectContent();
		
//		S3Object s3object = s3.getObject(new GetObjectRequest(bucket, downloadKey));
//				InputStream stream = s3object.getObjectContent();
		
		return objectData;
	}
	
	private boolean checkFloorPlanExists(Long id, String fileName) {
		
		FloorPlans floorPlan = floorPlanRepository.findByProjectIdAndFileName(id, fileName);
		return floorPlan != null ? true : false;
	}

	public ResponseDTO uploadFile(FloorPlanDTO floorPlans) throws IOException, InterruptedException {
		
		if (!checkFileSupported(floorPlans.getFileType())) {
			// TODO return with some valid error : File type not supported
			return new ResponseDTO("Error", "File not supported");
		}
		
		if (!checkProjectByID(floorPlans.getId())) {
			return new ResponseDTO("Error", "Project " + floorPlans.getDisplayName() + " does not exists");
		}
		FloorPlans fp = getFloorPlan(floorPlans.getDisplayName()+'.'+floorPlans.getFileType(), floorPlans.getId());
		if (fp != null) {
			Versions v = getLatestVersion(fp.getIndex());
			if (v == null) {
				v = new Versions(fp.getIndex(), 1L);
			} else {
				v = new Versions(fp.getIndex(), v.getV_number()+1);
			}

			String newFileName = fp.getDisplayName()+"_" + (v.getV_number()) + "." + fp.getType();
			v.setFile_name(newFileName);
			versionsRepository.save(v);
			fp.setFileName(newFileName);
			String fileName = floorPlans.getDisplayName() + "." + floorPlans.getFileType();
			String old_path = floorPlans.getProjectName() + "/" + fileName;
			InputStream input = getObject(old_path);
			String path = floorPlans.getProjectName() + "/" + fp.getFileName();
			upload(input, path);
		}
		
		String fileName = floorPlans.getDisplayName() + "." + floorPlans.getFileType();
		String path = floorPlans.getProjectName() + "/" + fileName;
		String thumbPath = floorPlans.getProjectName() + "/" + floorPlans.getDisplayName() + "_thumb.png";
		String largePath = floorPlans.getProjectName() + "/" + floorPlans.getDisplayName() + "_large.png";

		InputStream targetStream = null;
		if (!checkFloorPlanExists(floorPlans.getId(), fileName)) {
			try {
				byte[] bytes = floorPlans.getBytes();
				targetStream = new ByteArrayInputStream(bytes);
				upload(targetStream, path);
				uploadPNG(100, 100, thumbPath, bytes, floorPlans.getFileType());
				uploadPNG(2000, 2000, largePath, bytes, floorPlans.getFileType());
				addFloorPlanToDB(floorPlans, fileName);
				return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (floorPlans.isReplaceFlag()) {
			try {
				byte[] bytes = floorPlans.getBytes();
				targetStream = new ByteArrayInputStream(bytes);
				upload(targetStream, path);
				uploadPNG(100, 100, thumbPath, bytes, floorPlans.getFileType());
				uploadPNG(2000, 2000, largePath, bytes, floorPlans.getFileType());
				return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			return new ResponseDTO("Error", "File already exists");
		}
		return new ResponseDTO("Error", "Internal Server Error");
	}

	private Versions getLatestVersion(Long versionId) {
		return versionsRepository.findByVersionId(versionId);
	}

	private FloorPlans getFloorPlan(String string, Long id) {
		FloorPlans fp = floorPlanRepository.findByProjectIdAndFileName(id, string);
		return fp;
	}

	private boolean checkProjectByID(Long id) {
		Projects project = projectsRepository.findOne(id);
		return project == null ? false : true;
	}

	private boolean checkFileSupported(String fileType) {
		if (fileType.equalsIgnoreCase("pdf") || fileType.equalsIgnoreCase("jpg")) {
			return true;
		} 
		return false;
	}

	private void addFloorPlanToDB(FloorPlanDTO floorPlanDTO, String fileName) {
		
		FloorPlans floorPlan = new FloorPlans(floorPlanDTO.getId(), floorPlanDTO.getDisplayName(),
				floorPlanDTO.getFileType(), fileName);
		floorPlanRepository.save(floorPlan);
	}

	public List<Projects> getProjects() {
		
		List<Projects> projects = projectsRepository.findAll();
		return projects;
	}

	public boolean isProjectExists(String projectName) {
		
		Projects project = projectsRepository.findByProjectName(projectName);
		return project != null ? true : false;
	}

	public List<Projects> addProject(String projectName) {
		if (!isProjectExists(projectName)) {
			Projects obj = new Projects();
			obj.setProjectName(projectName);
			projectsRepository.save(obj);
			return getProjects();
		}
		return null;
	}

	public List<FloorPlans> getFloorPlans() {
		
		List<FloorPlans> floorPlans = floorPlanRepository.findAll();
		return floorPlans;
	}

	public List<FloorPlans> getFloorPlansOfProject(Long projectId) {
		
		List<FloorPlans> floorPlans = floorPlanRepository.findByProjectId(projectId);
		return floorPlans;
	}

	public ResponseEntity<byte[]> download(String key) throws IOException {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
		S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
		S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
		byte[] bytes = IOUtils.toByteArray(objectInputStream);

		String fileName = URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		httpHeaders.setContentLength(bytes.length);
		httpHeaders.setContentDispositionFormData("attachment", fileName);

		return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}

	public void uploadPNG(int width, int height, String name, byte[] bytes, String type)
			throws IOException, InterruptedException {

		BufferedImage img = null;
		if (type.equalsIgnoreCase("pdf")) {
			ByteBuffer buf = ByteBuffer.wrap(bytes);
			PDFFile pdffile = new PDFFile(buf);
			PDFPage page = pdffile.getPage(0);
			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			Graphics2D g2 = img.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Rectangle rect = new Rectangle(0, 0, width, height);
			PDFRenderer renderer = new PDFRenderer(page, g2, rect, null, Color.WHITE);
			page.waitForFinish();
			renderer.run();
		} else if (type.equalsIgnoreCase("jpg")) {
			InputStream in = new ByteArrayInputStream(bytes);
			img = ImageIO.read(in);
		}
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(img, "png", os);
		byte[] buffer = os.toByteArray();
		InputStream is = new ByteArrayInputStream(buffer);
		upload(is, name);

	}
	
	public List<Versions> getVersions(Long versionId) {
		
		List<Versions> versions = versionsRepository.findByVersion(versionId);
		return versions;
	}

}
