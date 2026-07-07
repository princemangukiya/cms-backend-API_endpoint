package com.college.cms.service;

import com.college.cms.entity.Attendance;
import java.util.List;

public interface AttendanceService {
    Attendance saveAttendance(Attendance attendance);
    List<Attendance> getAllAttendance();
    Attendance getAttendanceById(Long id);
    Attendance updateAttendance(Long id, Attendance attendance);
    void deleteAttendance(Long id);
}