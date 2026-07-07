package com.college.cms.service.impl;

import com.college.cms.entity.Attendance;
import com.college.cms.repository.AttendanceRepository;
import com.college.cms.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance not found with ID: " + id));
    }

    @Override
    public Attendance updateAttendance(Long id, Attendance updated) {
        Attendance existing = getAttendanceById(id);
        existing.setAttendancedate(updated.getAttendancedate());
        existing.setIntime(updated.getIntime());
        existing.setOuttime(updated.getOuttime());
        existing.setUserid(updated.getUserid());
        return attendanceRepository.save(existing);
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
}