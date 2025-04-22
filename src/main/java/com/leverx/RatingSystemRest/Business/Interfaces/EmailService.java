package com.leverx.RatingSystemRest.Business.Interfaces;

/**
 * Contract for Email Service.
 */

@SuppressWarnings("checkstyle:Indentation")
public interface EmailService {
    /**
     * Sends registration confirmation email to user.
     *
     * @param to email recipient.
     * @param token confirmation token.
     */
    void sendConfirmationEmail(String to, String token);

    /**
     * Sends password recovery link to user.
     *
     * @param email recipient email address.
     * @param token recover token.
     */
    void sendRecoverLink(String email, String token);


}
