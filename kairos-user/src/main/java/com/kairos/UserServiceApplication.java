package com.kairos;

import com.kairos.utils.userContext.UserContextInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Application Start Point
 */
@SpringBootApplication

@EnableTransactionManagement(proxyTargetClass=true)

@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableNeo4jRepositories(basePackages = {"com.kairos.persistence.repository"})
@EnableEurekaClient
@EnableCircuitBreaker
public class UserServiceApplication {

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

    @LoadBalanced
	@Primary
	@Bean
	public RestTemplate getCustomRestTemplate() {
		RestTemplate template = new RestTemplate();
		List interceptors = template.getInterceptors();
		if (interceptors == null) {
			template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
		} else {
			interceptors.add(new UserContextInterceptor());
			template.setInterceptors(interceptors);
		}

		return template;
	}
	}

