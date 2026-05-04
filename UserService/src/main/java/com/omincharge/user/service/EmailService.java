package com.omincharge.user.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("OmniCharge — Your OTP Code");
        message.setText(
            "Hello!\n\n" +
            "Your OTP for OmniCharge registration is:\n\n" +
            "        " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n" +
            "Do not share this OTP with anyone.\n\n" +
            "Team OmniCharge"
        );
        mailSender.send(message);
    }

    // --- NEW METHOD ADDED HERE ---
    public void sendWelcomeEmail(String toEmail, String firstName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Welcome to OmniCharge!");
        message.setText(
            "Hello " + firstName + ",\n\n" +
            "Congratulations! Your registration with OmniCharge was successful.\n" +
            "We are thrilled to have you on board.\n\n" +
            "Best regards,\n" +
            "Team OmniCharge"
        );
        mailSender.send(message);
    }
}