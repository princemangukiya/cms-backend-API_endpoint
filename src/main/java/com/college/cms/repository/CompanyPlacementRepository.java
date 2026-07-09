package com.college.cms.repository;

import com.college.cms.entity.CompanyPlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPlacementRepository extends JpaRepository<CompanyPlacement, Long> {

}