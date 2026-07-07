package com.college.cms.service.impl;

import com.college.cms.entity.Student;
import com.college.cms.repository.StudentRepository;
import com.college.cms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    @Override
    @Transactional
    public Student updateStudent(Long id, Student student) {
        return studentRepository.findById(id).map(existingStudent -> {
            existingStudent.setStudent_name(student.getStudent_name());
            existingStudent.setMobile_no(student.getMobile_no());
            existingStudent.setGender(student.getGender());
            existingStudent.setAddress(student.getAddress());
            existingStudent.setDob(student.getDob());
            existingStudent.setEmail(student.getEmail());
            existingStudent.setStatus(student.getStatus());
            existingStudent.setRoll_no(student.getRoll_no());
            return studentRepository.save(existingStudent);
        }).orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}