package com.kairos.util.userContext;

import com.kairos.config.env.EnvConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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

