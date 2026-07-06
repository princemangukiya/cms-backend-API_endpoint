package com.college.cms.service.impl;

import com.college.cms.entity.Student;
import com.college.cms.repository.StudentRepository;
import com.college.cms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    @Transactional
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
}