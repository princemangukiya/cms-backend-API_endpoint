package com.college.cms.controller;

import com.college.cms.entity.Holiday;
import com.college.cms.service.HolidayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/holidays")
@CrossOrigin(origins = "http://localhost:5173")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    // ================= POST =================

    @PostMapping
    public ResponseEntity<?> saveHoliday(@RequestBody Holiday holiday) {

        if (holiday.getHolidayDate() == null || holiday.getHolidayName() == null
                || holiday.getHolidayName().trim().isEmpty()) {

            return ResponseEntity.badRequest().body("Holiday Date and Holiday Name are required.");
        }

        return ResponseEntity.ok(holidayService.saveHoliday(holiday));
    }

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Holiday>> getAllHoliday() {

        return ResponseEntity.ok(holidayService.getAllHoliday());
    }

    // ================= GET BY ID =================

    @GetMapping("/{holidayId}")
    public ResponseEntity<?> getHolidayById(@PathVariable Integer holidayId) {

        Optional<Holiday> holiday = holidayService.getHolidayById(holidayId);

        if (holiday.isPresent()) {
            return ResponseEntity.ok(holiday.get());
        } else {
            return ResponseEntity.badRequest().body("Holiday Not Found");
        }
    }

    // ================= UPDATE =================

    @PutMapping("/{holidayId}")
    public ResponseEntity<?> updateHoliday(@PathVariable Integer holidayId,
                                           @RequestBody Holiday holiday) {

        try {

            Holiday updated = holidayService.updateHoliday(holidayId, holiday);

            return ResponseEntity.ok(updated);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Holiday Not Found");

        }
    }

    // ================= DELETE =================

    @DeleteMapping("/{holidayId}")
    public ResponseEntity<?> deleteHoliday(@PathVariable Integer holidayId) {

        try {

            holidayService.deleteHoliday(holidayId);

            return ResponseEntity.ok("Holiday Deleted Successfully");

        } catch (Exception e) {

            return ResponseEntity.badRequest().body("Holiday Not Found");

        }
    }
}