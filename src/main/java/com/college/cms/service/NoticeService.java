package com.college.cms.service;

import com.college.cms.entity.Notice;

import java.util.List;
import java.util.Optional;

public interface NoticeService {

    Notice saveNotice(Notice notice);

    List<Notice> getAllNotices();

    List<Notice> getNoticesForRole(Integer roleId);

    Optional<Notice> getNoticeById(Integer noticeId);

    boolean deleteNotice(Integer noticeId);
}
