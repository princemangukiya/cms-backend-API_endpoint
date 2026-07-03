package com.college.cms.service.impl;

import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // Register User
    @Override
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    // Login User
    @Override
    public User loginUser(String email, String password) {

        return userRepository.findByEmailId(email)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
}
