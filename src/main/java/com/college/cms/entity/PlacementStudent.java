package com.college.cms.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "placement_student_detail")
public class PlacementStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placement_id")
    private Long placement_id;

    @Column(name = "company_id")
    private Long company_id;

    @Column(name = "student_id")
    private Integer student_id;

    @Temporal(TemporalType.DATE)
    @Column(name = "interview_date")
    private Date interview_date;

    @Column(name = "status")
    private String status;

    public PlacementStudent() {
    }

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