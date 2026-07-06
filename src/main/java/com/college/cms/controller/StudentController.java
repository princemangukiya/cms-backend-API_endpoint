
package com.college.cms.controller;

import com.college.cms.entity.Student;
import com.college.cms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        System.out.println("DEBUG: Saving student: " + student.getStudent_name());
        return studentService.saveStudent(student);
    }
}
