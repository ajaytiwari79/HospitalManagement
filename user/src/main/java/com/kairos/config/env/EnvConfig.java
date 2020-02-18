package com.kairos.config.env;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
@Configuration
@Getter
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
public class EnvConfig {

    @Value("${twillio.accountsid}")
    private String twillioAccountId;

    @Value("${twillio.authtoken}")
    private String twillioAuthToken;

    @Value("${twillio.number}")
    private String getTwillioNumber;

    @Value("${server.host.http.url}")
    private String serverHost;

    @Value("${carte.server.host.http.url}")
    private String carteServerHost;

    @Value("${webservice.wsurl}")
    private String wsUrl;

    @Value("${webservice.googleCalendarApiUrl}")
    private String googleCalendarAPIV3Url;

    @Value("${webservice.imagesPath}")
    private String imagesPath;

    @Value("${tomtom.geocode.url}")
    private String tomtomGeoCodeUrl;

    @Value("${tomtom.key}")
    private String tomtomKey;

    @Value("${forgot.password.link}")
    private String forgotPasswordApiLink;

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;
    @Value("${spring.profiles.active}")
    private String currentProfile;

    public String getGoogleCalendarAPIV3Url(String vCardId){
        return googleCalendarAPIV3Url.replace("{countryVCard}" ,vCardId);
    }

}
