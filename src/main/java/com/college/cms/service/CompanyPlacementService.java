package com.college.cms.service;

import com.college.cms.entity.CompanyPlacement;

import java.util.List;
import java.util.Optional;

public interface CompanyPlacementService {

    CompanyPlacement saveCompany(CompanyPlacement companyPlacement);

    List<CompanyPlacement> getAllCompanies();

    Optional<CompanyPlacement> getCompanyById(Long companyId);

    CompanyPlacement updateCompany(Long companyId, CompanyPlacement companyPlacement);

    void deleteCompany(Long companyId);

}