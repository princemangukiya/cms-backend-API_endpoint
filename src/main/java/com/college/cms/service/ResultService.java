package com.college.cms.service;

import com.college.cms.dto.ResultResponseDTO;
import com.college.cms.entity.Result;
import java.util.List;

public interface ResultService {
    void saveResult(Result result);
    List<ResultResponseDTO> getAllResultsDTO();
}