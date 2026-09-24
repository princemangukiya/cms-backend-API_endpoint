package com.college.cms.controller;

import com.college.cms.entity.Course;
import com.college.cms.entity.Exam;
import com.college.cms.entity.Staff;
import com.college.cms.entity.Student;
import com.college.cms.entity.Subject;
import com.college.cms.entity.User;
import com.college.cms.repository.CourseRepository;
import com.college.cms.repository.ExamRepository;
import com.college.cms.repository.StaffRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.SubjectRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping({"/api/subjects", "/subjects"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ExamRepository examRepository;

    private void populateDetails(List<Subject> subjects) {
        if (subjects == null || subjects.isEmpty()) return;

        Map<Integer, String> courseMap = new HashMap<>();
        try {
            for (Course c : courseRepository.findAll()) {
                if (c.getCourseId() != null && c.getCourseName() != null) {
                    courseMap.put(c.getCourseId(), c.getCourseName());
                }
            }
        } catch (Exception ignored) {}

        Map<Integer, String> examMap = new HashMap<>();
        try {
            for (Exam e : examRepository.findAll()) {
                if (e.getExam_id() != null) {
                    String name = e.getExam_type();
                    if (name == null || name.trim().isEmpty()) {
                        name = "Exam #" + e.getExam_id();
                    }
                    examMap.put(e.getExam_id().intValue(), name);
                }
            }
        } catch (Exception ignored) {}

        Map<Integer, String> staffMap = new HashMap<>();
        try {
            for (Staff st : staffRepository.findAll()) {
                if (st.getStaffid() != null && st.getStaffname() != null) {
                    staffMap.put(st.getStaffid(), st.getStaffname());
                }
            }
        } catch (Exception ignored) {}

        for (Subject s : subjects) {
            if (s.getCourseId() != null && courseMap.containsKey(s.getCourseId())) {
                s.setCourseName(courseMap.get(s.getCourseId()));
            }
            if (s.getExamId() != null && examMap.containsKey(s.getExamId())) {
                String eName = examMap.get(s.getExamId());
                s.setExamName(eName);
                s.setExamType(eName);
            }
            if (s.getStaffId() != null && staffMap.containsKey(s.getStaffId())) {
                s.setStaffName(staffMap.get(s.getStaffId()));
            }
        }
    }

    // POST: http://localhost:8080/api/subjects/save
    @PostMapping({"/save", ""})
    public Subject addSubject(@RequestBody Subject subject) {
        Subject saved = subjectService.saveSubject(subject);
        populateDetails(Collections.singletonList(saved));
        return saved;
    }

    // GET: http://localhost:8080/api/subjects/all (Role-Based Isolation)
    @GetMapping({"/all", ""})
    public List<Subject> getAll(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Role 4 = Student -> Return strictly subjects mapped to their enrolled course
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
                        List<Subject> res = subjectRepository.findByCourseIdIn(new ArrayList<>(courseIds));
                        populateDetails(res);
                        return res;
                    } else {
                        return Collections.emptyList();
                    }
                }

                // 2. Role 3 = Professor -> Return strictly subjects belonging to this Professor's assigned courses!
                else if (roleId == 3L) {
                    List<Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    Set<Integer> courseIds = new HashSet<>();
                    for (Staff st : staffMembers) {
                        courseIds.addAll(st.getAllCourseIds());
                    }

                    if (!courseIds.isEmpty()) {
                        List<Subject> res = subjectRepository.findByCourseIdIn(new ArrayList<>(courseIds));
                        populateDetails(res);
                        return res;
                    } else {
                        return Collections.emptyList();
                    }
                }

                // 3. Role 1 = HOD -> Return strictly subjects mapped to their assigned course(s)
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
                        List<Subject> res = subjectRepository.findByCourseIdIn(new ArrayList<>(courseIds));
                        populateDetails(res);
                        return res;
                    }
                }
            }
        }
        // Principal (Role 2) and general -> Return all college subjects
        List<Subject> all = subjectService.getAllSubjects();
        populateDetails(all);
        return all;
    }

    // GET: http://localhost:8080/api/subjects/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(@PathVariable Long id) {
        Subject s = subjectService.getSubjectById(id);
        if (s != null) {
            populateDetails(Collections.singletonList(s));
            return ResponseEntity.ok(s);
        }
        return ResponseEntity.notFound().build();
    }

    // PUT: http://localhost:8080/api/subjects/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable Long id, @RequestBody Subject subject) {
        Subject updated = subjectService.updateSubject(id, subject);
        if (updated != null) {
            populateDetails(Collections.singletonList(updated));
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // DELETE: http://localhost:8080/api/subjects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Subject deleted successfully");
    }
}