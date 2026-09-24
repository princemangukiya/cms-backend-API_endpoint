package com.college.cms.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "feedback_detail")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "feedback_id")
    @JsonProperty("feedback_id")
    @JsonAlias({"feedbackId", "feedback_id"})
    private Integer feedbackId;

    @Column(name = "feedback_from")
    @JsonProperty("feedback_from")
    @JsonAlias({"feedbackFrom", "feedback_from"})
    private Integer feedbackFrom;

    @Column(name = "feedback_to")
    @JsonProperty("feedback_to")
    @JsonAlias({"feedbackTo", "feedback_to"})
    private Integer feedbackTo;

    @Column(name = "rating")
    @JsonProperty("rating")
    @JsonAlias({"rating"})
    private Integer rating;

    @Column(name = "feedback_message")
    @JsonProperty("feedback_message")
    @JsonAlias({"feedbackMessage", "feedback_message"})
    private String feedbackMessage;

    @Transient
    @JsonProperty("sender_name")
    @JsonAlias({"senderName", "sender_name"})
    private String senderName;

    @Transient
    @JsonProperty("sender_role")
    @JsonAlias({"senderRole", "sender_role"})
    private String senderRole;

    @Transient
    @JsonProperty("recipient_name")
    @JsonAlias({"recipientName", "recipient_name"})
    private String recipientName;

    @Transient
    @JsonProperty("recipient_role")
    @JsonAlias({"recipientRole", "recipient_role"})
    private String recipientRole;

    public Feedback() {
    }

    public Integer getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(Integer feedbackId) {
        this.feedbackId = feedbackId;
    }

    public Integer getFeedbackFrom() {
        return feedbackFrom;
    }

    public void setFeedbackFrom(Integer feedbackFrom) {
        this.feedbackFrom = feedbackFrom;
    }

    public Integer getFeedbackTo() {
        return feedbackTo;
    }

    public void setFeedbackTo(Integer feedbackTo) {
        this.feedbackTo = feedbackTo;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientRole() {
        return recipientRole;
    }

    public void setRecipientRole(String recipientRole) {
        this.recipientRole = recipientRole;
    }
}