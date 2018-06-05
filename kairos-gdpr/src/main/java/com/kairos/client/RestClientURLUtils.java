package com.kairos.client;


import com.kairos.utils.userContext.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestClientURLUtils {


    private static String userServiceBaseUrl;

    @Value("${gateway.kairos.user.url}")
    public  void setUserServiceBaseUrl(String userServiceBaseUrl) {
        RestClientURLUtils.userServiceBaseUrl = userServiceBaseUrl;
    }


    public final static String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {

            String baseUrl = new StringBuilder(userServiceBaseUrl+"/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;

        } else {
            String baseUrl = new StringBuilder(userServiceBaseUrl + "/organization/").toString();
            return baseUrl;
        }
    }

    public  static String getUserServiceBaseUrl() {
        return userServiceBaseUrl;
    }

}
