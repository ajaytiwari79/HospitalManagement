package com.kairos.service.fls_visitour.webservice_config;

import com.kairos.config.env.EnvConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.inject.Inject;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
@Configuration
public class FlsConfig {

    @Inject
    EnvConfig config;

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(config.getFlsPackageName());
        return marshaller;
    }


   /* @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate client = new WebServiceTemplate();
        //ClientInterceptor [] logging = {new WebServiceLogging()};
       // client.setInterceptors(logging); // To log request and response
        client.setDefaultUri(config.getFlsDefaultUrl());
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        client.setMessageSender(new WebServiceMessageSenderWithAuth(config));
        return client;
    }*/
}
