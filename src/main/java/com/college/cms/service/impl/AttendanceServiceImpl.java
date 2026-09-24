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
        // Yahan se purane records delete karne wala code hata diya hai
        // taaki ab saari history (aaj ki, kal ki, purani) save rahe aur user sab dekh sake.
        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getAttendanceByAddedBy(String addedBy) {
        return attendanceRepository.findByAddedBy(addedBy);
    }

    @Override
    public List<Attendance> getAttendanceByUserId(Long userid) {
        return attendanceRepository.findByUserid(userid != null ? userid.intValue() : null);
    }

    @Override
    public List<Attendance> getAttendanceByUserIdOrAddedBy(Long userid, String addedBy) {
        Integer uId = userid != null ? userid.intValue() : null;
        if (uId != null && addedBy != null && !addedBy.trim().isEmpty()) {
            return attendanceRepository.findByUseridOrAddedBy(uId, addedBy);
        } else if (uId != null) {
            return attendanceRepository.findByUserid(uId);
        } else if (addedBy != null && !addedBy.trim().isEmpty()) {
            return attendanceRepository.findByAddedBy(addedBy);
        }
        return List.of();
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