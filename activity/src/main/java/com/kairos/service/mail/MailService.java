package com.kairos.service.mail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;


/**
 * Created by oodles on 11/11/16.
 */

@Service
public class MailService {
    final static Logger logger = LoggerFactory.getLogger(MailService.class);
    final static boolean isSSL = true;

    @Inject
    JavaMailSender javaMailSender;

    @Inject
    private TemplateEngine templateEngine;




    public boolean sendPlainMail(String receiver,String body, String subject) {
        try {
            logger.info("Sending email to::" + receiver);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setFrom("info@nordicplanning.dk");
            helper.setTo(receiver);
            helper.addCc("kairos-backend@oodlestechnologies.com");
            helper.setSubject(subject);
            helper.setText(body);
            javaMailSender.send(mimeMessage);
            logger.info("Email sent");
        } catch (MessagingException e) {
            logger.info("exception occured {}",e);
            return false;
        }
        catch (Exception e){
            logger.info("exception occured {}",e);
            return false;
        }
        return false;
    }

    public boolean sendMailWithAttachment(String[] recipients, String message, String subject, File filePath) {
        DataSource source = new FileDataSource(filePath);
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();

            StringBuilder sb = getRecipientsFromArray(recipients);
            String recipientsString = sb.toString();
            logger.info("List: "+recipientsString);
            //InternetAddress me = new InternetAddress("info@nordicplanning.dk");
            mail.setFrom("info@nordicplanning.dk");
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


            } catch (MessagingException e) {
            logger.info("exception occured {}",e);
            return false;
            }

            catch (Exception e) {
             logger.info("exception occured {}",e);
             return false;
            }

    }

    private StringBuilder getRecipientsFromArray(String[] recipients) {
        StringBuilder sb = new StringBuilder();
        for (String n : recipients) {
            if (sb.length() > 0) sb.append(',');
            sb.append(n);
        }
        return sb;
    }


    public boolean sendPlainMail(String receiver,String message, String subject, String[] filePath){
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        DataSource fileDataSource;
        BodyPart part;
        for (String path:filePath) {
            part = new MimeBodyPart();
            fileDataSource= new FileDataSource(new File(path));
            try {
                part.setDataHandler(new DataHandler(fileDataSource));
                part.setFileName(fileDataSource.getName());
                multipart.addBodyPart(part);
            } catch (MessagingException e) {
                e.printStackTrace();
            }

        }

        try {
            Message mail = javaMailSender.createMimeMessage();
            mail.setRecipients(Message.RecipientType.TO,InternetAddress.parse(receiver));

            mail.setSubject(subject);
            mail.setText(message);
               mail.setContent(multipart);
            Transport.send(mail);
            logger.info("Message send successfully....");
            return true;


        } catch (MessagingException e) {
            logger.info("exception occured {}",e);
            return false;
        }

        catch (Exception e) {
            logger.info("exception occured {}",e);
            return false;
        }

    }

    /**
     * send email using template {thymleaf}
     * @param ctx
     * @param templateName
     * @param emailTo
     * @param subj
     * @throws MessagingException
     */
    public void sendEmail(final Context ctx, final String templateName, final String emailTo,
                          final String subj) throws MessagingException{
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setTo(emailTo);
        message.setSubject(subj);
        final String htmlContent = templateEngine.process(templateName, ctx);
        message.setText(htmlContent, true);
        javaMailSender.send(mimeMessage);
    }
}
