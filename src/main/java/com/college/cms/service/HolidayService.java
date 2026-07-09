package com.college.cms.service;

import com.college.cms.entity.Holiday;

import java.util.List;
import java.util.Optional;

public interface HolidayService {

    Holiday saveHoliday(Holiday holiday);

    List<Holiday> getAllHoliday();

    Optional<Holiday> getHolidayById(Integer holidayId);

    Holiday updateHoliday(Integer holidayId, Holiday holiday);

    void deleteHoliday(Integer holidayId);

}