package com.college.cms.service.impl;

import com.college.cms.entity.Staff;
import com.college.cms.repository.StaffRepository;
import com.college.cms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public Staff addStaff(Staff staff) {
        // The user_id should be present in the staff object passed from the controller
        // Ensure that your JSON request from the frontend includes "user_id"
        return staffRepository.save(staff);
    }
}