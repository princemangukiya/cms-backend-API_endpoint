package com.college.cms.controller;

import com.college.cms.entity.Staff;
import com.college.cms.repository.StaffRepository;
import com.college.cms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/staff", "/api/staff"})
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:5175"})
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private StaffRepository staffRepository;

    // POST: http://localhost:8080/staff/add or /staff or /api/staff
    @PostMapping({"/add", ""})
    public ResponseEntity<?> addStaff(@RequestBody Staff staff) {
        if (staff.getEmail() != null && !staff.getEmail().trim().isEmpty()) {
            List<Staff> existing = staffRepository.findByEmail(staff.getEmail().trim());
            if (!existing.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "DUPLICATE_STAFF_EMAIL",
                    "message", "Staff member with email '" + staff.getEmail().trim() + "' is already registered (#ID: " + existing.get(0).getStaffid() + ")! Please edit the existing staff member instead of creating a duplicate."
                ));
            }
        }
        if (staff.getCourseId() == null && staff.getCourseIds() != null && !staff.getCourseIds().trim().isEmpty()) {
            List<Integer> ids = staff.getAllCourseIds();
            if (!ids.isEmpty()) {
                staff.setCourseId(ids.get(0));
            }
        }
        return ResponseEntity.ok(staffService.addStaff(staff));
    }

    // GET: http://localhost:8080/staff/all or /staff
    @GetMapping({"/all", ""})
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

    // PUT: http://localhost:8080/staff/update/1 or /staff/1
    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @RequestBody Staff staff) {
        if (staff.getEmail() != null && !staff.getEmail().trim().isEmpty()) {
            List<Staff> existing = staffRepository.findByEmail(staff.getEmail().trim());
            for (Staff s : existing) {
                if (!s.getStaffid().equals(id)) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "error", "DUPLICATE_STAFF_EMAIL",
                        "message", "Email '" + staff.getEmail().trim() + "' is already used by another staff member (#ID: " + s.getStaffid() + ")."
                    ));
                }
            }
        }
        if (staff.getCourseId() == null && staff.getCourseIds() != null && !staff.getCourseIds().trim().isEmpty()) {
            List<Integer> ids = staff.getAllCourseIds();
            if (!ids.isEmpty()) {
                staff.setCourseId(ids.get(0));
            }
        }
        Staff updated = staffService.updateStaff(id, staff);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // DELETE: http://localhost:8080/staff/delete/1 or /staff/1
    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteStaff(@PathVariable Integer id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok("Staff deleted successfully!");
    }
}