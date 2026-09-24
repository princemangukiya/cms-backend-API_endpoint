package com.college.cms.controller;

import com.college.cms.dto.ResultResponseDTO;
import com.college.cms.entity.Result;
import com.college.cms.entity.Staff;
import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.StaffRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/results", "/api/results"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ResultController {

    @Autowired
    private ResultService resultService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping({"/add", "/save", ""})
    public ResponseEntity<?> saveResult(@RequestBody Result result, Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 👔 HOD (Role 1) Cannot publish results (Professors publish results for their subjects)
                if (roleId == 1L) {
                    return ResponseEntity.status(403).body("Access Denied: HOD cannot publish subject results. In college hierarchy, individual professors submit marks for their subjects.");
                }

                // 🎓 Student (Role 4) Cannot publish results
                if (roleId == 4L) {
                    return ResponseEntity.status(403).body("Access Denied: Students cannot publish results.");
                }

                // 🏛️ Principal (Role 2) Cannot publish results (Teaching professors submit marks)
                if (roleId == 2L) {
                    return ResponseEntity.status(403).body("Access Denied: Principal oversees academic records. Marks are submitted and published directly by teaching professors.");
                }
            }
        }

        try {
            resultService.saveResult(result);
            return ResponseEntity.ok("Result Saved Successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping({"", "/all"})
    public ResponseEntity<?> getAllResults(Principal principal) {
        try {
            List<ResultResponseDTO> allResults = resultService.getAllResultsDTO();

            if (principal != null) {
                String loggedInEmail = principal.getName();
                User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

                if (user != null && user.getRoleId() != null) {
                    Long roleId = user.getRoleId();

                    // 1. Principal (Role 2) -> College Head, sees all results
                    if (roleId == 2L) {
                        return ResponseEntity.ok(allResults);
                    }

                    // 2. HOD (Role 1) -> Department Head, sees ONLY results of their course/department!
                    if (roleId == 1L) {
                        List<Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                        if (staffMembers.isEmpty() && user.getUser_id() != null) {
                            staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                        }
                        if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                            staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                        }

                        List<Integer> hodCourseIds = new ArrayList<>();
                        for (Staff st : staffMembers) {
                            hodCourseIds.addAll(st.getAllCourseIds());
                        }

                        if (!hodCourseIds.isEmpty()) {
                            List<ResultResponseDTO> hodResults = allResults.stream()
                                    .filter(r -> r.getCourseId() != null && hodCourseIds.contains(r.getCourseId()))
                                    .collect(Collectors.toList());
                            return ResponseEntity.ok(hodResults);
                        } else {
                            return ResponseEntity.ok(allResults);
                        }
                    }

                    // 3. Professor (Role 3) -> Faculty, sees results of their assigned subjects or course
                    if (roleId == 3L) {
                        List<Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                        if (staffMembers.isEmpty() && user.getUser_id() != null) {
                            staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                        }
                        if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                            staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                        }

                        List<Integer> profStaffIds = new ArrayList<>();
                        List<Integer> profCourseIds = new ArrayList<>();
                        for (Staff st : staffMembers) {
                            if (st.getStaffid() != null) {
                                profStaffIds.add(st.getStaffid());
                            }
                            profCourseIds.addAll(st.getAllCourseIds());
                        }

                        List<ResultResponseDTO> profResults = allResults.stream()
                                .filter(r -> (r.getStaffId() != null && profStaffIds.contains(r.getStaffId())) ||
                                             (r.getCourseId() != null && profCourseIds.contains(r.getCourseId())))
                                .collect(Collectors.toList());
                        return ResponseEntity.ok(profResults);
                    }

                    // 4. Student (Role 4) -> Sees only their own marksheet for their CURRENT ENROLLED COURSE
                    if (roleId == 4L) {
                        List<Student> students = studentRepository.findByEmail(user.getEmailId());
                        if (students.isEmpty() && user.getUser_id() != null) {
                            students = studentRepository.findByUserId(user.getUser_id().intValue());
                        }
                        if (students.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                            students = studentRepository.findByMobileNo(user.getMobile_no().trim());
                        }

                        List<Long> studentIds = new ArrayList<>();
                        List<Integer> currentCourseIds = new ArrayList<>();
                        for (Student s : students) {
                            if (s.getStudent_id() != null) {
                                studentIds.add(s.getStudent_id());
                            }
                            if (s.getCourse_id() != null) {
                                currentCourseIds.add(s.getCourse_id());
                            }
                        }

                        List<ResultResponseDTO> studentResults = allResults.stream()
                                .filter(r -> {
                                    boolean isThisStudent = (r.getStudentId() != null && studentIds.contains(r.getStudentId())) ||
                                                            (r.getUserId() != null && user.getUser_id() != null && r.getUserId().equals(user.getUser_id())) ||
                                                            (r.getStudentEmail() != null && r.getStudentEmail().equalsIgnoreCase(user.getEmailId()));
                                    if (!isThisStudent) return false;

                                    // Real-world college logic:
                                    // A student only sees examination results of their PRESENT enrolled course!
                                    // Historical/demo experiments from older courses are excluded.
                                    if (!currentCourseIds.isEmpty()) {
                                        return r.getCourseId() != null && currentCourseIds.contains(r.getCourseId());
                                    }
                                    return true;
                                })
                                .collect(Collectors.toList());
                        return ResponseEntity.ok(studentResults);
                    }
                }
            }

            return ResponseEntity.ok(allResults);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}