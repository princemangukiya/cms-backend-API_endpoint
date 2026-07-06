package com.college.cms.service;

import com.college.cms.dto.LoginResponse;
import com.college.cms.entity.User;

public interface UserService {

    User registerUser(User user);

    LoginResponse loginUser(String email, String password);
}