package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "subject_detail")
@Data
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subject_id;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "subject_credit")
    private Integer subjectCredit;

    @Column(name = "subject_type")
    private String subjectType;

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "exam_id")
    private Integer examId;
}