package com.college.cms.service.impl;

import com.college.cms.dto.LoginResponse;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.UserService;
import com.college.cms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private com.college.cms.security.CustomUserDetailsService customUserDetailsService;

    @Override
    public User registerUser(User user) {

        System.out.println("========== REGISTER ==========");
        System.out.println("Email : " + user.getEmailId());

        // Plain Text Password Save
        return userRepository.save(user);
    }

    @Override
    public LoginResponse loginUser(String email, String password) {

        System.out.println("========== LOGIN SERVICE ==========");
        System.out.println("Entered Email : " + email);
        System.out.println("Entered Password : " + password);

        User user = userRepository.findByEmailId(email).orElse(null);

        if (user == null) {
            System.out.println("❌ USER NOT FOUND");
            return null;
        }

        System.out.println("Database Email : " + user.getEmailId());
        System.out.println("Database Password : " + user.getPassword());

        // Plain Text Password Compare
        if (!user.getPassword().equals(password)) {
            System.out.println("❌ PASSWORD NOT MATCHED");
            return null;
        }

        System.out.println("✅ PASSWORD MATCHED");

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(email);

        System.out.println("UserDetails Loaded : " + userDetails.getUsername());

        String token = jwtUtil.generateToken(userDetails);

        System.out.println("JWT Token Generated Successfully");
        System.out.println("===================================");

        return new LoginResponse(token, user);
    }
}