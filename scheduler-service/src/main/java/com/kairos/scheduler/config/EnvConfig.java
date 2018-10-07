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

    @Value("${gateway.userserviceauth.url}")
    private String userServiceUrlAuth;

    @Value("${user.loginapi.authUsername}")
    private String userServiceAuthUsername;

    @Value("${user.loginapi.authPassword}")
    private String userServiceAuthPassword;





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

    public String getUserServiceUrlAuth() {
        return userServiceUrlAuth;
    }

    public void setUserServiceUrlAuth(String userServiceUrlAuth) {
        this.userServiceUrlAuth = userServiceUrlAuth;
    }

    public String getUserServiceAuthUsername() {
        return userServiceAuthUsername;
    }

    public void setUserServiceAuthUsername(String userServiceAuthUsername) {
        this.userServiceAuthUsername = userServiceAuthUsername;
    }

    public String getUserServiceAuthPassword() {
        return userServiceAuthPassword;
    }

    public void setUserServiceAuthPassword(String userServiceAuthPassword) {
        this.userServiceAuthPassword = userServiceAuthPassword;
    }
}
