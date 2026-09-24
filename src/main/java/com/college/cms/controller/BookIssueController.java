package com.college.cms.controller;

import com.college.cms.entity.BookIssue;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.BookIssueService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/book-issues")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175", "*"})
public class BookIssueController {

    @Autowired
    private BookIssueService service;

    @Autowired
    private UserRepository userRepository;

    private boolean isLibrarian(Principal principal) {
        if (principal == null) return false;
        User user = userRepository.findByEmailId(principal.getName()).orElse(null);
        return user != null && user.getRoleId() != null && user.getRoleId() == 5L;
    }

    // ================== POST (Only Librarian Can Issue Books) ==================

    @PostMapping
    public ResponseEntity<?> saveBookIssue(@RequestBody BookIssue bookIssue, Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to issue books.");
        }

        if (bookIssue.getBookId() == null || bookIssue.getUserId() == null) {
            return ResponseEntity.badRequest().body("Book ID and User ID are required.");
        }

        if (bookIssue.getIssueDate() == null) {
            bookIssue.setIssueDate(java.time.LocalDate.now());
        }

        try {
            return ResponseEntity.ok(service.saveBookIssue(bookIssue));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ================== GET ALL (Role-Based Isolation) ==================

    @GetMapping
    public ResponseEntity<List<BookIssue>> getAllBookIssues(
            Principal principal,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "all", required = false) Boolean all) {

        if (principal != null) {
            String loggedInEmail = principal.getName();
            User user = userRepository.findByEmailId(loggedInEmail).orElse(null);
            if (user != null && user.getRoleId() != null) {
                Long roleId = user.getRoleId();

                // 1. Only Librarian (Role 5) can view all college records if explicitly requested
                if (roleId == 5L && Boolean.TRUE.equals(all)) {
                    if (userId != null) {
                        return ResponseEntity.ok(service.getBookIssuesByUserId(userId));
                    }
                    return ResponseEntity.ok(service.getAllBookIssues());
                }

                // 2. Everyone (Principal 2, Student 4, Professor 3, HOD 1) and Librarian by default
                // strictly sees ONLY their own separate issued books ("sirf uski id ka hi rakho")
                if (user.getUser_id() != null) {
                    return ResponseEntity.ok(service.getBookIssuesByUserId(user.getUser_id().longValue()));
                }
            }
        }

        if (userId != null) {
            return ResponseEntity.ok(service.getBookIssuesByUserId(userId));
        }

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

    // ================== GET BY USER ID ==================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookIssue>> getBookIssuesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getBookIssuesByUserId(userId));
    }

    // ================== UPDATE (Only Librarian Can Update) ==================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBookIssue(@PathVariable Long id,
                                             @RequestBody BookIssue bookIssue,
                                             Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to update book issues.");
        }

        try {
            BookIssue updated = service.updateBookIssue(id, bookIssue);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book Issue Not Found");
        }
    }

    // ================== DELETE (Only Librarian Can Delete) ==================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookIssue(@PathVariable Long id, Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to delete book issues.");
        }

        try {
            service.deleteBookIssue(id);
            return ResponseEntity.ok("Book Issue Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book Issue Not Found");
        }
    }
}