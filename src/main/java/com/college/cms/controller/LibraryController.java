package com.college.cms.controller;

import com.college.cms.entity.Library;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.LibraryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping({"/api/library", "/library"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @Autowired
    private UserRepository userRepository;

    private boolean isLibrarian(Principal principal) {
        if (principal == null) return false;
        User user = userRepository.findByEmailId(principal.getName()).orElse(null);
        return user != null && user.getRoleId() != null && user.getRoleId() == 5L;
    }

    // ================= POST (Only Librarian Can Add Books) =================

    @PostMapping({"", "/add"})
    public ResponseEntity<?> saveLibrary(@RequestBody Library library, Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to add new books.");
        }

        if (library.getBookname() == null || library.getBookname().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Book Name is required.");
        }

        return ResponseEntity.ok(libraryService.saveLibrary(library));
    }

    // ================= GET ALL (All Roles Can View Library Catalog) =================

    @GetMapping({"", "/all"})
    public ResponseEntity<List<Library>> getAllLibrary() {
        return ResponseEntity.ok(libraryService.getAllLibrary());
    }

    // ================= GET BY ID =================

    @GetMapping("/{bookid}")
    public ResponseEntity<?> getLibraryById(@PathVariable Long bookid) {
        Optional<Library> library = libraryService.getLibraryById(bookid);
        if (library.isPresent()) {
            return ResponseEntity.ok(library.get());
        } else {
            return ResponseEntity.badRequest().body("Book Not Found");
        }
    }

    // ================= UPDATE (Only Librarian Can Update) =================

    @PutMapping("/{bookid}")
    public ResponseEntity<?> updateLibrary(@PathVariable Long bookid, @RequestBody Library library, Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to update books.");
        }

        try {
            Library updated = libraryService.updateLibrary(bookid, library);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book Not Found");
        }
    }

    // ================= DELETE (Only Librarian Can Delete) =================

    @DeleteMapping("/{bookid}")
    public ResponseEntity<?> deleteLibrary(@PathVariable Long bookid, Principal principal) {
        if (principal != null && !isLibrarian(principal)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only the Librarian (Role 5) is authorized to delete books.");
        }

        try {
            libraryService.deleteLibrary(bookid);
            return ResponseEntity.ok("Book Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Book Not Found");
        }
    }
}