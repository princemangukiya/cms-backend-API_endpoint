package com.college.cms.service.impl;

import com.college.cms.entity.CompanyPlacement;
import com.college.cms.repository.CompanyPlacementRepository;
import com.college.cms.service.CompanyPlacementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyPlacementServiceImpl implements CompanyPlacementService {

    @Autowired
    private CompanyPlacementRepository companyPlacementRepository;

    @Override
    public CompanyPlacement saveCompany(CompanyPlacement companyPlacement) {
        return companyPlacementRepository.save(companyPlacement);
    }

    @Override
    public List<CompanyPlacement> getAllCompanies() {
        return companyPlacementRepository.findAll();
    }

    @Override
    public Optional<CompanyPlacement> getCompanyById(Long companyId) {
        return companyPlacementRepository.findById(companyId);
    }

    @Override
    public CompanyPlacement updateCompany(Long companyId, CompanyPlacement companyPlacement) {

        CompanyPlacement existing = companyPlacementRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company Not Found"));

        existing.setCompanyName(companyPlacement.getCompanyName());
        existing.setJobRole(companyPlacement.getJobRole());
        existing.setLocation(companyPlacement.getLocation());
        existing.setPackageLpa(companyPlacement.getPackageLpa());
        existing.setWebsite(companyPlacement.getWebsite());

        return companyPlacementRepository.save(existing);
    }

    @Override
    public void deleteCompany(Long companyId) {

        CompanyPlacement existing = companyPlacementRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company Not Found"));

        companyPlacementRepository.delete(existing);
    }
}