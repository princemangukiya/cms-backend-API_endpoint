package com.college.cms.service.impl;

import com.college.cms.entity.Exam;
import com.college.cms.repository.ExamRepository;
import com.college.cms.service.ExamService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamRepository repository;

    @Override
    public Exam saveExam(Exam exam) {
        return repository.save(exam);
    }

    @Override
    public List<Exam> getAllExams() {
        return repository.findAll();
    }

    @Override
    public Optional<Exam> getExamById(Long examId) {
        return repository.findById(examId);
    }

    @Override
    public Exam updateExam(Long examId, Exam exam) {

        Exam existing = repository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam Not Found"));

        existing.setCourse_id(exam.getCourse_id());
        existing.setExam_type(exam.getExam_type());
        existing.setExam_start_date(exam.getExam_start_date());
        existing.setExam_end_date(exam.getExam_end_date());
        existing.setExam_time(exam.getExam_time());
        existing.setSubject_id(exam.getSubject_id());

        return repository.save(existing);
    }

    @Override
    public void deleteExam(Long examId) {

        Exam existing = repository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam Not Found"));

        repository.delete(existing);
    }
}