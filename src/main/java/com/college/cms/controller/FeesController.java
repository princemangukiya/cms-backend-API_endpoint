package com.college.cms.controller;

import com.college.cms.entity.Fees;
import com.college.cms.service.FeesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "http://localhost:5173")
public class FeesController {

    @Autowired
    private FeesService feesService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveFees(@RequestBody Fees fees) {

        if (fees.getCourseId() == null || fees.getStudentId() == null) {
            return ResponseEntity.badRequest().body("Course ID and Student ID are required.");
        }

        return ResponseEntity.ok(feesService.saveFees(fees));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Fees>> getAllFees() {

        return ResponseEntity.ok(feesService.getAllFees());
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeesById(@PathVariable Long id) {

        Optional<Fees> fees = feesService.getFeesById(id);

        if (fees.isPresent()) {
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