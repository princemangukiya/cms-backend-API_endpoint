package com.college.cms.controller;

import com.college.cms.entity.Course;
import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.CourseRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping({"/api/courses", "/courses", "/course", "/api/course"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private com.college.cms.repository.StaffRepository staffRepository;

    private String normalizeCourseName(String name) {
        if (name == null) return "";
        return name.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    private String normalizeSemester(String sem) {
        if (sem == null) return "";
        String s = sem.trim().toLowerCase();
        Matcher matcher = Pattern.compile("\\d+").matcher(s);
        if (matcher.find()) {
            return matcher.group();
        }
        return s.replaceAll("\\s+", "");
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course, Principal principal) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Course name cannot be empty."));
        }
        if (course.getSemester() == null || course.getSemester().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Semester cannot be empty."));
        }

        String targetName = normalizeCourseName(course.getCourseName());
        String targetSem = normalizeSemester(course.getSemester());

        List<Course> allCourses = courseRepository.findAll();
        for (Course existing : allCourses) {
            if (normalizeCourseName(existing.getCourseName()).equals(targetName) &&
                normalizeSemester(existing.getSemester()).equals(targetSem)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "DUPLICATE_COURSE_SEMESTER",
                    "message", "Course '" + course.getCourseName().trim() + "' with " + course.getSemester() + " already exists! You can create multiple semesters for '" + course.getCourseName().trim() + "', but the same semester cannot be duplicated."
                ));
            }
        }

        Course saved = courseService.saveCourse(course);

        // If created by HOD (Role 1), automatically link new course to HOD's staff record
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
            if (user != null && user.getRoleId() != null && user.getRoleId() == 1L) {
                List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                if (staffMembers.isEmpty() && user.getUser_id() != null) {
                    staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                }
                for (com.college.cms.entity.Staff st : staffMembers) {
                    String existingIds = st.getCourseIds();
                    if (existingIds == null || existingIds.trim().isEmpty()) {
                        st.setCourseIds(String.valueOf(saved.getCourseId()));
                    } else {
                        Set<String> idSet = new LinkedHashSet<>(Arrays.asList(existingIds.split(",")));
                        idSet.add(String.valueOf(saved.getCourseId()));
                        st.setCourseIds(String.join(",", idSet));
                    }
                    if (st.getCourseId() == null) {
                        st.setCourseId(saved.getCourseId());
                    }
                    staffRepository.save(st);
                }
            }
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping({"/all", ""})
    public List<Course> getAllCourses(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Role 4 = Student -> Return strictly their enrolled course(s)
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
                        return courseRepository.findByCourseIdIn(new ArrayList<>(courseIds));
                    } else {
                        return Collections.emptyList();
                    }
                }

                // 2. Role 3 = Professor -> Return strictly their assigned department course(s)
                if (roleId == 3L) {
                    Set<Integer> assignedCourseIds = new HashSet<>();

                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    for (com.college.cms.entity.Staff st : staffMembers) {
                        assignedCourseIds.addAll(st.getAllCourseIds());
                    }

                    if (!assignedCourseIds.isEmpty()) {
                        return courseRepository.findByCourseIdIn(new ArrayList<>(assignedCourseIds));
                    } else {
                        return Collections.emptyList();
                    }
                }

                // 3. Role 1 = HOD -> Return strictly their assigned department course(s)
                if (roleId == 1L) {
                    Set<Integer> assignedCourseIds = new HashSet<>();

                    List<com.college.cms.entity.Staff> staffMembers = staffRepository.findByEmail(user.getEmailId());
                    if (staffMembers.isEmpty() && user.getUser_id() != null) {
                        staffMembers = staffRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (staffMembers.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        staffMembers = staffRepository.findByMobileno(user.getMobile_no().trim());
                    }

                    for (com.college.cms.entity.Staff st : staffMembers) {
                        assignedCourseIds.addAll(st.getAllCourseIds());
                    }

                    if (!assignedCourseIds.isEmpty()) {
                        return courseRepository.findByCourseIdIn(new ArrayList<>(assignedCourseIds));
                    }
                }
            }
        }
        // Principal (Role 2) and general -> Returns all college courses
        return courseService.getAllCourses();
    }

    @GetMapping("/lookup")
    public List<Course> getAllCoursesLookup() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        return course != null ? ResponseEntity.ok(course) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Integer id, @RequestBody Course course) {
        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Course name cannot be empty."));
        }
        if (course.getSemester() == null || course.getSemester().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Semester cannot be empty."));
        }

        String targetName = normalizeCourseName(course.getCourseName());
        String targetSem = normalizeSemester(course.getSemester());

        List<Course> allCourses = courseRepository.findAll();
        for (Course existing : allCourses) {
            if (!existing.getCourseId().equals(id) &&
                normalizeCourseName(existing.getCourseName()).equals(targetName) &&
                normalizeSemester(existing.getSemester()).equals(targetSem)) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "DUPLICATE_COURSE_SEMESTER",
                    "message", "Cannot update: Course '" + course.getCourseName().trim() + "' with " + course.getSemester() + " already exists in record #" + existing.getCourseId() + "! Please choose a different semester."
                ));
            }
        }

        Course updated = courseService.updateCourse(id, course);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }
}