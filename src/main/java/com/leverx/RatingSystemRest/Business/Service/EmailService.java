package com.leverx.RatingSystemRest.Business.Service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EmailService {


    private JavaMailSender mailSender;

    public void sendConfirmationEmail(String to,String Token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("LeverX Rating System Confirmation Email");
            helper.setText("Confirmation Code :"+Token, true);
            helper.setText("Expires at : "+ LocalDateTime.now().plusHours(24), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
