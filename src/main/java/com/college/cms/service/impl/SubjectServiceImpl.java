package com.college.cms.service.impl;

import com.college.cms.entity.Subject;
import com.college.cms.repository.SubjectRepository;
import com.college.cms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public Subject saveSubject(Subject subject) { return subjectRepository.save(subject); }

    @Override
    public List<Subject> getAllSubjects() { return subjectRepository.findAll(); }

    @Override
    public Subject getSubjectById(Long id) { return subjectRepository.findById(id).orElse(null); }

    @Override
    public Subject updateSubject(Long id, Subject subject) {
        if (subjectRepository.existsById(id)) {
            subject.setSubject_id(id);
            return subjectRepository.save(subject);
        }
        return null;
    }

    @Override
    public void deleteSubject(Long id) { subjectRepository.deleteById(id); }
}