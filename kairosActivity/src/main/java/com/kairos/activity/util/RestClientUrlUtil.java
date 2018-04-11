package com.kairos.activity.util;

import com.kairos.activity.util.userContext.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by vipul on 19/9/17.
 */
@Component
public class RestClientUrlUtil {

    private static  String userServiceUrl;

        @Value("${gateway.userservice.url}")
    public  void setUserServiceUrl(String userServiceUrl) {
        RestClientUrlUtil.userServiceUrl = userServiceUrl;
    }


    public static final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
    public static final String getBaseUrl() {
        return userServiceUrl;


    }


    public static final String getDefaultSchedulerUrl(){

        String baseUrl=new StringBuilder(userServiceUrl+"organization/123").toString();
        return baseUrl;


    }

}
