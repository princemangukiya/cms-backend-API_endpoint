package com.college.cms.service;

import com.college.cms.entity.Staff;
import java.util.List;
import java.util.Optional;

public interface StaffService {
    Staff addStaff(Staff staff);
    List<Staff> getAllStaff();
    Optional<Staff> getStaffById(Integer id);
    Staff updateStaff(Integer id, Staff staff);
    void deleteStaff(Integer id);
}