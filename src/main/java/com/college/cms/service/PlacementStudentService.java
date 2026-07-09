package com.college.cms.service;

import com.college.cms.entity.PlacementStudent;

import java.util.List;
import java.util.Optional;

public interface PlacementStudentService {

    PlacementStudent savePlacementDetail(PlacementStudent placement);

    List<PlacementStudent> getAllPlacements();

    Optional<PlacementStudent> getPlacementById(Long placementId);

    PlacementStudent updatePlacement(Long placementId, PlacementStudent placement);

    void deletePlacement(Long placementId);
}