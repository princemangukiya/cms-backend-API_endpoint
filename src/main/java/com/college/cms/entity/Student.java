
package com.college.cms.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "student_detail")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long student_id;

    private String student_name;
    private String mobile_no;
    private String gender;
    private String address;
    private LocalDate dob;
    private String email;
    private LocalDate admission_date;
    private String status;
    private Integer user_id;
    private String roll_no;

    // Getters and Setters (Manual)
    public Long getStudent_id() { return student_id; }
    public void setStudent_id(Long student_id) { this.student_id = student_id; }

    public String getStudent_name() { return student_name; }
    public void setStudent_name(String student_name) { this.student_name = student_name; }

    public String getMobile_no() { return mobile_no; }
    public void setMobile_no(String mobile_no) { this.mobile_no = mobile_no; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDate getAdmission_date() { return admission_date; }
    public void setAdmission_date(LocalDate admission_date) { this.admission_date = admission_date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public String getRoll_no() { return roll_no; }
    public void setRoll_no(String roll_no) { this.roll_no = roll_no; }
}

