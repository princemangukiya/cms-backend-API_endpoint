package com.college.cms.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String full_name;

    private String emailId;

    private String mobile_no;

    private String password;

    private Long role_id;
}