package com.college.cms.controller;

import com.college.cms.entity.PlacementStudent;
import com.college.cms.service.PlacementStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/placement")
@CrossOrigin(origins = "*") // "*" sabhi requests ko allow karega
public class PlacementStudentController {

    @Autowired
    private PlacementStudentService placementStudentService;

    @PostMapping("/save")
    public PlacementStudent createPlacement(@RequestBody PlacementStudent placement) {
        return placementStudentService.savePlacementDetail(placement);
    }
}