package com.kairos;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.config.LocalDateDeserializer;
import com.kairos.config.LocalDateSerializer;
import com.kairos.interceptor.ExtractOrganizationAndUnitInfoInterceptor;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepositoryImpl;

import com.kairos.util.userContext.UserContextInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofPattern;

@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy
@EnableCircuitBreaker
@EnableMongoRepositories(basePackages ={"com.kairos.persistence.repository"},
repositoryBaseClass = MongoBaseRepositoryImpl.class)
public class KairosActivityApplication implements WebMvcConfigurer {
	@Autowired
	private Environment environment;
	public static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd");

	static{
		java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
		System.setProperty("user.timezone", "UTC");
	}

	public static void main(String[] args) {
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


/*
	private static final String ALLOWED_HEADERS = "X-Requested-With,access-control-allow-origin,Authorization,authorization,Origin,Content-Type,Version";
	private static final String ALLOWED_METHODS = "GET,PUT,POST,DELETE,OPTIONS";
	private static final String ALLOWED_ORIGIN = "*";
	private static final String MAX_AGE = "3600";
	@Bean
	public WebFilter corsFilter() {
		return (ServerWebExchange ctx, WebFilterChain chain) -> {
			ServerHttpRequest request = ctx.getRequest();
			ServerHttpResponse response = ctx.getResponse();
			HttpHeaders headers = response.getHeaders();
			headers.set("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
			headers.set("Access-Control-Allow-Credentials", "true");
			headers.set("Access-Control-Allow-Methods", ALLOWED_METHODS);
			headers.set("Access-Control-Max-Age", MAX_AGE);
			headers.set("Access-Control-Allow-Headers",ALLOWED_HEADERS);
			if (request.getMethod() == HttpMethod.OPTIONS) {
				response.setStatusCode(HttpStatus.OK);
				return  Mono.empty();
			}
			return chain.filter(ctx);
		};
	}*/
}
