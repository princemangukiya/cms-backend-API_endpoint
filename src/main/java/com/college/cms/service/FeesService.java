package com.college.cms.service;

import com.college.cms.entity.Fees;

import java.util.List;
import java.util.Optional;

public interface FeesService {

    Fees saveFees(Fees fees);

    List<Fees> getAllFees();

    Optional<Fees> getFeesById(Long id);

    Fees updateFees(Long id, Fees fees);

    void deleteFees(Long id);

}