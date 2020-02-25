package com.kairos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.kairos.commons.config.mongo.EnableAuditLogging;
import com.kairos.custom_exception.ActivityExceptionHandler;
import com.kairos.dto.user_context.UserContextInterceptor;
import com.kairos.interceptor.ExtractOrganizationAndUnitInfoInterceptor;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.kairos.commons.utils.ObjectMapperUtils.LOCALDATE_FORMATTER;
import static java.time.format.DateTimeFormatter.ofPattern;

@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy
@EnableCircuitBreaker
@EnableMongoRepositories(basePackages ={"com.kairos.persistence.repository"},
repositoryBaseClass = MongoBaseRepositoryImpl.class)
@EnableAuditLogging
@EnableAsync
public class KairosActivityApplication implements WebMvcConfigurer {
	@Autowired
	private Environment environment;
	public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

	static{
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ActivityExceptionHandler());
		SpringApplication.run(KairosActivityApplication.class, args);
	}

	/**
	 * Set locale language to resolve text Messages
	 * @return
	 */
	@Bean(name = "messageSource")
	public ReloadableResourceBundleMessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageBundle = new ReloadableResourceBundleMessageSource();
		messageBundle.setBasename("classpath:messages/messages");
		messageBundle.setDefaultEncoding("UTF-8");
		return messageBundle;


	}

	@Bean("objectMapperJackson")
	@Primary
	public ObjectMapper serializingObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(LOCALDATE_FORMATTER));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(LOCALDATE_FORMATTER));
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(javaTimeModule);
		return mapper;
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
		converters.add(mappingJackson2HttpMessageConverter());
		converters.add(mappingJackson2XmlHttpMessageConverter());

	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ExtractOrganizationAndUnitInfoInterceptor());
	}

	@Profile({"development","qa","production"})
	@LoadBalanced
	@Primary
	@Bean
	public RestTemplate getCustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.interceptors(new UserContextInterceptor()).messageConverters(mappingJackson2HttpMessageConverter()).build();
	}

	@Profile({"development","qa","production"})
	@LoadBalanced
	@Bean(name ="restTemplateWithoutAuth")
	public RestTemplate getCustomRestTemplateWithoutAuthorization(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
				.messageConverters(mappingJackson2HttpMessageConverter())
				.build();
	}

    @Profile({"local", "test"})
    @Primary
    @Bean
    public RestTemplate getCustomRestTemplateLocal(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
                .interceptors(new UserContextInterceptor())
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
    }

	@Profile({"local", "test"})
    @Bean(name ="restTemplateWithoutAuth")
    public RestTemplate getCustomRestTemplateWithoutAuthorizationLocal(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder
                .messageConverters(mappingJackson2HttpMessageConverter())
                .build();
    }

}
