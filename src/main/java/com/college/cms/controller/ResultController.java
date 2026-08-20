package com.college.cms.controller;

import com.college.cms.entity.Result;
import com.college.cms.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:5173")
public class ResultController {

    @Autowired
    private ResultService resultService;

    @PostMapping("/save")
    public ResponseEntity<String> saveResult(@RequestBody Result result) {
            try {
            resultService.saveResult(result);
            return ResponseEntity.ok("Result Saved Successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}