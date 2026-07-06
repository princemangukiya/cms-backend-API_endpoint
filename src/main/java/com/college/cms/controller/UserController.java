package com.college.cms.controller;

import com.college.cms.dto.LoginResponse;
import com.college.cms.entity.User;
import com.college.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired
    private UserService userService;

    // Register User
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {

        System.out.println("========== REGISTER API HIT ==========");

        User savedUser = userService.registerUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {

        System.out.println("========== LOGIN API HIT ==========");
        System.out.println("Email : " + user.getEmailId());

        if (user.getEmailId() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email or Password cannot be empty");
        }

        LoginResponse response =
                userService.loginUser(user.getEmailId(), user.getPassword());

        if (response == null) {
            System.out.println("LOGIN FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Email or Password");
        }

        System.out.println("LOGIN SUCCESS");
        System.out.println("TOKEN : " + response.getToken());

        return ResponseEntity.ok(response);
    }
}