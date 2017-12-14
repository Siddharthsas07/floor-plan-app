package com.floorplanapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.floorplanapp.domain.FloorPlans;
import com.floorplanapp.domain.Versions;

public interface VersionsRepository  extends JpaRepository<Versions, Long>{
	
	@Query(value = "select * from floor_plans", nativeQuery = true)
	public List<FloorPlans> find();
	
	@Query(value = "select * from versions where version_id = ?1", nativeQuery = true)
	List<Versions> findByVersion(Long id);
	
	@Query(value = "select * from versions where version_id = ?1 order by v_number desc limit 1", nativeQuery = true)
	Versions findByVersionId(Long versionId);
	
}
