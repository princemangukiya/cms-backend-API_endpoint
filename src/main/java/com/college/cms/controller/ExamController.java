package com.college.cms.controller;

import com.college.cms.entity.Course;
import com.college.cms.entity.Exam;
import com.college.cms.entity.Staff;
import com.college.cms.entity.Student;
import com.college.cms.entity.Subject;
import com.college.cms.entity.User;
import com.college.cms.repository.CourseRepository;
import com.college.cms.repository.StaffRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.SubjectRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.ExamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/exams", "/exam", "/exams", "/api/exam"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class ExamController {

    @Autowired
    private ExamService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    // Helper method to verify Principal (Role 2) or HOD (Role 1)
    private boolean isPrincipalUser(Principal principal) {
        if (principal == null) return false;
        User user = userRepository.findByEmailId(principal.getName()).orElse(null);
        return user != null && user.getRoleId() != null && (user.getRoleId() == 2L || user.getRoleId() == 1L);
    }

    // ================= POST (Principal & HOD Can Schedule) =================

    @PostMapping({"", "/add", "/save"})
    public ResponseEntity<?> saveExam(@RequestBody Exam exam, Principal principal) {
        if (!isPrincipalUser(principal)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("Only administrators are authorized to schedule examinations.");
        }

        if (exam.getCourse_id() == null) {
            return ResponseEntity.badRequest().body("Course ID is required.");
        }
        if (exam.getSubject_id() == null) {
            exam.setSubject_id(1);
        }

        Exam saved = service.saveExam(exam);
        populateNames(Collections.singletonList(saved));
        return ResponseEntity.ok(saved);
    }

    // ================= LOOKUP / OPTIONS ENDPOINT (All Logged-in Roles) =================

    @GetMapping({"/lookup", "/options"})
    public ResponseEntity<List<Exam>> getExamLookup() {
        List<Exam> list = service.getAllExams();
        populateNames(list);
        return ResponseEntity.ok(list);
    }

    // ================= GET ALL (Role-Based Course & Section Isolation) =================

    @GetMapping({"", "/all"})
    public ResponseEntity<List<Exam>> getAllExams(Principal principal,
                                                  @RequestParam(name = "courseId", required = false) Integer courseId,
                                                  @RequestParam(name = "all", required = false, defaultValue = "false") boolean all) {
        List<Exam> list = service.getAllExams();

        if (all) {
            populateNames(list);
            return ResponseEntity.ok(list);
        }

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Role 4 = Student -> Return strictly exams mapped to their enrolled course
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
                        list = list.stream()
                                .filter(e -> e.getCourse_id() != null && courseIds.contains(e.getCourse_id()))
                                .collect(Collectors.toList());
                    } else {
                        list = Collections.emptyList();
                    }
                }

                // 2. Role 3 = Professor -> Return strictly exams mapped to their assigned department course
                else if (roleId == 3L) {
                    Set<Integer> courseIds = new HashSet<>();
                    List<Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }
                    for (Staff st : staffMembers) {
                        courseIds.addAll(st.getAllCourseIds());
                    }

                    if (!courseIds.isEmpty()) {
                        list = list.stream()
                                .filter(e -> e.getCourse_id() != null && courseIds.contains(e.getCourse_id()))
                                .collect(Collectors.toList());
                    } else {
                        list = Collections.emptyList();
                    }
                }

                // 3. Role 1 = HOD -> Return strictly exams mapped to their assigned department course(s)
                else if (roleId == 1L) {
                    Set<Integer> courseIds = new HashSet<>();
                    List<Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }
                    for (Staff st : staffMembers) {
                        courseIds.addAll(st.getAllCourseIds());
                    }

                    if (!courseIds.isEmpty()) {
                        list = list.stream()
                                .filter(e -> e.getCourse_id() != null && courseIds.contains(e.getCourse_id()))
                                .collect(Collectors.toList());
                    }
                }

                // 4. Role 2 = Principal -> Can view all, or filter by specific courseId if provided
                else if (roleId == 2L && courseId != null && courseId > 0) {
                    list = list.stream()
                            .filter(e -> e.getCourse_id() != null && e.getCourse_id().equals(courseId))
                            .collect(Collectors.toList());
                }
            }
        }

        // Populate course_name and subject_name so frontend always displays full names, never just IDs
        populateNames(list);

        return ResponseEntity.ok(list);
    }

    private void populateNames(List<Exam> list) {
        if (list == null || list.isEmpty()) return;

        Map<Integer, String> courseMap = new HashMap<>();
        try {
            for (Course c : courseRepository.findAll()) {
                if (c.getCourseId() != null && c.getCourseName() != null) {
                    courseMap.put(c.getCourseId(), c.getCourseName());
                }
            }
        } catch (Exception ignored) {}

        Map<Long, String> subjectMap = new HashMap<>();
        try {
            for (Subject s : subjectRepository.findAll()) {
                if (s.getSubject_id() != null && s.getSubjectName() != null) {
                    String label = s.getSubjectName();
                    if (s.getSubjectCode() != null && !s.getSubjectCode().trim().isEmpty()) {
                        label += " (" + s.getSubjectCode() + ")";
                    }
                    subjectMap.put(s.getSubject_id(), label);
                }
            }
        } catch (Exception ignored) {}

        for (Exam e : list) {
            if (e.getCourse_id() != null && courseMap.containsKey(e.getCourse_id())) {
                e.setCourse_name(courseMap.get(e.getCourse_id()));
            }
            if (e.getSubject_id() != null && subjectMap.containsKey(e.getSubject_id().longValue())) {
                e.setSubject_name(subjectMap.get(e.getSubject_id().longValue()));
            }
        }
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getExamById(@PathVariable Long id) {

        Optional<Exam> exam = service.getExamById(id);

        if (exam.isPresent()) {
            return ResponseEntity.ok(exam.get());
        } else {
            return ResponseEntity.badRequest().body("Exam Not Found");
        }
    }

    // ================= UPDATE (Principal Only) =================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExam(@PathVariable Long id,
                                        @RequestBody Exam exam,
                                        Principal principal) {
        if (!isPrincipalUser(principal)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("Only the Principal is authorized to update examinations.");
        }

        try {
            Exam updated = service.updateExam(id, exam);
            populateNames(Collections.singletonList(updated));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exam Not Found");
        }
    }

    // ================= DELETE (Principal Only) =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id, Principal principal) {
        if (!isPrincipalUser(principal)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN)
                    .body("Only the Principal is authorized to delete examinations.");
        }

        try {
            service.deleteExam(id);
            return ResponseEntity.ok("Exam Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Exam Not Found");
        }
    }
}