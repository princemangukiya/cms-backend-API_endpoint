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
    public ResponseEntity<?> register(@RequestBody User user) {
        System.out.println("========== REGISTER API HIT ==========");
        try {
            User savedUser = userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
        } catch (SecurityException se) {
            System.err.println("Security exception on register: " + se.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", "UNAUTHORIZED_ROLE_REGISTRATION",
                "message", se.getMessage()
            ));
        } catch (IllegalArgumentException iae) {
            System.err.println("Duplicate or invalid registration argument: " + iae.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "DUPLICATE_EMAIL",
                "message", iae.getMessage()
            ));
        } catch (org.springframework.dao.DataIntegrityViolationException dive) {
            System.err.println("Data integrity violation on register: " + dive.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "DUPLICATE_ENTRY",
                "message", "This Email Address or Mobile Number is already registered! Please log in or use a different email address."
            ));
        } catch (Exception e) {
            System.err.println("Registration error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", "REGISTRATION_FAILED",
                "message", e.getMessage()
            ));
        }
    }

    // Get All Users for ID & Name Lookups
    @GetMapping({"", "/all"})
    public ResponseEntity<java.util.List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        System.out.println("========== LOGIN API HIT ==========");
        System.out.println("Email : " + user.getEmailId());

        if (user.getEmailId() == null || user.getPassword() == null) {
            return ResponseEntity.badRequest().body("Email or Password cannot be empty");
        }

        LoginResponse response = userService.loginUser(user.getEmailId(), user.getPassword());

        if (response == null) {
            System.out.println("LOGIN FAILED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Email or Password");
        }

        System.out.println("LOGIN SUCCESS");
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

    // Forgot Password API
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || newPassword == null || email.trim().isEmpty() || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email and New Password are required");
        }

        boolean isReset = userService.resetPassword(email, newPassword);
        if (isReset) {
            return ResponseEntity.ok("Password updated successfully");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with this email");
    }
}