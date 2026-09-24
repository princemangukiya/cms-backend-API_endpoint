package com.college.cms.security;

import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with email: " + email));

        // Corrected Lombok getter: getRoleId()
        Long roleId = user.getRoleId();
        String roleName = "ROLE_STUDENT"; // Default

        if (roleId != null) {
            if (roleId == 1L) {
                roleName = "ROLE_HOD";
            } else if (roleId == 2L) {
                roleName = "ROLE_PRINCIPAL";
            } else if (roleId == 3L) {
                roleName = "ROLE_PROFESSOR";
            } else if (roleId == 4L) {
                roleName = "ROLE_STUDENT";
            } else if (roleId == 5L) {
                roleName = "ROLE_LIBRARIAN";
            } else if (roleId == 6L) {
                roleName = "ROLE_PLACEMENT_OFFICER";
            }
        }

        System.out.println("User Email: " + email + " | Assigned Role: " + roleName);

        return new org.springframework.security.core.userdetails.User(
                user.getEmailId(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }
}