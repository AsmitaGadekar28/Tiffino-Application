package com.tiffino.tiffino.service;
/**
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.mail.SimpleMailMessage;
 import org.springframework.mail.javamail.JavaMailSender;
 import org.springframework.stereotype.Service;

 @Service
 public class EmailService {

 @Autowired(required = false)
 private JavaMailSender mailSender;

 // Email bhejne ka method
 public void sendEmail(String to, String subject, String body) {
 try {
 if (mailSender != null) {
 SimpleMailMessage message = new SimpleMailMessage();
 message.setTo(to);
 message.setSubject(subject);
 message.setText(body);
 mailSender.send(message);
 System.out.println("✅ Email sent successfully to: " + to);
 } else {
 // Agar SMTP configure nahi hai to console me print hoga
 System.out.println("📩 [Console Email Fallback]");
 System.out.println("To: " + to);
 System.out.println("Subject: " + subject);
 System.out.println("Body: \n" + body);
 }
 } catch (Exception e) {
 throw new RuntimeException("❌ Failed to send email: " + e.getMessage());
 }
 }
 }
 **/



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private EmailValidationService emailValidationService; //  New validation service added

    //  Email bhejne ka method
    public void sendEmail(String to, String subject, String body) {
        try {
            // Step 1️⃣ - Check if email is valid and deliverable
            boolean isValid = emailValidationService.isEmailDeliverable(to);

            if (!isValid) {
                System.out.println("❌ Invalid Or Not Deliverable email: " + to);
                return; // Stop execution if email is not valid
            }

            // Step 2️⃣ - Proceed to send mail
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                System.out.println("✅ Email sent successfully to: " + to);
            } else {
                // Fallback (SMTP not configured)
                System.out.println("📩 [Console Email Fallback]");
                System.out.println("To: " + to);
                System.out.println("Subject: " + subject);
                System.out.println("Body: \n" + body);
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to send email: " + e.getMessage());
        }
    }
}
