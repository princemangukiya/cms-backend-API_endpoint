package com.college.cms.controller;

import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.ArrayList;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private com.college.cms.repository.StaffRepository staffRepository;

    @Autowired
    private com.college.cms.repository.ResultRepository resultRepository;

    // CREATE (POST): Save student with logged-in user's ID
    @PostMapping
    public ResponseEntity<?> createStudent(@RequestBody Student student, Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
            if (user != null) {
                // Link student to their own student User ID (Role 4)
                if (student.getEmail() != null && !student.getEmail().trim().isEmpty()) {
                    User studentUser = userRepository.findByEmailId(student.getEmail().trim().toLowerCase()).orElse(null);
                    if (studentUser != null && studentUser.getUser_id() != null) {
                        student.setUser_id(studentUser.getUser_id().intValue());
                    }
                }
                if (student.getUser_id() == null && user.getRoleId() != null && user.getRoleId() == 4L) {
                    if (user.getUser_id() != null) {
                        student.setUser_id(user.getUser_id().intValue());
                    }
                }

                // 👔 Real-world college logic: HOD (Role 1) can strictly only enroll students into their department course(s)
                if (user.getRoleId() != null && user.getRoleId() == 1L) {
                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    Set<Integer> hodCourseIds = new HashSet<>();
                    for (com.college.cms.entity.Staff st : staffMembers) {
                        hodCourseIds.addAll(st.getAllCourseIds());
                    }

                    if (!hodCourseIds.isEmpty() && student.getCourse_id() != null) {
                        if (!hodCourseIds.contains(student.getCourse_id())) {
                            return ResponseEntity.badRequest().body(Map.of(
                                "error", "HOD_COURSE_RESTRICTION",
                                "message", "As HOD, you can only enroll students into your department assigned course(s)!"
                            ));
                        }
                    }
                }
            }
        }
        Student saved = studentService.saveStudent(student);
        return ResponseEntity.ok(saved);
    }

    // READ ALL (GET): Role-Based Isolated Student List
    @GetMapping("/all")
    public List<Student> getAllStudents(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Student (Role 4) -> Sees only their own self record
                if (roleId == 4L) {
                    List<Student> self = studentRepository.findByEmail(user.getEmailId());
                    if (self.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        self = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (self.isEmpty() && user.getUser_id() != null) {
                        self = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (self.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        self = studentRepository.findByStudentName(user.getFull_name().trim());
                    }
                    if (!self.isEmpty()) {
                        return self;
                    }
                    return java.util.Collections.emptyList();
                }

                // 2. Professor (Role 3) -> Returns strictly students enrolled in this Professor's assigned course(s)
                if (roleId == 3L) {
                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    java.util.Set<Integer> assignedCourseIds = new java.util.HashSet<>();
                    for (com.college.cms.entity.Staff st : staffMembers) {
                        assignedCourseIds.addAll(st.getAllCourseIds());
                    }

                    if (!assignedCourseIds.isEmpty()) {
                        return studentRepository.findByCourseIdIn(new java.util.ArrayList<>(assignedCourseIds));
                    } else {
                        return java.util.Collections.emptyList();
                    }
                }

                // 3. HOD (Role 1) -> Returns strictly students enrolled in this HOD's assigned department course(s)
                if (roleId == 1L) {
                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    java.util.Set<Integer> assignedCourseIds = new java.util.HashSet<>();
                    for (com.college.cms.entity.Staff st : staffMembers) {
                        assignedCourseIds.addAll(st.getAllCourseIds());
                    }

                    if (!assignedCourseIds.isEmpty()) {
                        return studentRepository.findByCourseIdIn(new java.util.ArrayList<>(assignedCourseIds));
                    } else {
                        return java.util.Collections.emptyList();
                    }
                }
            }
        }
        // Principal (Role 2) and Super Admin -> Sees ALL college students
        return studentService.getAllStudents();
    }

    // READ BY ID (GET)
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id).orElse(null);
    }

    // HELPER: Clean up older course results when student changes program
    private void cleanUpOldCourseResults(Long studentId, Integer newCourseId) {
        try {
            List<com.college.cms.entity.Result> studentResults = resultRepository.findByStudentId(studentId);
            if (studentResults != null && !studentResults.isEmpty()) {
                for (com.college.cms.entity.Result r : studentResults) {
                    Integer subCourseId = r.getSubject() != null ? r.getSubject().getCourseId() : null;
                    if (subCourseId == null || !subCourseId.equals(newCourseId)) {
                        resultRepository.delete(r);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cleaning up old course results for student " + studentId + ": " + e.getMessage());
        }
    }

    // UPDATE (PUT): Role-scoped student update with course transition logic
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student, Principal principal) {
        Student existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 🎓 Student (Role 4) and Professor (Role 3) cannot edit students
                if (roleId == 4L || roleId == 3L) {
                    return ResponseEntity.status(403).body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "Access Denied: Only HOD and Principal are authorized to update student details and courses!"
                    ));
                }

                // 👔 HOD (Role 1): Strict ownership rule
                // "wahi student ka jo course he wo sirf wahi course ke hod hi update ya edit kar sakte he aesa"
                if (roleId == 1L) {
                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    Set<Integer> hodCourseIds = new HashSet<>();
                    for (com.college.cms.entity.Staff st : staffMembers) {
                        hodCourseIds.addAll(st.getAllCourseIds());
                    }

                    Integer studentCurrentCourseId = existingStudent.getCourse_id();
                    if (studentCurrentCourseId != null && !hodCourseIds.contains(studentCurrentCourseId)) {
                        return ResponseEntity.status(403).body(Map.of(
                            "error", "HOD_UNAUTHORIZED_STUDENT",
                            "message", "Access Denied: You can only edit students who are currently enrolled in your assigned department course(s)!"
                        ));
                    }
                }
            }
        }

        Integer oldCourseId = existingStudent.getCourse_id();
        Integer newCourseId = student.getCourse_id();

        Student updated = studentService.updateStudent(id, student);

        // 🎓 If the course changed, clean up previous course results so only new course results exist
        if (newCourseId != null && oldCourseId != null && !oldCourseId.equals(newCourseId)) {
            cleanUpOldCourseResults(id, newCourseId);
        }

        return ResponseEntity.ok(updated);
    }

    // ASSIGN / CHANGE COURSE (PUT)
    @PutMapping("/{id}/assign-course/{courseId}")
    public ResponseEntity<?> assignCourse(@PathVariable Long id, @PathVariable Integer courseId, Principal principal) {
        Student existingStudent = studentRepository.findById(id).orElse(null);
        if (existingStudent == null) {
            return ResponseEntity.notFound().build();
        }

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                if (roleId == 4L || roleId == 3L) {
                    return ResponseEntity.status(403).body(Map.of(
                        "error", "ACCESS_DENIED",
                        "message", "Access Denied: Only HOD and Principal are authorized to update student course!"
                    ));
                }

                if (roleId == 1L) {
                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    Set<Integer> hodCourseIds = new HashSet<>();
                    for (com.college.cms.entity.Staff st : staffMembers) {
                        hodCourseIds.addAll(st.getAllCourseIds());
                    }

                    Integer studentCurrentCourseId = existingStudent.getCourse_id();
                    if (studentCurrentCourseId != null && !hodCourseIds.contains(studentCurrentCourseId)) {
                        return ResponseEntity.status(403).body(Map.of(
                            "error", "HOD_UNAUTHORIZED_STUDENT",
                            "message", "Access Denied: You can only edit students who are currently enrolled in your assigned department course(s)!"
                        ));
                    }
                }
            }
        }

        Integer oldCourseId = existingStudent.getCourse_id();
        existingStudent.setCourse_id(courseId);
        Student saved = studentRepository.save(existingStudent);

        if (oldCourseId != null && !oldCourseId.equals(courseId)) {
            cleanUpOldCourseResults(id, courseId);
        }

        return ResponseEntity.ok(saved);
    }

    // DELETE (DELETE)
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "Student deleted successfully!";
    }
}
