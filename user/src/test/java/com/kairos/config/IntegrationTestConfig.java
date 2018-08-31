package com.kairos.config;

import com.kairos.util.user_context.UserContext;
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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Configuration
public class IntegrationTestConfig {
    @Value("${spring.test.authorization}")
    private String authorization ;

    @Profile({"test","local"})
    @Bean
    @Primary
    public TestRestTemplate getTestRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        RestTemplate template =restTemplateBuilder
                .interceptors(new TestUserContextInterceptor())
                .build();
        TestRestTemplate restTemplate = new TestRestTemplate(template);

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
