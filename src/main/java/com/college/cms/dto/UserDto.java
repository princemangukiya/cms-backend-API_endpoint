package com.college.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long user_id;
    private String full_name;
    private String emailId;
    private String mobile_no;
    private Long role_id;
    private String profile_pic;
}