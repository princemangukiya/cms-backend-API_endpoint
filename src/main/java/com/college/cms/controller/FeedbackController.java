
package com.college.cms.controller;

import com.college.cms.entity.Feedback;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"}, allowCredentials = "true")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private UserRepository userRepository;

    // Helper to resolve role name
    private String getRoleName(Long roleId) {
        if (roleId == null) return "User";
        switch (roleId.intValue()) {
            case 1: return "HOD";
            case 2: return "Principal";
            case 3: return "Professor";
            case 4: return "Student";
            case 5: return "Librarian";
            case 6: return "Placement Officer";
            default: return "User";
        }
    }

    // Populate sender & recipient transient names & roles
    private void populateDetails(List<Feedback> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) return;

        List<User> users = userRepository.findAll();
        Map<Long, User> userMap = new HashMap<>();
        for (User u : users) {
            if (u.getUser_id() != null) {
                userMap.put(u.getUser_id(), u);
            }
        }

        for (Feedback f : feedbacks) {
            // Resolve Sender
            if (f.getFeedbackFrom() != null) {
                User senderUser = userMap.get(f.getFeedbackFrom().longValue());
                if (senderUser != null) {
                    f.setSenderName(senderUser.getFull_name() != null ? senderUser.getFull_name() : senderUser.getEmailId());
                    f.setSenderRole(getRoleName(senderUser.getRoleId()));
                } else if (f.getFeedbackFrom() <= 6) {
                    f.setSenderName(getRoleName(f.getFeedbackFrom().longValue()));
                    f.setSenderRole(getRoleName(f.getFeedbackFrom().longValue()));
                } else {
                    f.setSenderName("User #" + f.getFeedbackFrom());
                    f.setSenderRole("User");
                }
            }

            // Resolve Recipient
            if (f.getFeedbackTo() != null) {
                User recUser = userMap.get(f.getFeedbackTo().longValue());
                if (recUser != null) {
                    f.setRecipientName(recUser.getFull_name() != null ? recUser.getFull_name() : recUser.getEmailId());
                    f.setRecipientRole(getRoleName(recUser.getRoleId()));
                } else if (f.getFeedbackTo() <= 6) {
                    f.setRecipientName(getRoleName(f.getFeedbackTo().longValue()));
                    f.setRecipientRole(getRoleName(f.getFeedbackTo().longValue()));
                } else {
                    f.setRecipientName("User #" + f.getFeedbackTo());
                    f.setRecipientRole("User");
                }
            }
        }
    }

    // ================= GET ELIGIBLE RECIPIENTS (Based on Hierarchy) =================
    @GetMapping("/recipients")
    public ResponseEntity<?> getEligibleRecipients(Principal principal) {
        if (principal == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        String email = principal.getName();
        User caller = userRepository.findByEmailId(email).orElse(null);
        if (caller == null || caller.getRoleId() == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Long callerRole = caller.getRoleId();
        List<Long> targetRoleIds = new ArrayList<>();

        // Hierarchy definition:
        // 1. Student (Role 4) -> Can send to Professors (3), HOD (1), Principal (2), Librarian (5)
        if (callerRole == 4L) {
            targetRoleIds.addAll(Arrays.asList(3L, 1L, 2L, 5L));
        }
        // 2. Professor (Role 3) -> Can send to HOD (1), Principal (2)
        else if (callerRole == 3L) {
            targetRoleIds.addAll(Arrays.asList(1L, 2L));
        }
        // 3. HOD (Role 1) -> Can send to Principal (2)
        else if (callerRole == 1L) {
            targetRoleIds.add(2L);
        }
        // 4. Librarian (Role 5) -> Can send to Principal (2)
        else if (callerRole == 5L) {
            targetRoleIds.add(2L);
        }
        // 5. Principal (Role 2) -> Top authority, views all, doesn't send feedback
        else {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<User> allUsers = userRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (User u : allUsers) {
            if (u.getRoleId() != null && targetRoleIds.contains(u.getRoleId())) {
                // Don't include self
                if (caller.getUser_id() != null && u.getUser_id() != null && caller.getUser_id().equals(u.getUser_id())) {
                    continue;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("userId", u.getUser_id());
                map.put("name", u.getFull_name() != null ? u.getFull_name() : u.getEmailId());
                map.put("email", u.getEmailId());
                map.put("roleId", u.getRoleId());
                map.put("roleName", getRoleName(u.getRoleId()));
                map.put("label", "[" + getRoleName(u.getRoleId()) + "] " + (u.getFull_name() != null ? u.getFull_name() : u.getEmailId()) + " (ID #" + u.getUser_id() + ")");
                result.add(map);
            }
        }

        // Sort by role hierarchy, then alphabetically by name
        result.sort((a, b) -> {
            int rA = ((Long) a.get("roleId")).intValue();
            int rB = ((Long) b.get("roleId")).intValue();
            if (rA != rB) return Integer.compare(rA, rB);
            return ((String) a.get("name")).compareToIgnoreCase((String) b.get("name"));
        });

        return ResponseEntity.ok(result);
    }

    // ================= POST (Submit Feedback) =================
    @PostMapping
    public ResponseEntity<?> saveFeedback(@RequestBody Feedback feedback, Principal principal) {
        if (feedback.getFeedbackTo() == null) {
            return ResponseEntity.badRequest().body("Recipient is required.");
        }

        // Auto-assign sender's user_id from authenticated user session
        if (principal != null) {
            String email = principal.getName();
            User caller = userRepository.findByEmailId(email).orElse(null);
            if (caller != null && caller.getUser_id() != null) {
                feedback.setFeedbackFrom(caller.getUser_id().intValue());
            }
        }

        if (feedback.getFeedbackFrom() == null) {
            return ResponseEntity.badRequest().body("Sender user ID is required.");
        }

        Feedback saved = feedbackService.saveFeedback(feedback);
        populateDetails(Collections.singletonList(saved));
        return ResponseEntity.ok(saved);
    }

    // ================= GET ALL (Filtered strictly by User / Role Isolation) =================
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback(Principal principal) {
        List<Feedback> all = feedbackService.getAllFeedback();

        if (principal != null) {
            String email = principal.getName();
            User caller = userRepository.findByEmailId(email).orElse(null);
            if (caller != null && caller.getRoleId() != null) {
                Long roleId = caller.getRoleId();
                Long userId = caller.getUser_id();

                // 👑 1. Principal (Role 2) -> Sees ALL feedbacks across college
                if (roleId == 2L) {
                    populateDetails(all);
                    return ResponseEntity.ok(all);
                }

                // 🔒 2. Other Roles (Professor, HOD, Student, Librarian) -> Strictly see only feedbacks:
                //    - Sent TO their specific userId (feedback_to == userId)
                //    - Or Sent BY their specific userId (feedback_from == userId)
                //    - Or legacy role-based feedback (feedback_to == roleId when feedback_to <= 6)
                List<Feedback> filtered = all.stream().filter(f -> {
                    boolean isSender = (f.getFeedbackFrom() != null && userId != null && f.getFeedbackFrom().longValue() == userId.longValue());
                    boolean isRecipient = (f.getFeedbackTo() != null && userId != null && f.getFeedbackTo().longValue() == userId.longValue());
                    boolean isLegacyRecipient = (f.getFeedbackTo() != null && f.getFeedbackTo() <= 6 && f.getFeedbackTo().longValue() == roleId.longValue());
                    return isSender || isRecipient || isLegacyRecipient;
                }).collect(Collectors.toList());

                populateDetails(filtered);
                return ResponseEntity.ok(filtered);
            }
        }

        populateDetails(all);
        return ResponseEntity.ok(all);
    }

    // ================= GET FEEDBACKS FOR SPECIFIC USER =================
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbacksForUser(@PathVariable Integer userId) {
        List<Feedback> feedbacks = feedbackService.getFeedbacksForUser(userId);
        populateDetails(feedbacks);
        return ResponseEntity.ok(feedbacks);
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<?> getFeedbackById(@PathVariable Integer id) {
        Optional<Feedback> feedback = feedbackService.getFeedbackById(id);
        if (feedback.isPresent()) {
            Feedback f = feedback.get();
            populateDetails(Collections.singletonList(f));
            return ResponseEntity.ok(f);
        } else {
            return ResponseEntity.badRequest().body("Feedback Not Found");
        }
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeedback(@PathVariable Integer id,
                                            @RequestBody Feedback feedback) {
        try {
            Feedback updated = feedbackService.updateFeedback(id, feedback);
            populateDetails(Collections.singletonList(updated));
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Feedback Not Found");
        }
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Integer id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok("Feedback Deleted Successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Feedback Not Found");
        }
    }
}

