package com.college.cms.repository;

import com.college.cms.entity.PlacementStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacementStudentRepository extends JpaRepository<PlacementStudent, Long> {

}