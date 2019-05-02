package com.planner.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.planner.appConfig.UserContextInterceptor;
import com.planner.repository.common.MongoBaseRepositoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.planner")
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNeo4jRepositories(basePackages ={"com.planner.repository"})
@EnableMongoRepositories(basePackages ={"com.planner.repository"},repositoryBaseClass = MongoBaseRepositoryImpl.class)
public class PlanningAppConfig {

    private static final Logger logger = LoggerFactory.getLogger(PlanningAppConfig.class);

    public static void main(String[] args) {
        //ch.qos.logback.classic.turbo.TurboFilter tf=null;
        //ClassPathResource cps= new ClassPathResource("droolsFile");

        //logger.info("drool files path "+new File(AppConstants.DROOL_FILES_PATH).exists());

        SpringApplication.run(PlanningAppConfig.class, args);
    }

    static{
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
    }

    @Profile({"development","qa","production"})
    @LoadBalanced
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

    @Profile({"local"})
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

    @Bean("objectMapperJackson")
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(javaTimeModule);
        return objectMapper;
    }
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setObjectMapper(serializingObjectMapper());
        mappingJackson2HttpMessageConverter.setPrettyPrint(true);
        return mappingJackson2HttpMessageConverter;
    }
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageBundle = new ReloadableResourceBundleMessageSource();
        messageBundle.setBasename("classpath:messages/messages");
        messageBundle.setDefaultEncoding("UTF-8");
        return messageBundle;


    }
}
