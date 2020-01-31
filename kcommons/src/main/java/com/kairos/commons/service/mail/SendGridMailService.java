package com.kairos.commons.service.mail;

import com.kairos.commons.config.EnvConfigCommon;
import com.kairos.commons.custom_exception.InvalidRequestException;
import com.sendgrid.*;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
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
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isMapNotEmpty;
import static com.kairos.constants.CommonConstants.*;


//import javax.validation.constraints.Email;


/**
 * Created by oodles on 11/11/16.
 */
//TODO this should implement EmailService interface as there could be multiple email providers
@Service
public class SendGridMailService implements EmailService{
    private static final Logger LOGGER = LoggerFactory.getLogger(SendGridMailService.class);

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
            LOGGER.info("Mail response {}", response.getBody());
        } catch (IOException ex) {
            LOGGER.error("exception occured {}", ex);
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
            LOGGER.info("List: {}",recipientsString);
            mail.setFrom("info@kairosplanning.com");
            mail.setRecipients(Message.RecipientType.TO,InternetAddress.parse(recipientsString));
            mail.setSubject(subject);
            mail.setText(message);
            bodyPart.setFileName(source.getName());
            LOGGER.info("File has dataType: {}",source.getContentType());
            bodyPart.setDataHandler(new DataHandler(source));
            multipart.addBodyPart(bodyPart);
            mail.setContent(multipart);

            javaMailSender.send(mail);

            LOGGER.info("Message send successfully....");
            return true;


            } catch (Exception e) {
             LOGGER.info("exception occured {}",e);
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
                LOGGER.info("Receiver is {}",receiver);
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
        Content content = new Content(PLAIN_CONTENT_TYPE,body);
        if(StringUtils.isNotBlank(templateName)){
            final Context context = getContext(templateParam);
            body = templateEngine.process(templateName, context);
            content = new Content(HTML_CONTENT_TYPE,body);
        }
        return content;
    }

    private Context getContext(Map<String,Object> templateParam){
        Context context = new Context(Locale.ENGLISH);
        if(isMapNotEmpty(templateParam)){
            context.setVariables(templateParam);
            context.setVariable("kairosLogo",envConfigCommon.getServerHost() + FORWARD_SLASH + envConfigCommon.getImagesPath()+KAIROS_LOGO);
            if(!templateParam.containsKey("receiverImage")){
                context.setVariable("receiverImage",envConfigCommon.getServerHost() + FORWARD_SLASH + envConfigCommon.getImagesPath()+USER_DEFAULT_IMAGE);
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


    //Todo Please don't use this method for sending any Custom exception
    public void sendMailToBackendOnException(Exception ex){
         if(envConfigCommon.getCurrentProfile().equals(PRODUCTION_PROFILE)){
            StringBuffer body = new StringBuffer(ex.getMessage());
            for (StackTraceElement stackTraceElement : ex.getStackTrace()) {
                //if(stackTraceElement.getClassName().contains(PACKAGE_NAME)) {
                    body.append(stackTraceElement.toString()).append(" ").append(System.getProperty("line.separator")).append(" ");
                //}
            }
            sendMailWithSendGrid(null,null,body.toString(),"Exception in "+envConfigCommon.getApplicationName()+" | "+envConfigCommon.getCurrentProfile(),KAIROS_BACKEND_MAIL_IDS);
        }
    }

    @Override
    public void sendMail(String from, String to, String subject, String htmlBody, String textBody) {
      throw new NotImplementedException("This has not been implemented yet ");
    }
}
