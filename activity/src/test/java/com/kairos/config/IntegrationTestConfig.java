package com.kairos.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kairos.util.userContext.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.time.LocalDate;

@Configuration
public class IntegrationTestConfig {
    @Value("${spring.test.authorization}")
    private String authorization ;

    @Bean
    @Primary
    public TestRestTemplate getTestRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        restTemplateBuilder = restTemplateBuilder
        .interceptors(new TestUserContextInterceptor());
        TestRestTemplate restTemplate = new TestRestTemplate(restTemplateBuilder);
        return restTemplate;
    }

    class TestUserContextInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {

            HttpHeaders headers = request.getHeaders();
            headers.add(UserContext.AUTH_TOKEN,authorization);
            return execution.execute(request, body);
        }
    }
}
