package com.college.cms.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "placement_student_detail")
public class PlacementStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placement_id;

    private Long company_id;
    private Integer student_id;

    @Temporal(TemporalType.DATE)
    private Date interview_date;

    private String status;

    // Default Constructor (Required by JPA)
    public PlacementStudent() {
    }

    // Parameterized Constructor
    public PlacementStudent(Long company_id, Integer student_id, Date interview_date, String status) {
        this.company_id = company_id;
        this.student_id = student_id;
        this.interview_date = interview_date;
        this.status = status;
    }

    // Getters and Setters
    public Long getPlacement_id() {
        return placement_id;
    }

    public void setPlacement_id(Long placement_id) {
        this.placement_id = placement_id;
    }

    public Long getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Long company_id) {
        this.company_id = company_id;
    }

    public Integer getStudent_id() {
        return student_id;
    }

    public void setStudent_id(Integer student_id) {
        this.student_id = student_id;
    }

    public Date getInterview_date() {
        return interview_date;
    }

    public void setInterview_date(Date interview_date) {
        this.interview_date = interview_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}