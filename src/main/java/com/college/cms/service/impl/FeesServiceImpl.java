package com.college.cms.service.impl;

import com.college.cms.entity.Fees;
import com.college.cms.repository.FeesRepository;
import com.college.cms.service.FeesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeesServiceImpl implements FeesService {

    @Autowired
    private FeesRepository feesRepository;

    @Override
    public Fees saveFees(Fees fees) {
        return feesRepository.save(fees);
    }

    @Override
    public List<Fees> getAllFees() {
        return feesRepository.findAll();
    }

    @Override
    public Optional<Fees> getFeesById(Long id) {
        return feesRepository.findById(id);
    }

    @Override
    public Fees updateFees(Long id, Fees fees) {

        Fees existing = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fees Not Found"));

        existing.setCourseId(fees.getCourseId());
        existing.setStudentId(fees.getStudentId());
        existing.setScholarship(fees.getScholarship());
        existing.setDiscountPercentage(fees.getDiscountPercentage());
        existing.setTotalFees(fees.getTotalFees());

        return feesRepository.save(existing);
    }

    @Override
    public void deleteFees(Long id) {

        Fees existing = feesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fees Not Found"));

        feesRepository.delete(existing);
    }
}