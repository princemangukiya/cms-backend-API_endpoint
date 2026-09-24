package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "exam_detail")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long exam_id;

    @Column(name = "course_id")
    private Integer course_id;

    @Column(name = "exam_type")
    private String exam_type;

    @Column(name = "exam_start_date")
    @JsonAlias({"exam_date", "examDate", "startDate", "date"})
    private LocalDate exam_start_date;

    @Column(name = "exam_start_time")
    @JsonAlias({"start_time", "startTime", "exam_time"})
    private LocalTime exam_start_time;

    @Column(name = "exam_end_time")
    @JsonAlias({"end_time", "endTime"})
    private LocalTime exam_end_time;

    @Column(name = "subject_id")
    private Integer subject_id;

    @Transient
    @JsonProperty("course_name")
    private String course_name;

    @Transient
    @JsonProperty("subject_name")
    private String subject_name;

    public Exam() {
    }

    public Long getExam_id() {
        return exam_id;
    }

    public void setExam_id(Long exam_id) {
        this.exam_id = exam_id;
    }

    public Integer getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Integer course_id) {
        this.course_id = course_id;
    }

    public String getExam_type() {
        return exam_type;
    }

    public void setExam_type(String exam_type) {
        this.exam_type = exam_type;
    }

    @Transient
    @JsonProperty("exam_name")
    public String getExam_name() {
        return this.exam_type;
    }

    public void setExam_name(String exam_name) {
        if (this.exam_type == null || this.exam_type.trim().isEmpty()) {
            this.exam_type = exam_name;
        }
    }

    public LocalDate getExam_start_date() {
        return exam_start_date;
    }

    public void setExam_start_date(LocalDate exam_start_date) {
        this.exam_start_date = exam_start_date;
    }

    public LocalTime getExam_start_time() {
        return exam_start_time;
    }

    public void setExam_start_time(LocalTime exam_start_time) {
        this.exam_start_time = exam_start_time;
    }

    public LocalTime getExam_end_time() {
        return exam_end_time;
    }

    public void setExam_end_time(LocalTime exam_end_time) {
        this.exam_end_time = exam_end_time;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }

    // --- Compatibility helper getters/setters ---
    public LocalDate getExam_date() {
        return exam_start_date;
    }

    public void setExam_date(LocalDate exam_date) {
        this.exam_start_date = exam_date;
    }

    public LocalTime getStart_time() {
        return exam_start_time;
    }

    public void setStart_time(LocalTime start_time) {
        this.exam_start_time = start_time;
    }

    public LocalTime getEnd_time() {
        return exam_end_time;
    }

    public void setEnd_time(LocalTime end_time) {
        this.exam_end_time = end_time;
    }

    @JsonProperty("course_name")
    public String getCourse_name() {
        return course_name;
    }

    @JsonProperty("courseName")
    public String getCourseName() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public void setCourseName(String courseName) {
        this.course_name = courseName;
    }

    @JsonProperty("subject_name")
    public String getSubject_name() {
        return subject_name;
    }

    @JsonProperty("subjectName")
    public String getSubjectName() {
        return subject_name;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public void setSubjectName(String subjectName) {
        this.subject_name = subjectName;
    }
}