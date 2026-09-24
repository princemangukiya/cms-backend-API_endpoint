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
        Optional<Staff> existingOpt = staffRepository.findById(id);
        if (existingOpt.isPresent()) {
            Staff existing = existingOpt.get();
            if (staff.getStaffname() != null) existing.setStaffname(staff.getStaffname());
            if (staff.getDesignation() != null) existing.setDesignation(staff.getDesignation());
            if (staff.getMobileno() != null) existing.setMobileno(staff.getMobileno());
            if (staff.getGender() != null) existing.setGender(staff.getGender());
            if (staff.getAddress() != null) existing.setAddress(staff.getAddress());
            if (staff.getDob() != null) existing.setDob(staff.getDob());
            if (staff.getEmail() != null) existing.setEmail(staff.getEmail());
            if (staff.getJoiningdate() != null) existing.setJoiningdate(staff.getJoiningdate());
            if (staff.getSalary() != null) existing.setSalary(staff.getSalary());
            existing.setCourseId(staff.getCourseId());
            existing.setCourseIds(staff.getCourseIds());
            if (staff.getUserId() != null) existing.setUserId(staff.getUserId());
            return staffRepository.save(existing);
        }
        return null;
    }

    @Override
    public void deleteStaff(Integer id) { staffRepository.deleteById(id); }
}