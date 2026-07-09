package com.college.cms.service.impl;

import com.college.cms.entity.ClassMgmt;
import com.college.cms.repository.ClassRepository;
import com.college.cms.service.ClassService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Override
    public ClassMgmt saveClass(ClassMgmt classMgmt) {
        return classRepository.save(classMgmt);
    }

    @Override
    public List<ClassMgmt> getAllClasses() {
        return classRepository.findAll();
    }

    @Override
    public Optional<ClassMgmt> getClassById(Long classId) {
        return classRepository.findById(classId);
    }

    @Override
    public ClassMgmt updateClass(Long classId, ClassMgmt classMgmt) {

        ClassMgmt existing = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class Not Found"));

        existing.setClass_name(classMgmt.getClass_name());
        existing.setCourse_id(classMgmt.getCourse_id());
        existing.setBuilding_no(classMgmt.getBuilding_no());
        existing.setFloor_no(classMgmt.getFloor_no());
        existing.setRoom_no(classMgmt.getRoom_no());

        return classRepository.save(existing);
    }

    @Override
    public void deleteClass(Long classId) {

        ClassMgmt existing = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class Not Found"));

        classRepository.delete(existing);
    }
}