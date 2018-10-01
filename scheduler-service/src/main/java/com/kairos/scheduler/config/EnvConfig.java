package com.kairos.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by anil on 27/7/17.
 */
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Configuration
public class EnvConfig {

    @Value("${gateway.userservice.url}")
    private String userServiceUrl;
    @Value("${user.loginapi.authToken}")
    private String userLoginApiAuthToken;

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public String getUserLoginApiAuthToken() {
        return userLoginApiAuthToken;
    }

    public void setUserLoginApiAuthToken(String userLoginApiAuthToken) {
        this.userLoginApiAuthToken = userLoginApiAuthToken;
    }
}
