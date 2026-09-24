package com.college.cms.service;

import com.college.cms.entity.Attendance;
import java.util.List;

public interface AttendanceService {
    Attendance saveAttendance(Attendance attendance);
    List<Attendance> getAllAttendance();

    // Specific user ke records lane ke liye (addedBy ke liye)
    List<Attendance> getAttendanceByAddedBy(String addedBy);

    // Student/Professor ke liye userid ke base par record lane ke liye
    List<Attendance> getAttendanceByUserId(Long userid);

    // Apni attendance + apne dwara bhari gayi attendance fetch karne ke liye
    List<Attendance> getAttendanceByUserIdOrAddedBy(Long userid, String addedBy);

    Attendance getAttendanceById(Long id);
    Attendance updateAttendance(Long id, Attendance attendance);
    void deleteAttendance(Long id);
}