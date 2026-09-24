package com.college.cms.controller;

import com.college.cms.entity.ClassMgmt;
import com.college.cms.service.ClassService;

import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.ClassRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping({"/api/class-management", "/class-management"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000", "http://127.0.0.1:5173"})
public class ClassController {

    @Autowired
    private ClassService classService;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.college.cms.repository.StaffRepository staffRepository;

    // ================= POST =================

    @PostMapping({"", "/add", "/save"})
    public ResponseEntity<?> saveClass(@RequestBody ClassMgmt classMgmt, Principal principal) {

        if (classMgmt.getClass_name() == null || classMgmt.getClass_name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Class Name is required.");
        }

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();
                // If HOD (Role 1), auto-assign course if single, or enforce assigned courses
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

                    if (classMgmt.getCourse_id() == null) {
                        if (hodCourseIds.size() == 1) {
                            classMgmt.setCourse_id(hodCourseIds.iterator().next());
                        }
                    } else if (!hodCourseIds.isEmpty() && !hodCourseIds.contains(classMgmt.getCourse_id())) {
                        return ResponseEntity.badRequest().body("HOD can only add classrooms for their assigned department courses.");
                    }
                }
            }
        }

        return ResponseEntity.ok(classService.saveClass(classMgmt));
    }

    // ================= GET ALL (Role-Based Isolation) =================

    @GetMapping({"", "/all"})
    public ResponseEntity<List<ClassMgmt>> getAllClasses(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Role 4 = Student -> Return strictly classrooms mapped to their enrolled course
                if (roleId == 4L) {
                    Set<Integer> courseIds = new HashSet<>();

                    List<Student> students = studentRepository.findByEmail(user.getEmailId());
                    if (students.isEmpty() && user.getUser_id() != null) {
                        students = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (students.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        students = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (students.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        students = studentRepository.findByStudentName(user.getFull_name().trim());
                    }

                    for (Student s : students) {
                        if (s.getCourse_id() != null) {
                            courseIds.add(s.getCourse_id());
                        }
                    }

                    if (!courseIds.isEmpty()) {
                        return ResponseEntity.ok(classRepository.findByCourseIdIn(new ArrayList<>(courseIds)));
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }

                // 2. Role 3 = Professor -> Return strictly classrooms mapped to their assigned course(s)
                if (roleId == 3L) {
                    Set<Integer> courseIds = new HashSet<>();

                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    for (com.college.cms.entity.Staff st : staffMembers) {
                        courseIds.addAll(st.getAllCourseIds());
                    }

                    if (!courseIds.isEmpty()) {
                        return ResponseEntity.ok(classRepository.findByCourseIdIn(new ArrayList<>(courseIds)));
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }

                // 3. Role 1 = HOD -> Return strictly classrooms mapped to their assigned department course(s)
                if (roleId == 1L) {
                    Set<Integer> courseIds = new HashSet<>();

                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    for (com.college.cms.entity.Staff st : staffMembers) {
                        courseIds.addAll(st.getAllCourseIds());
                    }

                    if (!courseIds.isEmpty()) {
                        return ResponseEntity.ok(classRepository.findByCourseIdIn(new ArrayList<>(courseIds)));
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }
            }
        }

        // Administrative role: Principal (2) sees all classrooms
        return ResponseEntity.ok(classService.getAllClasses());
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {

        Optional<ClassMgmt> classMgmt = classService.getClassById(id);

        if (classMgmt.isPresent()) {
            return ResponseEntity.ok(classMgmt.get());
        } else {
            return ResponseEntity.badRequest().body("Class Not Found");
        }
    }

    // ================= UPDATE =================

    @PutMapping({"/{id}", "/update/{id}"})
    public ResponseEntity<?> updateClass(@PathVariable Long id,
                                         @RequestBody ClassMgmt classMgmt) {

        try {

            ClassMgmt updated = classService.updateClass(id, classMgmt);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Class Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping({"/{id}", "/delete/{id}"})
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {

        try {

            classService.deleteClass(id);

            return ResponseEntity.ok("Class Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Class Not Found");

        }
    }

}