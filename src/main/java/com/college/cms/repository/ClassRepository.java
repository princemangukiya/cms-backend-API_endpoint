package com.college.cms.repository;

import com.college.cms.entity.ClassMgmt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassMgmt, Long> {

}