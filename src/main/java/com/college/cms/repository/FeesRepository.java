package com.college.cms.repository;

import com.college.cms.entity.Fees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeesRepository extends JpaRepository<Fees, Long> {

    List<Fees> findByStudentId(Long studentId);

    List<Fees> findByStudentIdIn(List<Long> studentIds);
}