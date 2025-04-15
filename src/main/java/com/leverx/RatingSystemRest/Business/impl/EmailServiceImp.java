package com.leverx.RatingSystemRest.Business.impl;

import com.leverx.RatingSystemRest.Business.Interfaces.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
//TODO - EACH SERVICE EACH INTERFACE
//ALSO NAME  ENDS WITH IMPL
//TODO UNECESSARY IMPORTS
public class EmailServiceImp implements EmailService {


    private final JavaMailSender mailSender;


    @Value("${frontend.path}")
    private String frontendPath;

    public EmailServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmationEmail(String to, String Token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("LeverX Rating System Confirmation Email");
            helper.setText("Confirmation Code " + Token, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public void sendRecoverLink(String email, String Token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("LeverX Recover Link");
            helper.setText(frontendPath + "reset" + "?token=" + Token);
            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
