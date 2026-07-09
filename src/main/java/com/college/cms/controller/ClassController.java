package com.college.cms.controller;

import com.college.cms.entity.ClassMgmt;
import com.college.cms.service.ClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/class-management")
@CrossOrigin(origins = "http://localhost:5173")
public class ClassController {

    @Autowired
    private ClassService classService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveClass(@RequestBody ClassMgmt classMgmt) {

        if (classMgmt.getClass_name() == null || classMgmt.getClass_name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Class Name is required.");
        }

        return ResponseEntity.ok(classService.saveClass(classMgmt));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<ClassMgmt>> getAllClasses() {

        return ResponseEntity.ok(classService.getAllClasses());
    }

    // ================= GET BY ID =================

    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {

        Optional<ClassMgmt> classMgmt = classService.getClassById(id);

        if (classMgmt.isPresent()) {
            return ResponseEntity.ok(classMgmt.get());
        } else {
            return ResponseEntity.badRequest().body("Class Not Found");
        }
    }

    // ================= UPDATE =================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClass(@PathVariable Long id,
                                         @RequestBody ClassMgmt classMgmt) {

        try {

            ClassMgmt updated = classService.updateClass(id, classMgmt);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Class Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {

        try {

            classService.deleteClass(id);

            return ResponseEntity.ok("Class Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Class Not Found");

        }
    }

}