package com.college.cms.controller;

import com.college.cms.entity.CompanyPlacement;
import com.college.cms.service.CompanyPlacementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/companyplacements")
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyPlacementController {

    @Autowired
    private CompanyPlacementService companyPlacementService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveCompany(@RequestBody CompanyPlacement companyPlacement) {

        if (companyPlacement.getCompanyName() == null ||
                companyPlacement.getCompanyName().trim().isEmpty()) {

            return ResponseEntity.badRequest().body("Company Name is required.");
        }

        return ResponseEntity.ok(companyPlacementService.saveCompany(companyPlacement));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<CompanyPlacement>> getAllCompanies() {
        return ResponseEntity.ok(companyPlacementService.getAllCompanies());
    }

    // ================= GET BY ID =================

    @GetMapping("/{companyId}")
    public ResponseEntity<?> getCompanyById(@PathVariable Long companyId) {

        Optional<CompanyPlacement> company =
                companyPlacementService.getCompanyById(companyId);

        if (company.isPresent()) {
            return ResponseEntity.ok(company.get());
        }

        return ResponseEntity.badRequest().body("Company Not Found");
    }

    // ================= UPDATE =================

    @PutMapping("/{companyId}")
    public ResponseEntity<?> updateCompany(
            @PathVariable Long companyId,
            @RequestBody CompanyPlacement companyPlacement) {

        try {

            CompanyPlacement updated =
                    companyPlacementService.updateCompany(companyId, companyPlacement);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Company Not Found");
        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{companyId}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long companyId) {

        try {

            companyPlacementService.deleteCompany(companyId);

            return ResponseEntity.ok("Company Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Company Not Found");
        }
    }
}