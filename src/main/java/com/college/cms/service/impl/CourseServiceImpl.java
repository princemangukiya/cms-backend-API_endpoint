package com.college.cms.service.impl;

import com.college.cms.entity.Course;
import com.college.cms.repository.CourseRepository;
import com.college.cms.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) { return courseRepository.save(course); }

    @Override
    public List<Course> getAllCourses() { return courseRepository.findAll(); }

    @Override
    public Course getCourseById(Integer courseId) { return courseRepository.findById(courseId).orElse(null); }

    @Override
    public Course updateCourse(Integer courseId, Course course) {
        if (courseRepository.existsById(courseId)) {
            course.setCourseId(courseId);
            return courseRepository.save(course);
        }
        return null;
    }

    @Override
    public void deleteCourse(Integer courseId) { courseRepository.deleteById(courseId); }
}