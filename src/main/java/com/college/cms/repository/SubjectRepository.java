package com.college.cms.repository;

import com.college.cms.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourseId(Integer courseId);
    List<Subject> findByCourseIdIn(List<Integer> courseIds);
    List<Subject> findByStaffId(Integer staffId);
    List<Subject> findByStaffIdIn(List<Integer> staffIds);
}