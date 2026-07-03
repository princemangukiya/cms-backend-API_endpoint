package com.college.cms.service.impl;

import com.college.cms.entity.CompanyPlacement;
import com.college.cms.repository.CompanyPlacementRepository;
import com.college.cms.service.CompanyPlacementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyPlacementServiceImpl implements CompanyPlacementService {

    @Autowired
    private CompanyPlacementRepository companyPlacementRepository;

    @Override
    public CompanyPlacement saveCompany(CompanyPlacement companyPlacement) {
        return companyPlacementRepository.save(companyPlacement);
    }
}