package com.kairos;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.config.LocalDateDeserializer;
import com.kairos.config.LocalDateSerializer;
import com.kairos.utils.userContext.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@EnableEurekaClient
@EnableAspectJAutoProxy
@EnableCircuitBreaker
@SpringBootApplication
public class KairosGdprApplication {

    public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

    public static void main(String args[]) {
        SpringApplication.run(KairosGdprApplication.class, args);

    }

    @Bean("objectMapperJackson")
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }

    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageBundle = new ReloadableResourceBundleMessageSource();
        messageBundle.setBasename("classpath:messages/messages");
        messageBundle.setDefaultEncoding("UTF-8");
        return messageBundle;


    }


    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(serializingObjectMapper());
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        return mappingJackson2HttpMessageConverter;
    }

    @Profile({"development","qa","production"})
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();

    }


    @Profile("local")
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template = restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }





}
