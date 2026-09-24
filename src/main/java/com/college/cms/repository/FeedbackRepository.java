package com.college.cms.repository;

import com.college.cms.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByFeedbackTo(Integer feedbackTo);
    List<Feedback> findByFeedbackFrom(Integer feedbackFrom);
    List<Feedback> findByFeedbackToOrFeedbackFrom(Integer feedbackTo, Integer feedbackFrom);
}