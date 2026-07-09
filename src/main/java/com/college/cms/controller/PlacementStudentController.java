package com.college.cms.controller;

import com.college.cms.entity.PlacementStudent;
import com.college.cms.service.PlacementStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/placement")
@CrossOrigin(origins = "http://localhost:5173")
public class PlacementStudentController {

    @Autowired
    private PlacementStudentService placementStudentService;

    // POST
    @PostMapping
    public ResponseEntity<?> savePlacement(@RequestBody PlacementStudent placement) {

        if (placement.getCompany_id() == null || placement.getStudent_id() == null) {
            return ResponseEntity.badRequest()
                    .body("Company ID and Student ID are required.");
        }

        return ResponseEntity.ok(
                placementStudentService.savePlacementDetail(placement));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<PlacementStudent>> getAllPlacements() {
        return ResponseEntity.ok(
                placementStudentService.getAllPlacements());
    }

    // GET BY ID
    @GetMapping("/{placementId}")
    public ResponseEntity<?> getPlacementById(@PathVariable Long placementId) {

        Optional<PlacementStudent> placement =
                placementStudentService.getPlacementById(placementId);

        if (placement.isPresent()) {
            return ResponseEntity.ok(placement.get());
        }

        return ResponseEntity.badRequest().body("Placement Not Found");
    }

    // UPDATE
    @PutMapping("/{placementId}")
    public ResponseEntity<?> updatePlacement(
            @PathVariable Long placementId,
            @RequestBody PlacementStudent placement) {

        try {

            PlacementStudent updated =
                    placementStudentService.updatePlacement(placementId, placement);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Placement Not Found");
        }
    }

    // DELETE
    @DeleteMapping("/{placementId}")
    public ResponseEntity<?> deletePlacement(
            @PathVariable Long placementId) {

        try {

            placementStudentService.deletePlacement(placementId);

            return ResponseEntity.ok("Placement Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Placement Not Found");
        }
    }
}