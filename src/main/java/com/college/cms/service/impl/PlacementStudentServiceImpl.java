package com.college.cms.service.impl;

import com.college.cms.entity.PlacementStudent;
import com.college.cms.repository.PlacementStudentRepository;
import com.college.cms.service.PlacementStudentService; // Import the interface
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlacementStudentServiceImpl implements PlacementStudentService {

    @Autowired
    private PlacementStudentRepository repository;

    @Override
    public PlacementStudent savePlacementDetail(PlacementStudent placement) {
        return repository.save(placement);
    }
}