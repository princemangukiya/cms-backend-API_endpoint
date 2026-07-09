package com.college.cms.entity;

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
    private LocalDate exam_start_date;

    @Column(name = "exam_end_date")
    private LocalDate exam_end_date;

    @Column(name = "exam_time")
    private LocalTime exam_time;

    @Column(name = "subject_id")
    private Integer subject_id;

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

    public LocalDate getExam_start_date() {
        return exam_start_date;
    }

    public void setExam_start_date(LocalDate exam_start_date) {
        this.exam_start_date = exam_start_date;
    }

    public LocalDate getExam_end_date() {
        return exam_end_date;
    }

    public void setExam_end_date(LocalDate exam_end_date) {
        this.exam_end_date = exam_end_date;
    }

    public LocalTime getExam_time() {
        return exam_time;
    }

    public void setExam_time(LocalTime exam_time) {
        this.exam_time = exam_time;
    }

    public Integer getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Integer subject_id) {
        this.subject_id = subject_id;
    }
}