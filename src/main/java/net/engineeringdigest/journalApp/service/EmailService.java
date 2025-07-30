package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)  // Make it optional to avoid startup errors
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) {
        if (javaMailSender == null) {
            log.warn("Email service not configured. Would send email to: {}, Subject: {}", to, subject);
            log.info("Email body: {}", body);
            return;
        }

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(to);
            mail.setSubject(subject);
            mail.setText(body);
            javaMailSender.send(mail);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Exception while sendEmail ", e);
        }
    }


}
