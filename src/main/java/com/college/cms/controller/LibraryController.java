package com.college.cms.controller;

import com.college.cms.entity.Library;
import com.college.cms.service.LibraryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/library")
@CrossOrigin(origins = "http://localhost:5173")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveLibrary(@RequestBody Library library) {

        if (library.getBookname() == null || library.getBookname().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Book Name is required.");
        }

        return ResponseEntity.ok(libraryService.saveLibrary(library));
    }

    // ================= GET ALL =================

    @GetMapping
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

    // ================= UPDATE =================

    @PutMapping("/{bookid}")
    public ResponseEntity<?> updateLibrary(@PathVariable Long bookid,
                                           @RequestBody Library library) {

        try {

            Library updated = libraryService.updateLibrary(bookid, library);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Book Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{bookid}")
    public ResponseEntity<?> deleteLibrary(@PathVariable Long bookid) {

        try {

            libraryService.deleteLibrary(bookid);

            return ResponseEntity.ok("Book Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Book Not Found");

        }
    }
}