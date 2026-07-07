package com.college.cms.service;

import com.college.cms.entity.Course;
import java.util.List;

public interface CourseService {
    Course saveCourse(Course course);
    List<Course> getAllCourses();
    Course getCourseById(Integer courseId);
    Course updateCourse(Integer courseId, Course course);
    void deleteCourse(Integer courseId);
}