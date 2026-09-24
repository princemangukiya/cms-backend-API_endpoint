package com.college.cms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_detail")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long user_id;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "mobile_no")
    private String mobile_no;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    private Long role_id;

    @Column(name = "profile_pic", columnDefinition = "LONGTEXT")
    private String profile_pic;

    // Transient field for registration role authorization verification
    @Transient
    private String authCode;

    // Explicit getter taaki kabhi error na aaye
    public Long getRoleId() {
        return this.role_id;
    }

    public String getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}