package com.kairos.commons.service.mail;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.sendgrid.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.*;


//import javax.validation.constraints.Email;


/**
 * Created by oodles on 11/11/16.
 */

@Service
public class MailService {
    final static Logger logger = LoggerFactory.getLogger(MailService.class);

    @Inject
    private JavaMailSender javaMailSender;
    @Inject
    private TemplateEngine templateEngine;
    @Inject private EnvConfigCommon envConfigCommon;

    /***
     *
     * @param templateName can be null if User want to send mail without any template
     * @param templateParam can be null if User want to send mail without any template or in case of template doesn't have any Context param
     * @param body can be null if mail send with template
     * @param subject should not be null or blank
     * @param receiver can't be null if null this method throw InvalidRequestException
     */
    public void sendMailWithSendGrid(String templateName,Map<String,Object> templateParam,String body, String subject, String... receiver) {
        Mail mail = getMail(templateName, templateParam, body, subject, receiver);
        SendGrid sendGrid = new SendGrid(SEND_GRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint(MAIL_REQUEST_ENDPOINT);
            request.setBody(mail.build());
            Response response = sendGrid.api(request);
            logger.info("Email sent to {}", receiver.toString());
            logger.info("Mail response {}", response.getBody());
        } catch (IOException ex) {
            logger.error("exception occured {}", ex);
        }

    }


    //TODO we need to refactor this method as per the requirement
    public boolean sendMailWithAttachment(String[] recipients, String message, String subject, File filePath) {
        DataSource source = new FileDataSource(filePath);
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();

            StringBuilder sb = getRecipientsFromArray(recipients);
            String recipientsString = sb.toString();
            logger.info("List: "+recipientsString);
            //InternetAddress me = new InternetAddress("info@kairosplanning.com");
            mail.setFrom("info@kairosplanning.com");
            //mail.setFrom(me);
            mail.setRecipients(Message.RecipientType.TO,InternetAddress.parse(recipientsString));
            mail.setSubject(subject);
            mail.setText(message);
            bodyPart.setFileName(source.getName());
            logger.info("File has dataType: "+source.getContentType());
            bodyPart.setDataHandler(new DataHandler(source));
            multipart.addBodyPart(bodyPart);
            mail.setContent(multipart);

            javaMailSender.send(mail);

            logger.info("Message send successfully....");
            return true;


            } catch (Exception e) {
             logger.info("exception occured {}",e);
             return false;
            }

    }

    private Mail getMail(String templateName, Map<String,Object> templateParam, String body, String subject,String... receivers){
        if(StringUtils.isBlank(subject)){
            throw new InvalidRequestException("Subject should not be blank");
        }
        Personalization personalization = new Personalization();
        for (String receiver : receivers) {
            if(StringUtils.isBlank(receiver) || StringUtils.containsWhitespace(receiver)){
                logger.info("Receiver is {}",receiver);
                throw new InvalidRequestException("Receiver E-mail id is not correct");
            }
            personalization.addTo(new Email(receiver));
        }
        Email from=new Email(NO_REPLY_EMAIL);
        Content content= getContent(templateName,templateParam,body);
        Mail mail = new Mail();
        mail.setSubject(subject);
        mail.setFrom(from);
        mail.addContent(content);
        mail.addPersonalization(personalization);
        return mail;
    }

    private Content getContent(String templateName,Map<String,Object> templateParam,String body){
        Content content = null;
        if(StringUtils.isNotBlank(templateName)){
            final Context context = getContext(templateParam);
            body = templateEngine.process(templateName, context);
            content = new Content(HTML_CONTENT_TYPE,body);
        }else {
            content = new Content(PLAIN_CONTENT_TYPE,body);
        }
        return content;
    }

    private Context getContext(Map<String,Object> templateParam){
        Context context = new Context(Locale.ENGLISH);
        if(isMapNotEmpty(templateParam)){
            context.setVariables(templateParam);
            context.setVariable("kairosLogo","http://dev.kairosplanning.com" + FORWARD_SLASH + envConfigCommon.getImagesPath()+KAIROS_LOGO);
            if(!templateParam.containsKey("receiverImage")){
                context.setVariable("receiverImage","http://dev.kairosplanning.com" + FORWARD_SLASH + envConfigCommon.getImagesPath()+USER_DEFAULT_IMAGE);
            }
        }
        return context;
    }

    private StringBuilder getRecipientsFromArray(String[] recipients) {
        StringBuilder sb = new StringBuilder();
        for (String n : recipients) {
            if (sb.length() > 0) sb.append(',');
            sb.append(n);
        }
        return sb;
    }

}
