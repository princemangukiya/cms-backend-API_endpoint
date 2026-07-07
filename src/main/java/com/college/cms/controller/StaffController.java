package com.college.cms.controller;

import com.college.cms.entity.Staff;
import com.college.cms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    // POST: http://localhost:8080/staff/add
    @PostMapping("/add")
    public ResponseEntity<Staff> addStaff(@RequestBody Staff staff) {
        return ResponseEntity.ok(staffService.addStaff(staff));
    }

    // GET: http://localhost:8080/staff/all
    @GetMapping("/all")
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    // GET: http://localhost:8080/staff/1
    @GetMapping("/{id}")
    public ResponseEntity<Staff> getStaff(@PathVariable Integer id) {
        return staffService.getStaffById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT: http://localhost:8080/staff/update/1
    @PutMapping("/update/{id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable Integer id, @RequestBody Staff staff) {
        Staff updated = staffService.updateStaff(id, staff);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // DELETE: http://localhost:8080/staff/delete/1
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok("Staff deleted successfully!");
    }
}