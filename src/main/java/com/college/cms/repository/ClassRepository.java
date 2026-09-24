package com.college.cms.repository;

import com.college.cms.entity.ClassMgmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<ClassMgmt, Long> {

    @Query("SELECT c FROM ClassMgmt c WHERE c.course_id IN :courseIds")
    List<ClassMgmt> findByCourseIdIn(@Param("courseIds") List<Integer> courseIds);

    @Query("SELECT c FROM ClassMgmt c WHERE c.course_id = :courseId")
    List<ClassMgmt> findByCourseId(@Param("courseId") Integer courseId);
}