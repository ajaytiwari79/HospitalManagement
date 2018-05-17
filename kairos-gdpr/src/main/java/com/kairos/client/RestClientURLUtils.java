package com.kairos.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestClientURLUtils {


    private static String userServiceBaseUrl;



    @Value("${gateway.kairos.user.url}")
    public static void setUserServiceBaseUrl(String userServiceBaseUrl) {
        RestClientURLUtils.userServiceBaseUrl = userServiceBaseUrl;
    }


    public final static String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {

            String baseUrl = new StringBuilder("http://localhost:8091/kairos/user/api/v1/organization/").append("24").toString();
return baseUrl;
        } else {
            String baseUrl = new StringBuilder(userServiceBaseUrl + "organization/").append("24").toString();
            return baseUrl;
        }
    }

    public static String getUserServiceBaseUrl() {
        return userServiceBaseUrl;
    }

}
