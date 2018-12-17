package com.kairos.config.env;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by Rama.Shankar on 27/9/16.
 */
@Configuration
@PropertySource({ "classpath:application-${spring.profiles.active}.properties" })
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

    public String getGoogleCalendarAPIV3Url(String vCardId){

        return googleCalendarAPIV3Url.replace("{countryVCard}" ,vCardId);
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

    public String getCarteServerHost(){
        return carteServerHost;
    }

    public void setFlsPackageName(String flsPackageName) {
        this.flsPackageName = flsPackageName;
    }

    public String getImagesPath() {
        return imagesPath;
    }

    public void setImagesPath(String imagesPath) {
        this.imagesPath = imagesPath;
    }

    public String getTomtomGeoCodeUrl() {
        return tomtomGeoCodeUrl;
    }

    public String getTomtomKey() {
        return tomtomKey;
    }

    public String getSendGridApiKey() {
        return sendGridApiKey;
    }


    public String getForgotPasswordApiLink() {
        return forgotPasswordApiLink;
    }

}
