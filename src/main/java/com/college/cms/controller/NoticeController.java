package com.college.cms.controller;

import com.college.cms.entity.Notice;
import com.college.cms.entity.User;
import com.college.cms.repository.UserRepository;
import com.college.cms.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping({"/api/notices", "/notice", "/notices"})
@CrossOrigin(origins = "*")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserRepository userRepository;

    // ================= GET ALL (Filtered by Role) =================
    @GetMapping({"", "/all"})
    public ResponseEntity<List<Notice>> getAllNotices(
            @RequestParam(name = "roleId", required = false) Integer roleId,
            Principal principal) {

        Integer effectiveRoleId = roleId;

        // If roleId not explicitly passed in query params, resolve from authenticated user
        if (effectiveRoleId == null && principal != null) {
            User user = userRepository.findByEmailId(principal.getName()).orElse(null);
            if (user != null && user.getRoleId() != null) {
                effectiveRoleId = user.getRoleId().intValue();
            }
        }

        List<Notice> notices = noticeService.getNoticesForRole(effectiveRoleId);
        return ResponseEntity.ok(notices);
    }

    // ================= POST / ADD NOTICE =================
    @PostMapping({"", "/add"})
    public ResponseEntity<?> saveNotice(@RequestBody Notice notice, Principal principal) {
        if (notice.getTitle() == null || notice.getTitle().trim().isEmpty() ||
            notice.getMessage() == null || notice.getMessage().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Notice title and message are required.");
        }

        if (notice.getAudience() == null || notice.getAudience().trim().isEmpty()) {
            notice.setAudience("Everyone");
        }

        if (notice.getCategory() == null || notice.getCategory().trim().isEmpty()) {
            notice.setCategory("Academic");
        }

        // Set default author if not provided
        if (notice.getAuthor() == null || notice.getAuthor().trim().isEmpty()) {
            if (principal != null) {
                User user = userRepository.findByEmailId(principal.getName()).orElse(null);
                if (user != null) {
                    if (user.getRoleId() != null && user.getRoleId() == 2L) {
                        notice.setAuthor("Principal Office");
                    } else if (user.getRoleId() != null && user.getRoleId() == 1L) {
                        notice.setAuthor("HOD Department");
                    } else {
                        notice.setAuthor(user.getFull_name() != null ? user.getFull_name() : "Administration");
                    }
                } else {
                    notice.setAuthor("Principal Office");
                }
            } else {
                notice.setAuthor("Principal Office");
            }
        }

        Notice savedNotice = noticeService.saveNotice(notice);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNotice);
    }

    // ================= DELETE NOTICE =================
    @DeleteMapping({"/{noticeId}", "/delete/{noticeId}"})
    public ResponseEntity<?> deleteNotice(@PathVariable Integer noticeId) {
        boolean deleted = noticeService.deleteNotice(noticeId);
        if (deleted) {
            return ResponseEntity.ok("Notice deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notice not found with ID: " + noticeId);
        }
    }
}
