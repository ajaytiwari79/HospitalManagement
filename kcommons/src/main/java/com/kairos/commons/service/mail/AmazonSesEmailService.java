package com.kairos.commons.service.mail;


import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

import javax.validation.constraints.Null;

public class AmazonSesEmailService implements EmailService {

    private final String fromAddress = "noreply@dev.kairosplanning.com";
    private AmazonSimpleEmailService client ;

    public AmazonSesEmailService(final String region){
            client = AmazonSimpleEmailServiceClientBuilder.standard()
                    // Replace US_WEST_2 with the AWS Region you're using for
                    // Amazon SES.
                    .withRegion(region)
                    .build();
    }


    @Override
    public void sendMail(@Null final String from, final String to, final String subject, final String htmlBody, final String textBody) {
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(to))
                .withMessage(new Message()
                        .withBody(new Body()
                                .withHtml(new Content()
                                        .withCharset("UTF-8").withData(htmlBody))
                                .withText(new Content()
                                        .withCharset("UTF-8").withData(textBody)))
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)))
                .withSource(from!=null ? from:fromAddress);
                // Comment or remove the next line if you are not using a
                // configuration set
//                .withConfigurationSetName(CONFIGSET);
        client.sendEmail(request);
    }
}
