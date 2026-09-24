package com.college.cms.repository;

import com.college.cms.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    @Query("SELECT r FROM Result r WHERE r.student.student_id = :studentId AND r.subject.subject_id = :subjectId ORDER BY r.result_id DESC")
    List<Result> findByStudentIdAndSubjectId(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);

    @Query("SELECT r FROM Result r WHERE r.student.student_id = :studentId")
    List<Result> findByStudentId(@Param("studentId") Long studentId);
}