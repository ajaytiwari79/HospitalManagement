package com.kairos.scheduler.config.app;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.planner.appConfig.UserContextInterceptor;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.kairos.scheduler.utils.user_context.SchedulerUserContextInterceptor;
import com.kairos.scheduler.utils.user_context.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.kairos.scheduler")
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableMongoRepositories(basePackages ={"com.kairos.scheduler.persistence.repository"})
public class SchedulerAppConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerAppConfig.class);

    public static void main(String[] args) {
        //ch.qos.logback.classic.turbo.TurboFilter tf=null;
        //ClassPathResource cps= new ClassPathResource("droolsFile");

        //logger.info("drool files path "+new File(AppConstants.DROOL_FILES_PATH).exists());

        SpringApplication.run(SchedulerAppConfig.class, args);
    }

    static{
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
    }

/*
    @Profile({"!local"})
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
*/

    @Bean("objectMapper")
    @Primary
    public ObjectMapper serializingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer());
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer());
       // javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));

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

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(){
        ThreadPoolTaskScheduler threadPoolTaskScheduler
                = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.setThreadNamePrefix(
                "ThreadPoolTaskScheduler");
        return threadPoolTaskScheduler;
    }

    @Profile({"development","qa","production"})
    @LoadBalanced
    @Primary
    @Bean(name="restTemplate")
    public RestTemplate getCustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

    @Profile({"local", "test"})
    @Primary
    @Bean(name="restTemplate")
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

    @Profile({"local", "test"})
    @Bean(name="schedulerServiceRestTemplate")
    public RestTemplate getRestTemplateWithoutUserContextLocal(RestTemplateBuilder restTemplateBuilder,  @Value("${scheduler.authorization}") String authorization) {

        RestTemplate template =restTemplateBuilder
                .interceptors(new SchedulerUserContextInterceptor(authorization))
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

   /* @Profile({"development","qa","production"})
    @LoadBalanced
    @Bean(name="schedulerServiceRestTemplate")
    public RestTemplate getRestTemplateWithoutUserContext(RestTemplateBuilder restTemplateBuilder,  @Value("${scheduler.authorization}") String authorization) {

        RestTemplate template =restTemplateBuilder
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }*/

    @Profile({"development","qa","production"})
    @LoadBalanced
    @Primary
    @Bean(name="restTemplateWithoutAuth")
    public RestTemplate getCustomRestTemplateWithoutAuthorization(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

    @Profile({"local", "test"})
    @Primary
    @Bean(name="restTemplateWithoutAuth")
    public RestTemplate getCustomRestTemplateWithoutAuthorizationLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }

}
