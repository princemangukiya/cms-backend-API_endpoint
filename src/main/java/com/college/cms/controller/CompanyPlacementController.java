package com.college.cms.controller;

import com.college.cms.entity.CompanyPlacement;
import com.college.cms.service.CompanyPlacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companyplacements")
@CrossOrigin(origins = "http://localhost:5173") // Matches your React port
public class CompanyPlacementController {

    @Autowired
    private CompanyPlacementService companyPlacementService;

    @PostMapping
    public CompanyPlacement saveCompany(@RequestBody CompanyPlacement companyPlacement) {
        return companyPlacementService.saveCompany(companyPlacement);
    }
}