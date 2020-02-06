package com.kairos.commons.service.mail;

public interface EmailService {

    void sendMail(String from,String to,String subject,String htmlBody,String textBody);

}
