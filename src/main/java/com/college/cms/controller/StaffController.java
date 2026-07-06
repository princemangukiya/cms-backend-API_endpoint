package com.college.cms.controller;

import com.college.cms.entity.Staff;
import com.college.cms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/add")
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        // This will now call your implementation
        Staff savedStaff = staffService.addStaff(staff);
        return ResponseEntity.ok(savedStaff);
    }
}