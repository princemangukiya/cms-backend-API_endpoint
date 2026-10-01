package com.college.cms.repository;

import com.college.cms.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Integer> {

    List<Notice> findAllByOrderByNoticeIdDesc();

    List<Notice> findByAudienceInOrderByNoticeIdDesc(List<String> audiences);
}
