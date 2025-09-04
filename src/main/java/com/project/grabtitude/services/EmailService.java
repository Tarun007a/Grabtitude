package com.project.grabtitude.services;

import java.io.IOException;

public interface EmailService {
    public void send(String to, String subject, String body);
    public void sendOtpEmail(String toEmail, String otp);
    public void sendVerificationEmail(String toEmail, String verificationLink);
}
