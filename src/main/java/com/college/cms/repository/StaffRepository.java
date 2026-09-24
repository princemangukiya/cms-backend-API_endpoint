package com.college.cms.repository;

import com.college.cms.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    List<Staff> findByEmail(String email);
    List<Staff> findByUserId(Integer userId);
    List<Staff> findByMobileno(String mobileno);
}