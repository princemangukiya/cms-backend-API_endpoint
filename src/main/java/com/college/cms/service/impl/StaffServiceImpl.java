package com.college.cms.service.impl;

import com.college.cms.entity.Staff;
import com.college.cms.repository.StaffRepository;
import com.college.cms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Override
    public Staff addStaff(Staff staff) { return staffRepository.save(staff); }

    @Override
    public List<Staff> getAllStaff() { return staffRepository.findAll(); }

    @Override
    public Optional<Staff> getStaffById(Integer id) { return staffRepository.findById(id); }

    @Override
    public Staff updateStaff(Integer id, Staff staff) {
        if (staffRepository.existsById(id)) {
            staff.setStaffid(id);
            return staffRepository.save(staff);
        }
        return null;
    }

    @Override
    public void deleteStaff(Integer id) { staffRepository.deleteById(id); }
}