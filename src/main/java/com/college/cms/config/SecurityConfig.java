package com.college.cms.config;

import com.college.cms.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String[] allRoles = {"STUDENT", "PROFESSOR", "HOD", "PRINCIPAL", "LIBRARIAN", "PLACEMENT_OFFICER", "ROLE_STUDENT", "ROLE_PROFESSOR", "ROLE_HOD", "ROLE_PRINCIPAL", "ROLE_LIBRARIAN", "ROLE_PLACEMENT_OFFICER"};
        String[] principalAndHod = {"PRINCIPAL", "HOD", "ROLE_PRINCIPAL", "ROLE_HOD"};
        String[] principalOnly = {"PRINCIPAL", "ROLE_PRINCIPAL"};
        String[] principalAndStudent = {"PRINCIPAL", "STUDENT", "ROLE_PRINCIPAL", "ROLE_STUDENT"};
        String[] academicPaymentViewRoles = {"PRINCIPAL", "STUDENT", "ROLE_PRINCIPAL", "ROLE_STUDENT"};
        String[] professorManageRoles = {"PRINCIPAL", "HOD", "PROFESSOR", "ROLE_PRINCIPAL", "ROLE_HOD", "ROLE_PROFESSOR"};
        String[] librarianManageRoles = {"LIBRARIAN", "ROLE_LIBRARIAN"};
        String[] placementOfficerManageRoles = {"PLACEMENT_OFFICER", "ROLE_PLACEMENT_OFFICER"};

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // 0. ALLOW CORS PREFLIGHT REQUESTS (OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 1. PUBLIC APIS (Login / Register / Forgot Password / Error Dispatches)
                        .requestMatchers("/api/users/login", "/api/users/register", "/users/login", "/users/register", "/api/users/forgot-password", "/error", "/error/**").permitAll()

                        // 2. FEEDBACK MODULE: Allowed for All Logged-in Roles & Public
                        .requestMatchers("/api/feedback", "/api/feedback/**", "/feedback", "/feedback/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/feedback", "/api/feedback/**", "/feedback", "/feedback/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/feedback", "/api/feedback/**", "/feedback", "/feedback/**").permitAll()

                        // 2.5 NOTICE BOARD MODULE: Public GET for all students & faculty, Management for Principal & HOD
                        .requestMatchers(HttpMethod.GET, "/notice", "/notice/**", "/notices", "/notices/**", "/api/notices", "/api/notices/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/notice", "/notice/**", "/notices", "/notices/**", "/api/notices", "/api/notices/**").hasAnyAuthority(principalAndHod)
                        .requestMatchers(HttpMethod.DELETE, "/notice", "/notice/**", "/notices", "/notices/**", "/api/notices", "/api/notices/**").hasAnyAuthority(principalAndHod)

                        // 3. USER PROFILE UPDATE & GET
                        .requestMatchers(HttpMethod.PUT, "/api/users", "/api/users/**").hasAnyAuthority(allRoles)
                        .requestMatchers(HttpMethod.GET, "/api/users", "/api/users/**").hasAnyAuthority(allRoles)

                        // ================= PAYMENT MODULE (Academic View: Principal, HOD, Professor, Student) =================
                        .requestMatchers(HttpMethod.GET,
                                "/payment", "/payment/**", "/api/payment", "/api/payment/**",
                                "/payments", "/payments/**", "/api/payments", "/api/payments/**"
                        ).hasAnyAuthority(academicPaymentViewRoles)

                        .requestMatchers(HttpMethod.POST,
                                "/payment", "/payment/**", "/api/payment", "/api/payment/**",
                                "/payments", "/payments/**", "/api/payments", "/api/payments/**"
                        ).hasAnyAuthority(principalAndStudent)

                        .requestMatchers(HttpMethod.PUT,
                                "/payment", "/payment/**", "/api/payment", "/api/payment/**",
                                "/payments", "/payments/**", "/api/payments", "/api/payments/**"
                        ).hasAnyAuthority(principalOnly)

                        .requestMatchers(HttpMethod.DELETE,
                                "/payment", "/payment/**", "/api/payment", "/api/payment/**",
                                "/payments", "/payments/**", "/api/payments", "/api/payments/**"
                        ).hasAnyAuthority(principalOnly)

                        // ================= LIBRARY & BOOK ISSUE MODULE (Librarian & Principal Access) =================
                        .requestMatchers(HttpMethod.POST,
                                "/library", "/library/**", "/api/library", "/api/library/**",
                                "/api/book-issues", "/api/book-issues/**"
                        ).hasAnyAuthority(librarianManageRoles)

                        .requestMatchers(HttpMethod.PUT,
                                "/library", "/library/**", "/api/library", "/api/library/**",
                                "/api/book-issues", "/api/book-issues/**"
                        ).hasAnyAuthority(librarianManageRoles)

                        .requestMatchers(HttpMethod.DELETE,
                                "/library", "/library/**", "/api/library", "/api/library/**",
                                "/api/book-issues", "/api/book-issues/**"
                        ).hasAnyAuthority(librarianManageRoles)

                        // ================= STAFF MODULE (Principal & HOD Only) =================
                        .requestMatchers(HttpMethod.GET,
                                "/staff", "/staff/**", "/api/staff", "/api/staff/**"
                        ).hasAnyAuthority(principalAndHod)

                        // ================= GET MODULES (All Roles) =================
                        .requestMatchers(HttpMethod.GET,
                                "/student", "/student/**", "/api/student", "/api/student/**",
                                "/course", "/course/**", "/api/course", "/api/course/**",
                                "/courses", "/courses/**", "/api/courses", "/api/courses/**",
                                "/subject", "/subject/**", "/api/subject", "/api/subject/**",
                                "/subjects", "/subjects/**", "/api/subjects", "/api/subjects/**",
                                "/holiday", "/holiday/**", "/api/holiday", "/api/holiday/**",
                                "/holidays", "/holidays/**", "/api/holidays", "/api/holidays/**",
                                "/exam", "/exam/**", "/api/exam", "/api/exam/**",
                                "/exams", "/exams/**", "/api/exams", "/api/exams/**",
                                "/fees", "/fees/**", "/api/fees", "/api/fees/**",
                                "/library", "/library/**", "/api/library", "/api/library/**",
                                "/results", "/results/**", "/api/results", "/api/results/**",
                                "/api/book-issues", "/api/book-issues/**",
                                "/api/attendance", "/api/attendance/**", "/attendance", "/attendance/**",
                                "/class-management", "/class-management/**", "/api/class-management", "/api/class-management/**",
                                "/class", "/class/**", "/api/class", "/api/class/**",
                                "/placement", "/placement/**", "/api/placement", "/api/placement/**",
                                "/placements", "/placements/**", "/api/placements", "/api/placements/**",
                                "/roles", "/roles/**", "/api/roles", "/api/roles/**"
                        ).hasAnyAuthority(allRoles)

                        // ================= ATTENDANCE & RESULTS (Professor / HOD / Principal) =================
                        .requestMatchers(HttpMethod.POST,
                                "/results", "/results/**", "/api/results", "/api/results/**",
                                "/api/attendance", "/api/attendance/**", "/attendance", "/attendance/**"
                        ).hasAnyAuthority(professorManageRoles)

                        .requestMatchers(HttpMethod.PUT,
                                "/results", "/results/**", "/api/results", "/api/results/**",
                                "/api/attendance", "/api/attendance/**", "/attendance", "/attendance/**"
                        ).hasAnyAuthority(professorManageRoles)

                        .requestMatchers(HttpMethod.DELETE,
                                "/results", "/results/**", "/api/results", "/api/results/**",
                                "/api/attendance", "/api/attendance/**", "/attendance", "/attendance/**"
                        ).hasAnyAuthority(professorManageRoles)

                        // ================= CLASS MANAGEMENT (Principal & HOD Only) =================
                        .requestMatchers(HttpMethod.POST,
                                "/class-management", "/class-management/**", "/api/class-management", "/api/class-management/**",
                                "/class", "/class/**", "/api/class", "/api/class/**"
                        ).hasAnyAuthority(principalAndHod)

                        .requestMatchers(HttpMethod.PUT,
                                "/class-management", "/class-management/**", "/api/class-management", "/api/class-management/**",
                                "/class", "/class/**", "/api/class", "/api/class/**"
                        ).hasAnyAuthority(principalAndHod)

                        // ================= PLACEMENT MODULE (Placement Officer Exclusive) =================
                        .requestMatchers(HttpMethod.POST,
                                "/placement", "/placement/**", "/api/placement", "/api/placement/**",
                                "/placements", "/placements/**", "/api/placements", "/api/placements/**"
                        ).hasAnyAuthority(placementOfficerManageRoles)

                        .requestMatchers(HttpMethod.PUT,
                                "/placement", "/placement/**", "/api/placement", "/api/placement/**",
                                "/placements", "/placements/**", "/api/placements", "/api/placements/**"
                        ).hasAnyAuthority(placementOfficerManageRoles)

                        .requestMatchers(HttpMethod.DELETE,
                                "/placement", "/placement/**", "/api/placement", "/api/placement/**",
                                "/placements", "/placements/**", "/api/placements", "/api/placements/**"
                        ).hasAnyAuthority(placementOfficerManageRoles)

                        // ================= EXAM SCHEDULE MODULE (Principal Only Setup/Add/Edit/Delete) =================
                        .requestMatchers(HttpMethod.POST,
                                "/exam", "/exam/**", "/api/exam", "/api/exam/**",
                                "/exams", "/exams/**", "/api/exams", "/api/exams/**"
                        ).hasAnyAuthority(principalOnly)

                        .requestMatchers(HttpMethod.PUT,
                                "/exam", "/exam/**", "/api/exam", "/api/exam/**",
                                "/exams", "/exams/**", "/api/exams", "/api/exams/**"
                        ).hasAnyAuthority(principalOnly)

                        .requestMatchers(HttpMethod.DELETE,
                                "/exam", "/exam/**", "/api/exam", "/api/exam/**",
                                "/exams", "/exams/**", "/api/exams", "/api/exams/**"
                        ).hasAnyAuthority(principalOnly)

                        // ================= OTHER GENERAL MODULES =================
                        .requestMatchers(HttpMethod.POST,
                                "/student", "/student/**", "/api/student", "/api/student/**",
                                "/staff", "/staff/**", "/api/staff", "/api/staff/**",
                                "/course", "/course/**", "/api/course", "/api/course/**",
                                "/courses", "/courses/**", "/api/courses", "/api/courses/**",
                                "/subject", "/subject/**", "/api/subject", "/api/subject/**",
                                "/subjects", "/subjects/**", "/api/subjects", "/api/subjects/**",
                                "/holiday", "/holiday/**", "/api/holiday", "/api/holiday/**",
                                "/holidays", "/holidays/**", "/api/holidays", "/api/holidays/**",
                                "/fees", "/fees/**", "/api/fees", "/api/fees/**"
                        ).hasAnyAuthority(principalAndHod)

                        .requestMatchers(HttpMethod.PUT,
                                "/student", "/student/**", "/api/student", "/api/student/**",
                                "/staff", "/staff/**", "/api/staff", "/api/staff/**",
                                "/course", "/course/**", "/api/course", "/api/course/**",
                                "/courses", "/courses/**", "/api/courses", "/api/courses/**",
                                "/subject", "/subject/**", "/api/subject", "/api/subject/**",
                                "/subjects", "/subjects/**", "/api/subjects", "/api/subjects/**",
                                "/holiday", "/holiday/**", "/api/holiday", "/api/holiday/**",
                                "/holidays", "/holidays/**", "/api/holidays", "/api/holidays/**",
                                "/fees", "/fees/**", "/api/fees", "/api/fees/**"
                        ).hasAnyAuthority(principalAndHod)

                        .requestMatchers(HttpMethod.DELETE,
                                "/student", "/student/**", "/api/student", "/api/student/**",
                                "/staff", "/staff/**", "/api/staff", "/api/staff/**",
                                "/course", "/course/**", "/api/course", "/api/course/**",
                                "/course", "/courses/**", "/api/courses", "/api/courses/**",
                                "/subject", "/subject/**", "/api/subject", "/api/subject/**",
                                "/subjects", "/subjects/**", "/api/subjects", "/api/subjects/**",
                                "/holiday", "/holiday/**", "/api/holiday", "/api/holiday/**",
                                "/holidays", "/holidays/**", "/api/holidays", "/api/holidays/**",
                                "/fees", "/fees/**", "/api/fees", "/api/fees/**",
                                "/class-management", "/class-management/**", "/api/class-management", "/api/class-management/**",
                                "/class", "/class/**", "/api/class", "/api/class/**"
                        ).hasAnyAuthority(principalAndHod)

                        // ALL OTHER REQUESTS MUST BE AUTHENTICATED
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 🔥 Allows any localhost port (5173, 5174, 5175, etc.)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:*",
                "http://127.0.0.1:*"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
