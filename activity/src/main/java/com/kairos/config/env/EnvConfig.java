package com.kairos.config.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by anil on 27/7/17.
 */
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
@Configuration
public class EnvConfig {
    @Value("${fls.package.name}")
    private String flsPackageName;

    @Value("${fls.username.password}")
    private String flsUsernamePassword;

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

    public String getGoogleCalendarAPIV3Url(String vCardId) {

        return googleCalendarAPIV3Url.replace("{countryVCard}", vCardId);
    }


    public String getWsUrl() {
        return wsUrl;
    }


    public String getFlsPackageName() {
        return flsPackageName;
    }

    public String getFlsUsernamePassword() {
        return flsUsernamePassword;
    }


    public String getTwillioAccountId() {
        return twillioAccountId;
    }

    public String getTwillioAuthToken() {
        return twillioAuthToken;
    }

    public String getGetTwillioNumber() {
        return getTwillioNumber;
    }

    public String getServerHost() {
        return serverHost;
    }

    public String getCarteServerHost() {
        return carteServerHost;
    }

    public void setFlsPackageName(String flsPackageName) {
        this.flsPackageName = flsPackageName;
    }
}
