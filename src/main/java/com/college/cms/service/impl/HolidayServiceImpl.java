package com.college.cms.service.impl;

import com.college.cms.entity.Holiday;
import com.college.cms.repository.HolidayRepository;
import com.college.cms.service.HolidayService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    @Override
    public Holiday saveHoliday(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    @Override
    public List<Holiday> getAllHoliday() {
        return holidayRepository.findAll();
    }

    @Override
    public Optional<Holiday> getHolidayById(Integer holidayId) {
        return holidayRepository.findById(holidayId);
    }

    @Override
    public Holiday updateHoliday(Integer holidayId, Holiday holiday) {

        Holiday existing = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday Not Found"));

        existing.setHolidayDate(holiday.getHolidayDate());
        existing.setHolidayName(holiday.getHolidayName());

        return holidayRepository.save(existing);
    }

    @Override
    public void deleteHoliday(Integer holidayId) {

        Holiday existing = holidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("Holiday Not Found"));

        holidayRepository.delete(existing);
    }
}