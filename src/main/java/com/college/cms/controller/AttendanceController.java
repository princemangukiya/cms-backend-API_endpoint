package com.college.cms.controller;

import com.college.cms.entity.Attendance;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "http://localhost:5173")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserRepository userRepository;

    // 🟢 1. Save Attendance with Strict Target Validation
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody Attendance attendance, Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User loggedInUser = userRepository.findByEmailId(loggedInEmail).orElse(null);

            // 1. Set Added By to User's Real Name (not Gmail)
            String adderName = loggedInEmail;
            if (loggedInUser != null && loggedInUser.getFull_name() != null && !loggedInUser.getFull_name().trim().isEmpty()) {
                adderName = loggedInUser.getFull_name().trim();
            }
            attendance.setAddedBy(adderName);

            // 2. Date must be Current Date (Today)
            if (attendance.getAttendancedate() == null || !attendance.getAttendancedate().equals(java.time.LocalDate.now())) {
                attendance.setAttendancedate(java.time.LocalDate.now());
            }

            // 3. Time Validation: Out Time must be strictly after In Time
            if (attendance.getIntime() != null && attendance.getOuttime() != null) {
                if (!attendance.getOuttime().isAfter(attendance.getIntime())) {
                    return ResponseEntity.badRequest().body("Validation Error: Out Time (" + attendance.getOuttime() + ") must be after In Time (" + attendance.getIntime() + ")!");
                }
            }

            if (loggedInUser != null && loggedInUser.getRoleId() != null) {
                Long loggedInRoleId = loggedInUser.getRoleId();

                if (attendance.getUserid() == null) {
                    return ResponseEntity.badRequest().body("User ID is required.");
                }

                // Check Target User from Database
                User targetUser = userRepository.findById(attendance.getUserid().longValue()).orElse(null);
                if (targetUser == null) {
                    return ResponseEntity.badRequest().body("User ID " + attendance.getUserid() + " database me exist nahi karta!");
                }

                Long targetRoleId = targetUser.getRoleId();

                // Principal (Role 2) sirf HOD (Role 1) ki laga sakta hai
                if (loggedInRoleId == 2L && (targetRoleId == null || targetRoleId != 1L)) {
                    return ResponseEntity.badRequest().body("Validation Error: Principal sirf HOD (Role ID 1) ki attendance laga sakta hai! Yeh User ID HOD nahi hai.");
                }

                // HOD (Role 1) sirf Professor (Role 3) ki laga sakta hai
                if (loggedInRoleId == 1L && (targetRoleId == null || targetRoleId != 3L)) {
                    return ResponseEntity.badRequest().body("Validation Error: HOD sirf Professor (Role ID 3) ki attendance laga sakta hai! Yeh User ID Professor nahi hai.");
                }

                // Professor (Role 3) sirf Student (Role 4) ki laga sakta hai
                if (loggedInRoleId == 3L && (targetRoleId == null || targetRoleId != 4L)) {
                    return ResponseEntity.badRequest().body("Validation Error: Professor sirf Student (Role ID 4) ki attendance laga sakta hai! Yeh User ID Student nahi hai.");
                }

                // Student (Role 4) attendance nahi laga sakta
                if (loggedInRoleId == 4L) {
                    return ResponseEntity.badRequest().body("Validation Error: Students attendance record nahi kar sakte.");
                }
            }
        }
        return ResponseEntity.ok(attendanceService.saveAttendance(attendance));
    }

    // 🟢 2. Separate Attendance for Everyone, Principal sees ALL
    @GetMapping("/all")
    public ResponseEntity<List<Attendance>> getAll(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();


                // Principal (Role 2) -> Poore college ka complete directory
                if (roleId == 2L) {
                    return ResponseEntity.ok(attendanceService.getAllAttendance());
                }
                // HOD (1), Professor (3) -> Apni attendance + apne dwara li gayi student attendance
                else if (roleId == 1L || roleId == 3L) {
                    String adderName = (user.getFull_name() != null && !user.getFull_name().trim().isEmpty())
                            ? user.getFull_name().trim()
                            : loggedInEmail;
                    return ResponseEntity.ok(attendanceService.getAttendanceByUserIdOrAddedBy(user.getUser_id(), adderName));
                }
                // Student (4) -> Sirf apni attendance
                else {
                    return ResponseEntity.ok(attendanceService.getAttendanceByUserId(user.getUser_id()));
                }
            }
        }
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }
}