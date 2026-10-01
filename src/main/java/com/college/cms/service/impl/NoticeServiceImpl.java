package com.college.cms.service.impl;

import com.college.cms.entity.Notice;
import com.college.cms.repository.NoticeRepository;
import com.college.cms.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Override
    public Notice saveNotice(Notice notice) {
        if (notice.getCreatedDate() == null || notice.getCreatedDate().trim().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy, hh:mm a");
            notice.setCreatedDate(LocalDateTime.now().format(formatter));
        }
        if (notice.getPriority() == null || notice.getPriority().trim().isEmpty()) {
            if ("Urgent".equalsIgnoreCase(notice.getCategory())) {
                notice.setPriority("high");
            } else {
                notice.setPriority("medium");
            }
        }
        return noticeRepository.save(notice);
    }

    @Override
    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByNoticeIdDesc();
    }

    @Override
    public List<Notice> getNoticesForRole(Integer roleId) {
        if (roleId == null) {
            return noticeRepository.findAllByOrderByNoticeIdDesc();
        }

        List<String> allowedAudiences;
        if (roleId == 2) {
            // Principal (Role 2): Strictly sees Principal notices & Everyone (NOT All Students, NOT All Professors, NOT All HODs)
            allowedAudiences = Arrays.asList(
                    "Everyone", "everyone", "Everyone (All Roles)",
                    "All Principal", "all principal", "Principal Only", "principal only",
                    "All Students & Staff", "all students & staff",
                    "Staff", "staff"
            );
        } else if (roleId == 4) {
            // Student (Role 4): Strictly sees Student notices & Everyone
            allowedAudiences = Arrays.asList(
                    "Everyone", "everyone", "Everyone (All Roles)",
                    "All Students", "all students",
                    "Final Year Students", "final year students",
                    "All Students & Staff", "all students & staff"
            );
        } else if (roleId == 3) {
            // Professor (Role 3): Strictly sees Professor notices & Everyone (NOT All Students, NOT HOD Only, NOT Principal)
            allowedAudiences = Arrays.asList(
                    "Everyone", "everyone", "Everyone (All Roles)",
                    "All Professors", "all professors", "All Professors / Faculty",
                    "Faculty", "faculty",
                    "All Students & Staff", "all students & staff",
                    "Staff", "staff"
            );
        } else if (roleId == 1) {
            // HOD (Role 1): Strictly sees HOD Only notices & Everyone (NOT All Students, NOT All Professors, NOT Principal)
            allowedAudiences = Arrays.asList(
                    "Everyone", "everyone", "Everyone (All Roles)",
                    "All HODs", "all hods", "HOD Only", "hod only", "HOD Only (Heads of Dept)",
                    "All Students & Staff", "all students & staff",
                    "Staff", "staff"
            );
        } else {
            // Other roles (Librarian, Placement Officer, etc.)
            allowedAudiences = Arrays.asList(
                    "Everyone", "everyone", "Everyone (All Roles)",
                    "All Students & Staff", "all students & staff",
                    "Staff", "staff"
            );
        }

        return noticeRepository.findByAudienceInOrderByNoticeIdDesc(allowedAudiences);
    }

    @Override
    public Optional<Notice> getNoticeById(Integer noticeId) {
        return noticeRepository.findById(noticeId);
    }

    @Override
    public boolean deleteNotice(Integer noticeId) {
        if (noticeRepository.existsById(noticeId)) {
            noticeRepository.deleteById(noticeId);
            return true;
        }
        return false;
    }
}
