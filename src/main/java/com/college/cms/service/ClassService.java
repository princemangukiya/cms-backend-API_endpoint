package com.college.cms.service;

import com.college.cms.entity.ClassMgmt;

import java.util.List;
import java.util.Optional;

public interface ClassService {

    ClassMgmt saveClass(ClassMgmt classMgmt);

    List<ClassMgmt> getAllClasses();

    Optional<ClassMgmt> getClassById(Long classId);

    ClassMgmt updateClass(Long classId, ClassMgmt classMgmt);

    void deleteClass(Long classId);

}