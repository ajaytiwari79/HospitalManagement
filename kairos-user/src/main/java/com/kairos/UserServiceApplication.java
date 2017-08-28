package com.kairos;

import com.kairos.util.userContext.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * Application Start Point
 */
@SpringBootApplication

@EnableTransactionManagement(proxyTargetClass=true)
@EnableResourceServer
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNeo4jRepositories(basePackages = {"com.kairos.persistence.repository"})
@EnableEurekaClient
@EnableCircuitBreaker
public class UserServiceApplication extends WebMvcConfigurerAdapter{

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

		/**
		 * Allows CORS support
		 * @return
		 */
		@Bean
		public WebMvcConfigurer corsConfigurer() {
			return new WebMvcConfigurerAdapter() {
				@Override
				public void addCorsMappings(CorsRegistry registry) {
					registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").allowCredentials(true).allowedHeaders("Keep-Alive","Connection","Transfer-Encoding");
				}
			};
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


	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
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
	}

