package com.college.cms.controller;

import com.college.cms.dto.LoginResponse;
import com.college.cms.entity.User;
import com.college.cms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
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

        if (user.getEmailId() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email or Password cannot be empty");
        }

        LoginResponse response = userService.loginUser(user.getEmailId(), user.getPassword());

        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Email or Password");
        }

        return ResponseEntity.ok(response);
    }

    // Update Profile Picture API
    @PutMapping("/{userId}/update-profile-pic")
    public ResponseEntity<?> updateProfilePic(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        String profilePic = payload.get("profilePic");

        User updatedUser = userService.updateUserProfilePic(userId, profilePic);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
}