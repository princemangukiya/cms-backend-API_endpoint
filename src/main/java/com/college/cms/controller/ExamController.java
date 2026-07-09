package com.college.cms.controller;

import com.college.cms.entity.Exam;
import com.college.cms.service.ExamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
@CrossOrigin(origins = "http://localhost:5173")
public class ExamController {

    @Autowired
    private ExamService service;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveExam(@RequestBody Exam exam) {

        if (exam.getCourse_id() == null || exam.getSubject_id() == null) {
            return ResponseEntity.badRequest().body("Course ID and Subject ID are required.");
        }

        return ResponseEntity.ok(service.saveExam(exam));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Exam>> getAllExams() {

        return ResponseEntity.ok(service.getAllExams());
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

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExam(@PathVariable Long id,
                                        @RequestBody Exam exam) {

        try {

            Exam updated = service.updateExam(id, exam);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Exam Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExam(@PathVariable Long id) {

        try {

            service.deleteExam(id);

            return ResponseEntity.ok("Exam Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Exam Not Found");

        }
    }

}