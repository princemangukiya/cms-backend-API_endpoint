package com.college.cms.service.impl;

import com.college.cms.entity.PlacementStudent;
import com.college.cms.repository.PlacementStudentRepository;
import com.college.cms.service.PlacementStudentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlacementStudentServiceImpl implements PlacementStudentService {

    @Autowired
    private PlacementStudentRepository placementStudentRepository;

    @Override
    public PlacementStudent savePlacementDetail(PlacementStudent placement) {
        return placementStudentRepository.save(placement);
    }

    @Override
    public List<PlacementStudent> getAllPlacements() {
        return placementStudentRepository.findAll();
    }

    @Override
    public Optional<PlacementStudent> getPlacementById(Long placementId) {
        return placementStudentRepository.findById(placementId);
    }

    @Override
    public PlacementStudent updatePlacement(Long placementId,
                                            PlacementStudent placement) {

        PlacementStudent existing = placementStudentRepository.findById(placementId)
                .orElseThrow(() -> new RuntimeException("Placement Not Found"));

        existing.setCompany_id(placement.getCompany_id());
        existing.setStudent_id(placement.getStudent_id());
        existing.setInterview_date(placement.getInterview_date());
        existing.setStatus(placement.getStatus());

        return placementStudentRepository.save(existing);
    }

    @Override
    public void deletePlacement(Long placementId) {

        PlacementStudent existing = placementStudentRepository.findById(placementId)
                .orElseThrow(() -> new RuntimeException("Placement Not Found"));

        placementStudentRepository.delete(existing);
    }
}