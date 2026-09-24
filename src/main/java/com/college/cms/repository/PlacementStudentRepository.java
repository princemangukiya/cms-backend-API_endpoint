package com.college.cms.repository;

import com.college.cms.entity.PlacementStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlacementStudentRepository extends JpaRepository<PlacementStudent, Long> {

    @Query("SELECT p FROM PlacementStudent p WHERE p.student_id = :studentId")
    List<PlacementStudent> findByStudentId(@Param("studentId") Integer studentId);

    @Query("SELECT p FROM PlacementStudent p WHERE p.student_id IN :studentIds")
    List<PlacementStudent> findByStudentIdIn(@Param("studentIds") List<Integer> studentIds);
}