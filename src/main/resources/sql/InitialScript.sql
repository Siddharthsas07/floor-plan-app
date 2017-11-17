CREATE TABLE `projects` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Project_Name` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

CREATE TABLE `floor_plans` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `display_name` varchar(255) DEFAULT NULL,
  `project_id` int(11) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `floor_plans`
add foreign key(project_id) references projects (id);

ALTER TABLE `floor_plans`
ADD CONSTRAINT uk_fp_1 UNIQUE (display_name, project_id, type); 

-- 
-- drop table `floor_plans`;
-- drop table `FloorPlans`;
-- drop table `projects`
-- 

