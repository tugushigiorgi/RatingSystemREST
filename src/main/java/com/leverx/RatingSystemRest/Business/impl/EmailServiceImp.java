package com.leverx.RatingSystemRest.Business.impl;

import static com.leverx.RatingSystemRest.Business.ConstMessages.EmailConstMessages.CONFIRMATION_EMAIL_SUBJECT;
import static com.leverx.RatingSystemRest.Business.ConstMessages.EmailConstMessages.RECOVERY_EMAIL_SUBJECT;

import com.leverx.RatingSystemRest.Business.Interfaces.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


/**
 * Service which implement EmailService interface.
 */
@Service

public class EmailServiceImp implements EmailService {

  private final JavaMailSender mailSender;

  @Value("${frontend.path}")
  private String frontendPath;

  /**
   * Constructor.
   *
   * @param mailSender java Mail Api to work with mails.
   */
  public EmailServiceImp(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  /**
   * Send confirmation mail.
   *
   * @param to    recipient of mail
   * @param token token to send with mail
   */
  public void sendConfirmationEmail(String to, String token) {
    try {
      var message = mailSender.createMimeMessage();
      var helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(CONFIRMATION_EMAIL_SUBJECT);
      helper.setText("Confirmation Code " + token, true);

      mailSender.send(message);

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  /**
   * Send Recover link.
   *
   * @param email recipient of mail
   * @param token token to send
   */
  public void sendRecoverLink(String email, String token) {
    try {
      var message = mailSender.createMimeMessage();
      var helper = new MimeMessageHelper(message, true);
      helper.setTo(email);
      helper.setSubject(RECOVERY_EMAIL_SUBJECT);
      helper.setText(frontendPath + "reset" + "?token=" + token);
      mailSender.send(message);

    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }
}
