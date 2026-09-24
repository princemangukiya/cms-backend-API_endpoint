package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "course_management")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    @JsonProperty("course_id")
    private Integer courseId;

    @Column(name = "course_name")
    @JsonProperty("course_name")
    private String courseName;

    @Column(name = "semester")
    @JsonProperty("semester")
    private String semester;

    @Column(name = "course_fee")
    @JsonProperty("course_fee")
    private Double courseFee;

    @Column(name = "credits")
    @JsonProperty("credits")
    private String credits;

    @Column(name = "department")
    @JsonProperty("department")
    private String department;

    @Column(name = "course_code")
    @JsonProperty("course_code")
    private String courseCode;

    @Column(name = "description")
    @JsonProperty("description")
    private String description;

    public Course() {}

    // Getters and Setters
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public Double getCourseFee() { return courseFee; }
    public void setCourseFee(Double courseFee) { this.courseFee = courseFee; }

    public String getCredits() { return credits; }
    public void setCredits(String credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}