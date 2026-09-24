package com.college.cms.repository;

import com.college.cms.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE LOWER(s.email) = LOWER(:email)")
    List<Student> findByEmail(@Param("email") String email);

    @Query("SELECT s FROM Student s WHERE s.user_id = :userId")
    List<Student> findByUserId(@Param("userId") Integer userId);

    @Query("SELECT s FROM Student s WHERE s.mobile_no = :mobileNo")
    List<Student> findByMobileNo(@Param("mobileNo") String mobileNo);

    @Query("SELECT s FROM Student s WHERE LOWER(s.student_name) = LOWER(:name)")
    List<Student> findByStudentName(@Param("name") String name);

    @Query("SELECT s FROM Student s WHERE s.course_id IN :courseIds")
    List<Student> findByCourseIdIn(@Param("courseIds") List<Integer> courseIds);
}