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
        System.out.println("Role ID : " + user.getRoleId());

        Long roleId = user.getRoleId();
        if (roleId == null) {
            user.setRole_id(4L); // Default to Student
            roleId = 4L;
        }

        // 🔒 Real-World College Security Verification:
        // Role 4 (Student) is public registration.
        // All elevated administrative and faculty roles require an official authorization passcode.
        if (roleId != 4L) {
            String authCode = user.getAuthCode();
            String expectedKey = null;
            String roleName = "Unknown";

            if (roleId == 1L) {
                expectedKey = "HOD@CMS2024";
                roleName = "HOD";
            } else if (roleId == 2L) {
                expectedKey = "PRINCIPAL@CMS2024";
                roleName = "Principal";
            } else if (roleId == 3L) {
                expectedKey = "PROF@CMS2024";
                roleName = "Professor";
            } else if (roleId == 5L) {
                expectedKey = "LIB@CMS2024";
                roleName = "Librarian";
            } else if (roleId == 6L) {
                expectedKey = "PLACEMENT@CMS2024";
                roleName = "Placement Officer";
            }

            String masterKey = "CMS@ADMIN#SECURE";

            if (authCode == null || authCode.trim().isEmpty() ||
                (!authCode.trim().equals(expectedKey) && !authCode.trim().equals(masterKey))) {
                System.out.println("❌ REGISTRATION BLOCKED: Invalid authorization code for role: " + roleName);
                throw new SecurityException("Unauthorized: Invalid or missing authorization code for role: " + roleName + ". Only authorized college personnel can create this account.");
            }

            System.out.println("✅ Role authorization verified for " + roleName);
        }

        // 🔍 Check duplicate email before DB constraint violation
        if (user.getEmailId() != null && !user.getEmailId().trim().isEmpty()) {
            String trimmedEmail = user.getEmailId().trim().toLowerCase();
            user.setEmailId(trimmedEmail);

            if (userRepository.findByEmailId(trimmedEmail).isPresent()) {
                System.out.println("❌ DUPLICATE EMAIL ATTEMPT: " + trimmedEmail);
                throw new IllegalArgumentException("Email address '" + trimmedEmail + "' is already registered with another account! Please log in or use a different email address.");
            }
        }

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

        if (!user.getPassword().equals(password)) {
            System.out.println("❌ PASSWORD NOT MATCHED");
            return null;
        }

        System.out.println("✅ PASSWORD MATCHED");

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails);

        return new LoginResponse(token, user);
    }

    @Override
    public User updateUserProfilePic(Long userId, String profilePic) {
        System.out.println("========== UPDATE PROFILE PIC ==========");
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setProfile_pic(profilePic);
            User updatedUser = userRepository.save(user);
            System.out.println("✅ Profile picture saved in DB for user_id: " + userId);
            return updatedUser;
        }
        System.out.println("❌ USER NOT FOUND FOR UPDATE");
        return null;
    }

    // NEW: Reset Password Implementation
    @Override
    public boolean resetPassword(String email, String newPassword) {
        System.out.println("========== RESET PASSWORD SERVICE ==========");
        System.out.println("Target Email : " + email);

        User user = userRepository.findByEmailId(email).orElse(null);
        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
            System.out.println("✅ Password successfully updated in DB for email: " + email);
            return true;
        }

        System.out.println("❌ USER NOT FOUND FOR RESET PASSWORD");
        return false;
    }

    @Override
    public java.util.List<User> getAllUsers() {
        return userRepository.findAll().stream()
                .peek(u -> u.setPassword(null))
                .collect(java.util.stream.Collectors.toList());
    }
}