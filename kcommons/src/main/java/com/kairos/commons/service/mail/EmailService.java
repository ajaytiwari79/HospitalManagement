package com.kairos.commons.service.mail;

public interface EmailService {

    void sendMail(String from,String subject,String htmlBody,String textBody,String... to);

}
