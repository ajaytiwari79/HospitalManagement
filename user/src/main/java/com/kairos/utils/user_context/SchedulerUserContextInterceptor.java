package com.kairos.utils.user_context;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;


public class SchedulerUserContextInterceptor implements ClientHttpRequestInterceptor {

    private String authorization;
   public SchedulerUserContextInterceptor(String authorization) {
       this.authorization = authorization;
   }

    public SchedulerUserContextInterceptor() {

    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();
        headers.add(UserContext.AUTH_TOKEN,authorization);


        return execution.execute(request, body);
    }


}

