package com.college.cms.service.impl;

import com.college.cms.entity.Feedback;
import com.college.cms.repository.FeedbackRepository;
import com.college.cms.service.FeedbackService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    @Override
    public Optional<Feedback> getFeedbackById(Integer id) {
        return feedbackRepository.findById(id);
    }

    @Override
    public Feedback updateFeedback(Integer id, Feedback feedback) {

        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback Not Found"));

        existing.setFeedbackFrom(feedback.getFeedbackFrom());
        existing.setFeedbackTo(feedback.getFeedbackTo());
        existing.setRating(feedback.getRating());
        existing.setFeedbackMessage(feedback.getFeedbackMessage());

        return feedbackRepository.save(existing);
    }

    @Override
    public void deleteFeedback(Integer id) {

        Feedback existing = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback Not Found"));

        feedbackRepository.delete(existing);
    }
}