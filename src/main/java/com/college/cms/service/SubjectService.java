package com.college.cms.service;

import com.college.cms.entity.Subject;
import java.util.List;

public interface SubjectService {
    Subject saveSubject(Subject subject);
    List<Subject> getAllSubjects();
    Subject getSubjectById(Long id);
    Subject updateSubject(Long id, Subject subject);
    void deleteSubject(Long id);
}