package com.floorplanapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.floorplanapp.domain.FloorPlans;
import com.floorplanapp.domain.Projects;

public interface FloorPlanRepository  extends JpaRepository<FloorPlans, Long>{
	
	@Query(value = "select * from floor_plans", nativeQuery = true)
	public List<FloorPlans> find();
	
	@Query(value = "select * from floor_plans where project_id = ?1", nativeQuery = true)
	List<FloorPlans> findByProjectId(Long id);
	
	@Query(value = "select * from floor_plans where project_id = ?1 and file_name = ?2", nativeQuery = true)
	FloorPlans findByProjectIdAndFileName(Long projectId, String fileName);
	
}
