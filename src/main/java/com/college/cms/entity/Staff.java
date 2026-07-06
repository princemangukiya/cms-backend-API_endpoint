package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "staff_detail")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffid")
    private Integer staffid;

    @Column(name = "staffname")
    private String staffname;

    @Column(name = "designation")
    private String designation;

    @Column(name = "mobileno")
    private String mobileno;

    @Column(name = "gender")
    private String gender;

    @Column(name = "address")
    private String address;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "email")
    private String email;

    @Column(name = "joiningdate")
    private LocalDate joiningdate;

    @Column(name = "salary")
    private Double salary;

    @Column(name = "user_id")
    @JsonProperty("user_id")
    private Integer userId;

    public Staff() {}

    public Integer getStaffid() { return staffid; }
    public void setStaffid(Integer staffid) { this.staffid = staffid; }

    public String getStaffname() { return staffname; }
    public void setStaffname(String staffname) { this.staffname = staffname; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getMobileno() { return mobileno; }
    public void setMobileno(String mobileno) { this.mobileno = mobileno; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getJoiningdate() { return joiningdate; }
    public void setJoiningdate(LocalDate joiningdate) { this.joiningdate = joiningdate; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
}