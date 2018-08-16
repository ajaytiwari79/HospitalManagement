package com.kairos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.config.LocalDateDeserializer;
import com.kairos.config.LocalDateSerializer;
import com.kairos.persistence.repository.custom_repository.Neo4jBaseRepositoryImpl;
import com.kairos.util.userContext.SchedulerUserContextInterceptor;
import com.kairos.util.userContext.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.*;
import org.springframework.data.neo4j.annotation.EnableNeo4jAuditing;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Application Start Point
 */
@SpringBootApplication
@EnableEurekaClient
@EnableTransactionManagement(proxyTargetClass=true)
@EnableResourceServer
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNeo4jRepositories(basePackages = {"com.kairos.persistence.repository"},repositoryBaseClass = Neo4jBaseRepositoryImpl.class)
@EnableCircuitBreaker
@EnableKafka
public class UserServiceApplication extends WebMvcConfigurerAdapter{

	public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");
	public static final Logger logger = LoggerFactory.getLogger(UserServiceApplication.class);


	static{
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");
	}


	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	@Bean
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
	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		mappingJackson2HttpMessageConverter.setObjectMapper(serializingObjectMapper());
		mappingJackson2HttpMessageConverter.setPrettyPrint(true);
		return mappingJackson2HttpMessageConverter;
	}
	@Bean
	public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter() {
		MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter = new MappingJackson2XmlHttpMessageConverter();
		mappingJackson2XmlHttpMessageConverter.setPrettyPrint(true);
		return mappingJackson2XmlHttpMessageConverter;
	}
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		super.configureMessageConverters(converters);
		converters.add(mappingJackson2HttpMessageConverter());
		converters.add(mappingJackson2XmlHttpMessageConverter());

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
	@Profile({"development","qa","production"})
	@LoadBalanced
	@Bean(name ="schedulerRestTemplate")
	public RestTemplate getCustomRestTemplateWithoutAuthorization(RestTemplateBuilder restTemplateBuilder) {
		RestTemplate template =restTemplateBuilder
				.messageConverters(mappingJackson2HttpMessageConverter())
				.build();
		return template;
	}
    @Profile({"local", "test"})
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
        return template;
    }
    @Profile({"local", "test"})
    @Bean(name ="schedulerRestTemplate")
    public RestTemplate getCustomRestTemplateWithoutAuthorizationLocal(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
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

	@Profile({"development","qa","production"})
	@LoadBalanced
	@Bean(name="schedulerServiceRestTemplate")
	public RestTemplate getRestTemplateWithoutUserContext(RestTemplateBuilder restTemplateBuilder,  @Value("${scheduler.authorization}") String authorization) {

		RestTemplate template =restTemplateBuilder
				.interceptors(new SchedulerUserContextInterceptor(authorization))
				.messageConverters(mappingJackson2HttpMessageConverter())
				.build();
		return template;
	}
}

