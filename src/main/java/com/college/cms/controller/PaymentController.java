package com.college.cms.controller;

import com.college.cms.entity.Payment;
import com.college.cms.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:5173")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> savePayment(@RequestBody Payment payment) {

        if (payment.getFeeId() == null || payment.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Fee ID and Student ID are required.");
        }

        return ResponseEntity.ok(paymentService.savePayment(payment));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
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