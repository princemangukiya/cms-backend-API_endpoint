package com.college.cms.controller;

import com.college.cms.entity.Fees;
import com.college.cms.entity.Payment;
import com.college.cms.entity.Student;
import com.college.cms.entity.User;
import com.college.cms.repository.FeesRepository;
import com.college.cms.repository.PaymentRepository;
import com.college.cms.repository.StudentRepository;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/api/payments", "/payments", "/payment", "/api/payment"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FeesRepository feesRepository;

    // ================= POST =================

    @PostMapping({"", "/add"})
    public ResponseEntity<?> savePayment(@RequestBody Payment payment, Principal principal) {

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // If caller is a Student (Role 4): Strictly enforce student can ONLY pay for own studentId
                if (roleId == 4L) {
                    List<Student> selfStudents = studentRepository.findByEmail(user.getEmailId());
                    if (selfStudents.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        selfStudents = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (selfStudents.isEmpty() && user.getUser_id() != null) {
                        selfStudents = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (selfStudents.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        selfStudents = studentRepository.findByStudentName(user.getFull_name().trim());
                    }

                    Integer ownStudentId = null;
                    if (!selfStudents.isEmpty() && selfStudents.get(0).getStudent_id() != null) {
                        ownStudentId = selfStudents.get(0).getStudent_id().intValue();
                    } else if (user.getUser_id() != null) {
                        ownStudentId = user.getUser_id().intValue();
                    }

                    if (ownStudentId != null) {
                        payment.setStudentId(ownStudentId);
                    }
                }
            }
        }

        if (payment.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Student ID is required.");
        }

        // Auto-resolve Fee ID if not passed: check existing fee in fees_detail or auto-create one for student's enrolled course
        if (payment.getFeeId() == null) {
            List<Fees> existingFees = feesRepository.findByStudentId(payment.getStudentId().longValue());
            Optional<Student> studentOpt = studentRepository.findById(payment.getStudentId().longValue());
            Long enrolledCourseId = 1L;
            if (studentOpt.isPresent() && studentOpt.get().getCourse_id() != null) {
                enrolledCourseId = studentOpt.get().getCourse_id().longValue();
            }

            final Long finalCourseId = enrolledCourseId;
            Optional<Fees> matchingFee = existingFees.stream()
                .filter(f -> f.getCourseId() != null && f.getCourseId().equals(finalCourseId))
                .findFirst();

            if (matchingFee.isPresent() && matchingFee.get().getFeeId() != null) {
                payment.setFeeId(matchingFee.get().getFeeId().intValue());
            } else {
                Fees autoFee = new Fees();
                autoFee.setStudentId(payment.getStudentId().longValue());
                autoFee.setCourseId(enrolledCourseId); // Use student's enrolled course!
                autoFee.setScholarship(0.0);
                autoFee.setDiscountPercentage(0.0);
                autoFee.setTotalFees(payment.getPaidAmount() != null && payment.getPaidAmount() > 0 ? payment.getPaidAmount() : 1212.0);
                Fees savedFee = feesRepository.save(autoFee);
                payment.setFeeId(savedFee.getFeeId().intValue());
            }
        }

        if (payment.getPaidAmount() == null || payment.getPaidAmount() <= 0) {
            return ResponseEntity.badRequest().body("Valid Paid Amount is required.");
        }

        // Payment date must always be the current date
        payment.setDate(LocalDate.now());

        // Transaction ID auto-generated if missing
        if (payment.getTransactionId() == null || payment.getTransactionId().trim().isEmpty()) {
            payment.setTransactionId("TXN-" + System.currentTimeMillis() + (int)(Math.random() * 900 + 100));
        }

        if (payment.getStatus() == null || payment.getStatus().trim().isEmpty()) {
            payment.setStatus("Paid");
        }

        return ResponseEntity.ok(paymentService.savePayment(payment));
    }

    // ================= GET ALL (Role-Based Isolation) =================

    @GetMapping({"", "/all"})
    public ResponseEntity<?> getAllPayments(Principal principal) {
        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);

            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Principal (Role 2) -> Can view college payments
                if (roleId == 2L) {
                    return ResponseEntity.ok(paymentService.getAllPayments());
                }

                // HOD (Role 1) & Professor (Role 3) -> Access Denied
                if (roleId == 1L || roleId == 3L) {
                    return ResponseEntity.status(403).body("Access denied on payments");
                }

                // 2. Student (Role 4) -> Sees ONLY their own payments
                if (roleId == 4L) {
                    List<Student> selfStudents = studentRepository.findByEmail(user.getEmailId());
                    if (selfStudents.isEmpty() && user.getMobile_no() != null && !user.getMobile_no().trim().isEmpty()) {
                        selfStudents = studentRepository.findByMobileNo(user.getMobile_no().trim());
                    }
                    if (selfStudents.isEmpty() && user.getUser_id() != null) {
                        selfStudents = studentRepository.findByUserId(user.getUser_id().intValue());
                    }
                    if (selfStudents.isEmpty() && user.getFull_name() != null && !user.getFull_name().trim().isEmpty()) {
                        selfStudents = studentRepository.findByStudentName(user.getFull_name().trim());
                    }

                    List<Integer> studentIds = selfStudents.stream()
                            .filter(s -> s.getStudent_id() != null)
                            .map(s -> s.getStudent_id().intValue())
                            .collect(Collectors.toList());

                    if (user.getUser_id() != null && !studentIds.contains(user.getUser_id().intValue())) {
                        studentIds.add(user.getUser_id().intValue());
                    }

                    if (!studentIds.isEmpty()) {
                        List<Payment> studentPayments = paymentRepository.findByStudentIdIn(studentIds);
                        return ResponseEntity.ok(studentPayments);
                    } else {
                        return ResponseEntity.ok(Collections.emptyList());
                    }
                }
            }
        }

        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // ================= GET BY ID =================

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPaymentById(@PathVariable Integer paymentId) {

        Optional<Payment> payment = paymentService.getPaymentById(paymentId);

        if (payment.isPresent()) {
            return ResponseEntity.ok(payment.get());
        }

        return ResponseEntity.badRequest().body("Payment Not Found");
    }

    // ================= UPDATE =================

    @PutMapping("/{paymentId}")
    public ResponseEntity<?> updatePayment(@PathVariable Integer paymentId,
                                           @RequestBody Payment payment) {

        try {

            Payment updated = paymentService.updatePayment(paymentId, payment);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Payment Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<?> deletePayment(@PathVariable Integer paymentId) {

        try {

            paymentService.deletePayment(paymentId);

            return ResponseEntity.ok("Payment Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Payment Not Found");

        }
    }
}