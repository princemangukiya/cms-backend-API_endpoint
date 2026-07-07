package com.college.cms.controller;

import com.college.cms.entity.Subject;
import com.college.cms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/subjects")
@CrossOrigin(origins = "http://localhost:5173")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    // POST: http://localhost:8080/api/subjects
    @PostMapping("/save")
    public Subject addSubject(@RequestBody Subject subject) {
        return subjectService.saveSubject(subject);
    }

    // GET: http://localhost:8080/api/subjects
    @GetMapping("/all")
    public List<Subject> getAll() {
        return subjectService.getAllSubjects();
    }

    // GET: http://localhost:8080/api/subjects/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getById(@PathVariable Long id) {
        Subject s = subjectService.getSubjectById(id);
        return s != null ? ResponseEntity.ok(s) : ResponseEntity.notFound().build();
    }

    // PUT: http://localhost:8080/api/subjects/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Subject> update(@PathVariable Long id, @RequestBody Subject subject) {
        Subject updated = subjectService.updateSubject(id, subject);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // DELETE: http://localhost:8080/api/subjects/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Subject deleted successfully");
    }
}