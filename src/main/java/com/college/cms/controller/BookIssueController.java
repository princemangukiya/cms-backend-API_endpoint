package com.college.cms.controller;

import com.college.cms.entity.BookIssue;
import com.college.cms.service.BookIssueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/book-issues")
@CrossOrigin(origins = "http://localhost:5173")
public class BookIssueController {

    @Autowired
    private BookIssueService service;

    // ================== POST ==================

    @PostMapping
    public ResponseEntity<?> saveBookIssue(@RequestBody BookIssue bookIssue) {

        if (bookIssue.getBookId() == null || bookIssue.getUserId() == null) {
            return ResponseEntity.badRequest().body("Book ID and User ID are required.");
        }

        return ResponseEntity.ok(service.saveBookIssue(bookIssue));
    }

    // ================== GET ALL ==================

    @GetMapping
    public ResponseEntity<List<BookIssue>> getAllBookIssues() {

        return ResponseEntity.ok(service.getAllBookIssues());
    }

    // ================== GET BY ID ==================

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookIssueById(@PathVariable Long id) {

        Optional<BookIssue> bookIssue = service.getBookIssueById(id);

        if (bookIssue.isPresent()) {
            return ResponseEntity.ok(bookIssue.get());
        } else {
            return ResponseEntity.badRequest().body("Book Issue Not Found");
        }
    }

    // ================== UPDATE ==================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookIssue(@PathVariable Long id,
                                             @RequestBody BookIssue bookIssue) {

        try {

            BookIssue updated = service.updateBookIssue(id, bookIssue);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Book Issue Not Found");

        }
    }

    // ================== DELETE ==================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookIssue(@PathVariable Long id) {

        try {

            service.deleteBookIssue(id);

            return ResponseEntity.ok("Book Issue Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Book Issue Not Found");

        }
    }

}