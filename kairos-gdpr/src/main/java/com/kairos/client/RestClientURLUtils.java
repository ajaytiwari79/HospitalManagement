package com.kairos.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestClientURLUtils {


    private static String userServiceBaseUrl;


<<<<<<< HEAD
    @Value("${gateway.kairos.filter.url}")
=======
    @Value("${gateway.kairos.user.url}")
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91
    public static void setUserServiceBaseUrl(String userServiceBaseUrl) {
        RestClientURLUtils.userServiceBaseUrl = userServiceBaseUrl;
    }


    public final static String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
<<<<<<< HEAD
            String baseUrl = new StringBuilder("http://localhost:8091/kairos/filter/api/v1/organization/").append("24").toString();
=======
            String baseUrl = new StringBuilder("http://localhost:8091/kairos/user/api/v1/organization/").append("24").toString();
>>>>>>> d97f2521641985b2ef7974bbd2871f4291a34c91
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
