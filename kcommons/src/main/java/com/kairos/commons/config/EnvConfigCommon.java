package com.kairos.commons.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by anil on 27/7/17.
 */
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Configuration
@Getter
@Setter
public class EnvConfigCommon {

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

    @Value("${server.host.http.url}")
    private String serverHost;

    @Value("${webservice.imagesPath}")
    private String imagesPath;


    @Value("${kpermissions.data.publish}")
    private String kpermissionDataPublish;

    @Value("${kpermissions.model.package.path}")
    private String modelPackagePath;

    @Value("${spring.profiles.active}")
    private  String currentProfile;

    @Value("${spring.application.name}")
    private String applicationName;

}
