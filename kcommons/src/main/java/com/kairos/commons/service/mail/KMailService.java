package com.kairos.commons.service.mail;


import com.amazonaws.services.simpleemail.model.Content;
import com.kairos.commons.config.EnvConfigCommon;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isMapNotEmpty;
import static com.kairos.constants.CommonConstants.*;

public class KMailService {

    private EmailService emailService;
    private EnvConfigCommon envConfigCommon;
    private TemplateEngine templateEngine;

    public KMailService(EmailService emailService,EnvConfigCommon envConfigCommon,TemplateEngine templateEngine){
        this.emailService = emailService;
        this.envConfigCommon = envConfigCommon;
        this.templateEngine = templateEngine;
    }

    public void sendMail(final String from,final String to,final String subject,final String textBody,final String htmlBody,final Map<String,Object> templateParam,String templatePath){
        String htmlContent = getContent(templatePath,templateParam);
        if(templateParam==null){
            emailService.sendMail(from,to,subject,textBody,htmlBody);
        }else {
            emailService.sendMail(from, to, subject, textBody, htmlContent);
        }
    }



    public String getContent(String templateName, Map<String,Object> templateParam){
      String body = "";
        if(StringUtils.isNotBlank(templateName)){
            final Context context = getContext(templateParam,envConfigCommon);
            body = templateEngine.process(templateName, context);
        }
        return body;
    }

    private Context getContext(Map<String,Object> templateParam, EnvConfigCommon envConfigCommon){
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

}
