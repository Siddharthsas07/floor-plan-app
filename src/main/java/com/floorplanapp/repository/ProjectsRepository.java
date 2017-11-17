package com.floorplanapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.floorplanapp.domain.Projects;

public interface ProjectsRepository  extends JpaRepository<Projects, Long>, CrudRepository<Projects, Long>{
	
	@Query(value = "select * from projects where project_name = ?1", nativeQuery = true)
	Projects findByProjectName (String projectName);
}
