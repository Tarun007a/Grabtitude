package com.project.grabtitude.services.impl;

import com.project.grabtitude.services.EmailService;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private final SendGrid sendGrid;

    public EmailServiceImpl(SendGrid sendGrid){
        this.sendGrid = sendGrid;
    }

    @Value("${sendgrid.sender.email}")
    private String senderEmail;

    @Value("${sendgrid.sender.name}")
    private String senderName;

    public void send(String to, String subject, String body) {
        try {
            Email sender = new Email(senderEmail, senderName);
            Email recipient = new Email(to);
            Content content = new Content("text/html", body);

            Mail mail = new Mail(sender, subject, recipient, content);

            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            log.info("Status code : " + response.getStatusCode());
            log.info("Body : " + response.getBody());
            log.info("Headers : " + response.getHeaders());
        }
        catch (IOException e){
            log.info(String.valueOf(e.getCause()));
        }
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp){
        Email from = new Email("support@grabtitude.com");
        String subject = "Grabtitude - Password Reset OTP";
        Email to = new Email(toEmail);

        // HTML content
        String htmlContent = """        
        <html>
          <body style="font-family: Arial, sans-serif; color: #333;">
            <h2>Password Reset OTP</h2>
            <p>Hello,</p>
            <p>Use the following OTP to reset your Grabtitude account password:</p>
            <h3 style="color: #007bff;">%s</h3>
            <p>This OTP will expire in 5 minutes.</p>
            <hr>
            <p style="font-size:12px;color:gray;">
              If you did not request this, you can safely ignore this email.
            </p>
          </body>
        </html>
        """.formatted(otp);

        this.send(toEmail, subject, htmlContent);
    }

    @Override
    public void sendVerificationEmail(String toEmail, String verificationLink) {
        String subject = "Grabtitude - Verify Your Email Address";

        // HTML content
        String htmlContent = """
    <html>
      <body style="font-family: Arial, sans-serif; color: #333;">
        <h2>Email Verification</h2>
        <p>Hello,</p>
        <p>Thank you for signing up with <b>Grabtitude</b>.</p>
        <p>Please click the button below to verify your email address:</p>
        <p>
          <a href="%s" 
             style="display:inline-block; padding:10px 20px; color:white; background-color:#007bff;
                    text-decoration:none; border-radius:5px;">
             Verify Email
          </a>
        </p>
        <p>If the button above does not work, copy and paste the following link into your browser:</p>
        <p style="color:#007bff;">%s</p>
        <hr>
        <p style="font-size:12px;color:gray;">
          If you did not create this account, you can safely ignore this email.
        </p>
      </body>
    </html>
    """.formatted(verificationLink, verificationLink);

        this.send(toEmail, subject, htmlContent);
    }
}
