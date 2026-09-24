package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @Column(name = "staff_id")
    @JsonProperty("staff_id")
    @JsonAlias({"staffId", "staff_id", "professor_id", "professorId"})
    private Integer staffId;

    @Transient
    @JsonProperty("course_name")
    @JsonAlias({"courseName", "course_name"})
    private String courseName;

    @Transient
    @JsonProperty("exam_name")
    @JsonAlias({"examName", "exam_name"})
    private String examName;

    @Transient
    @JsonProperty("exam_type")
    @JsonAlias({"examType", "exam_type"})
    private String examType;

    @Transient
    @JsonProperty("staff_name")
    @JsonAlias({"staffName", "staff_name", "professor_name", "professorName"})
    private String staffName;
}