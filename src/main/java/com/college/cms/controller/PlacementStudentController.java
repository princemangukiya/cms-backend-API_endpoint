package com.college.cms.controller;

import com.college.cms.entity.PlacementStudent;
import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.PlacementStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/placement", "/placement"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "http://localhost:3000", "http://127.0.0.1:5173", "http://127.0.0.1:5174"})
public class PlacementStudentController {

    @Autowired
    private PlacementStudentService placementStudentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    // POST
    @PostMapping({"", "/add", "/save"})
    public ResponseEntity<?> savePlacement(@RequestBody PlacementStudent placement) {

        if (placement.getCompany_id() == null || placement.getStudent_id() == null) {
            return ResponseEntity.badRequest()
                    .body("Company ID and Student ID are required.");
        }

        return ResponseEntity.ok(
                placementStudentService.savePlacementDetail(placement));
    }

    // GET ALL (with Role-Based Isolation)
    @GetMapping({"", "/all"})
    public ResponseEntity<List<PlacementStudent>> getAllPlacements(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 3. Student (Role 4) -> Sees ONLY their own placement records
                if (roleId == 4L) {
                    List<Student> students = studentRepository.findByEmail(user.getEmailId());
                    if (students.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        students = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (students.isEmpty() && user.getUser_id() != null) {
                        students = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (students.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        students = studentRepository.findByStudentName(user.getFull_name().trim());
                    }

                    if (!students.isEmpty()) {
                        List<Integer> studentIds = students.stream()
                                .map(s -> s.getStudent_id().intValue())
                                .toList();
                        return ResponseEntity.ok(placementStudentService.getPlacementsByStudentIds(studentIds));
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }
            }
        }

        // Placement Officer (6), HOD (1), Principal (2), Professor (3) see all records
        return ResponseEntity.ok(
                placementStudentService.getAllPlacements());
    }

    // GET BY STUDENT ID (Strict Student Privacy)
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PlacementStudent>> getPlacementsByStudentId(@PathVariable Integer studentId, Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
            if (user != null && user.getRoleId() != null && user.getRoleId() == 4L) {
                List<Student> students = studentRepository.findByEmail(user.getEmailId());
                if (students.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                    students = studentRepository.findByMobileNo(user.getMobile_no().trim());
                }
                if (students.isEmpty() && user.getUser_id() != null) {
                    students = studentRepository.findByUserId(user.getUser_id().intValue());
                }
                if (students.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                    students = studentRepository.findByStudentName(user.getFull_name().trim());
                }
                boolean matches = students.stream().anyMatch(s -> s.getStudent_id().intValue() == studentId);
                if (!matches) {
                    return ResponseEntity.ok(Collections.emptyList());
                }
            }
        }
        return ResponseEntity.ok(placementStudentService.getPlacementsByStudentId(studentId));
    }

    // GET BY ID (with Student Privacy Check)
    @GetMapping("/{placementId}")
    public ResponseEntity<?> getPlacementById(@PathVariable Long placementId, Principal principal) {

        Optional<PlacementStudent> placement =
                placementStudentService.getPlacementById(placementId);

        if (placement.isPresent()) {
            PlacementStudent p = placement.get();
            if (principal != null) {
                String loggedInEmail = principal.getName();
                User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
                if (user != null && user.getRoleId() != null && user.getRoleId() == 4L) {
                    List<Student> students = studentRepository.findByEmail(user.getEmailId());
                    if (students.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        students = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (students.isEmpty() && user.getUser_id() != null) {
                        students = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (students.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        students = studentRepository.findByStudentName(user.getFull_name().trim());
                    }
                    boolean matches = students.stream().anyMatch(s -> s.getStudent_id().intValue() == p.getStudent_id());
                    if (!matches) {
                        return ResponseEntity.status(org.springframework.http.HttpStatus.FORBIDDEN).body("Access Denied: Confidential Record");
                    }
                }
            }
            return ResponseEntity.ok(p);
        }

        return ResponseEntity.badRequest().body("Placement Not Found");
    }

    // UPDATE
    @PutMapping({"/{placementId}", "/update/{placementId}"})
    public ResponseEntity<?> updatePlacement(
            @PathVariable Long placementId,
            @RequestBody PlacementStudent placement) {

        try {

            PlacementStudent updated =
                    placementStudentService.updatePlacement(placementId, placement);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Placement Not Found");
        }
    }

    // DELETE
    @DeleteMapping({"/{placementId}", "/delete/{placementId}"})
    public ResponseEntity<?> deletePlacement(
            @PathVariable Long placementId) {

        try {

            placementStudentService.deletePlacement(placementId);

            return ResponseEntity.ok("Placement Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Placement Not Found");
        }
    }
}