package com.college.cms.controller;

import com.college.cms.entity.Fees;
import com.college.cms.entity.Payment;
import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.FeesRepository;
import com.college.cms.repository.PaymentRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.FeesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/fees", "/fees"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class FeesController {

    @Autowired
    private FeesService feesService;

    @Autowired
    private FeesRepository feesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private void populateFinancials(List<Fees> feesList) {
        if (feesList == null || feesList.isEmpty()) return;

        List<Payment> allPayments;
        try {
            allPayments = paymentRepository.findAll();
        } catch (Exception e) {
            allPayments = Collections.emptyList();
        }

        for (Fees f : feesList) {
            Long feeId = f.getFeeId();
            Long studentId = f.getStudentId();
            Double totalFees = f.getTotalFees() != null ? f.getTotalFees() : 0.0;

            double paid = 0.0;
            for (Payment p : allPayments) {
                String status = p.getStatus();
                boolean isSuccessful = status == null || status.equalsIgnoreCase("Paid") || status.equalsIgnoreCase("Success");
                if (!isSuccessful) continue;

                boolean matchFee = p.getFeeId() != null && feeId != null && p.getFeeId().longValue() == feeId.longValue();
                boolean matchStudent = p.getStudentId() != null && studentId != null && p.getStudentId().longValue() == studentId.longValue() && (p.getFeeId() == null || p.getFeeId() == 0);

                if (matchFee || matchStudent) {
                    paid += (p.getPaidAmount() != null ? p.getPaidAmount() : 0.0);
                }
            }

            f.setPaidAmount(paid);
            f.setPendingDue(Math.max(0.0, totalFees - paid));
            if (totalFees > 0 && paid >= totalFees) {
                f.setPaymentStatus("Paid");
            } else if (paid > 0) {
                f.setPaymentStatus("Partial");
            } else {
                f.setPaymentStatus("Pending");
            }
        }
    }

    // ================= POST =================

    @PostMapping({"", "/add"})
    public ResponseEntity<?> saveFees(@RequestBody Fees fees) {

        if (fees.getCourseId() == null || fees.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Course ID and Student ID are required.");
        }

        // Validate that student exists and selected course strictly matches the student's enrolled course
        Optional<Student> studentOpt = studentRepository.findById(fees.getStudentId());
        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            if (student.getCourse_id() != null && !student.getCourse_id().equals(fees.getCourseId().intValue())) {
                return ResponseEntity.badRequest().body("Course Mismatch Warning: Student '" + student.getStudent_name() + 
                    "' is enrolled in Course ID " + student.getCourse_id() + 
                    ". You cannot assign fees for Course ID " + fees.getCourseId() + ".");
            }
        }

        Fees saved = feesService.saveFees(fees);
        populateFinancials(Collections.singletonList(saved));
        return ResponseEntity.ok(saved);
    }

    // ================= GET ALL (Role-Based Isolation) =================

    @GetMapping({"", "/all"})
    public ResponseEntity<?> getAllFees(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Principal (Role 2) -> Sees ALL fee records across the college
                if (roleId == 2L) {
                    List<Fees> all = feesService.getAllFees();
                    populateFinancials(all);
                    return ResponseEntity.ok(all);
                }

                // 2. Student (Role 4) -> Sees ONLY their own fee records for their enrolled course
                if (roleId == 4L) {
                    java.util.Set<Long> studentIds = new java.util.HashSet<>();

                    if (user.getUser_id() != null) {
                        studentIds.add(user.getUser_id());
                    }

                    if (user.getEmailId() != null && !user.getEmailId().trim().isEmpty()) {
                        List<Student> byEmail = studentRepository.findByEmail(user.getEmailId().trim());
                        for (Student s : byEmail) {
                            if (s.getStudent_id() != null) studentIds.add(s.getStudent_id());
                            if (s.getUser_id() != null) studentIds.add(s.getUser_id().longValue());
                        }
                    }

                    if (user.getUser_id() != null) {
                        List<Student> byUserId = studentRepository.findByUserId(user.getUser_id().intValue());
                        for (Student s : byUserId) {
                            if (s.getStudent_id() != null) studentIds.add(s.getStudent_id());
                        }
                    }

                    if (user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        List<Student> byMobile = studentRepository.findByMobileNo(user.getMobile_no().trim());
                        for (Student s : byMobile) {
                            if (s.getStudent_id() != null) studentIds.add(s.getStudent_id());
                        }
                    }

                    if (user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        List<Student> byName = studentRepository.findByStudentName(user.getFull_name().trim());
                        for (Student s : byName) {
                            if (s.getStudent_id() != null) studentIds.add(s.getStudent_id());
                        }
                    }

                    if (!studentIds.isEmpty()) {
                        List<Fees> studentFees = feesRepository.findByStudentIdIn(new java.util.ArrayList<>(studentIds));

                        // Find enrolled course of the student
                        Integer enrolledCourseId = null;
                        for (Long sId : studentIds) {
                            Optional<Student> sOpt = studentRepository.findById(sId);
                            if (sOpt.isPresent() && sOpt.get().getCourse_id() != null) {
                                enrolledCourseId = sOpt.get().getCourse_id();
                                break;
                            }
                        }

                        if (enrolledCourseId != null) {
                            final Integer finalCId = enrolledCourseId;
                            studentFees = studentFees.stream()
                                    .filter(f -> f.getCourseId() != null && f.getCourseId().intValue() == finalCId)
                                    .collect(Collectors.toList());
                        }

                        populateFinancials(studentFees);
                        return ResponseEntity.ok(studentFees);
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }

                // 3. HOD (Role 1) -> Can view fee directory
                if (roleId == 1L) {
                    List<Fees> all = feesService.getAllFees();
                    populateFinancials(all);
                    return ResponseEntity.ok(all);
                }

                // Professor (Role 3) -> Access Denied
                if (roleId == 3L) {
                    return ResponseEntity.status(403).body("Access denied for Professor on fees");
                }
            }
        }

        List<Fees> all = feesService.getAllFees();
        populateFinancials(all);
        return ResponseEntity.ok(all);
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeesById(@PathVariable Long id) {

        Optional<Fees> fees = feesService.getFeesById(id);

        if (fees.isPresent()) {
            populateFinancials(Collections.singletonList(fees.get()));
            return ResponseEntity.ok(fees.get());
        } else {
            return ResponseEntity.badRequest().body("Fees Not Found");
        }
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFees(@PathVariable Long id,
                                        @RequestBody Fees fees) {

        try {

            Fees updated = feesService.updateFees(id, fees);
            populateFinancials(Collections.singletonList(updated));
            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Fees Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFees(@PathVariable Long id) {

        try {

            feesService.deleteFees(id);

            return ResponseEntity.ok("Fees Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Fees Not Found");

        }
    }
}