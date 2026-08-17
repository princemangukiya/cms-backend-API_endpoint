package com.college.cms.dto;

public class    ForgotPasswordRequest {
    private String emailId;
    private String newPassword;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String emailId, String newPassword) {
        this.emailId = emailId;
        this.newPassword = newPassword;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}