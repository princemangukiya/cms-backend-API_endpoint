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
    public Course updateCourse(Integer courseId, Course updated) {
        return courseRepository.findById(courseId).map(existing -> {
            if (updated.getCourseName() != null && !updated.getCourseName().trim().isEmpty()) {
                existing.setCourseName(updated.getCourseName().trim());
            }
            if (updated.getSemester() != null && !updated.getSemester().trim().isEmpty()) {
                existing.setSemester(updated.getSemester().trim());
            }
            if (updated.getCourseFee() != null) {
                existing.setCourseFee(updated.getCourseFee());
            }
            if (updated.getCredits() != null) {
                existing.setCredits(updated.getCredits());
            }
            if (updated.getDepartment() != null) {
                existing.setDepartment(updated.getDepartment());
            }
            if (updated.getCourseCode() != null) {
                existing.setCourseCode(updated.getCourseCode());
            }
            if (updated.getDescription() != null) {
                existing.setDescription(updated.getDescription());
            }
            return courseRepository.save(existing);
        }).orElse(null);
    }

    @Override
    public void deleteCourse(Integer courseId) { courseRepository.deleteById(courseId); }
}