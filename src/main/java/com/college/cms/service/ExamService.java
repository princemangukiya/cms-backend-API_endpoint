package com.college.cms.service;

import com.college.cms.entity.Exam;

import java.util.List;
import java.util.Optional;

public interface ExamService {

    Exam saveExam(Exam exam);

    List<Exam> getAllExams();

    Optional<Exam> getExamById(Long examId);

    Exam updateExam(Long examId, Exam exam);

    void deleteExam(Long examId);

}