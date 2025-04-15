package com.leverx.RatingSystemRest.Business.Interfaces;

public interface EmailService {

    void sendConfirmationEmail(String to, String Token);

    void sendRecoverLink(String email, String Token);


}
