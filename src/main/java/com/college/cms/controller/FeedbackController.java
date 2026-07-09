package com.college.cms.controller;

import com.college.cms.entity.Feedback;
import com.college.cms.service.FeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "http://localhost:5173")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveFeedback(@RequestBody Feedback feedback) {

        if (feedback.getFeedbackFrom() == null || feedback.getFeedbackTo() == null) {
            return ResponseEntity.badRequest().body("Feedback From and Feedback To are required.");
        }

        return ResponseEntity.ok(feedbackService.saveFeedback(feedback));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {

        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Integer id) {

        Optional<Feedback> feedback = feedbackService.getFeedbackById(id);

        if (feedback.isPresent()) {
            return ResponseEntity.ok(feedback.get());
        } else {
            return ResponseEntity.badRequest().body("Feedback Not Found");
        }
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Integer id,
                                            @RequestBody Feedback feedback) {

        try {

            Feedback updated = feedbackService.updateFeedback(id, feedback);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Feedback Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Integer id) {

        try {

            feedbackService.deleteFeedback(id);

            return ResponseEntity.ok("Feedback Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Feedback Not Found");

        }
    }

}