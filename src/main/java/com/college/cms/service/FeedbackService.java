package com.college.cms.service;

import com.college.cms.entity.Feedback;

import java.util.List;
import java.util.Optional;

public interface FeedbackService {

    Feedback saveFeedback(Feedback feedback);

    List<Feedback> getAllFeedback();

    Optional<Feedback> getFeedbackById(Integer id);

    Feedback updateFeedback(Integer id, Feedback feedback);

    void deleteFeedback(Integer id);

}