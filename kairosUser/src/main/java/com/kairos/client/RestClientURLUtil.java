package com.kairos.client;

import com.kairos.util.userContext.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RestClientURLUtil {

    private static  String userServiceUrl;
    private static  String plannerServiceUrl;
    @Value("${gateway.plannerservice.url}")
    public void setPlannerServiceUrl(String plannerServiceUrl) {
        RestClientURLUtil.plannerServiceUrl = plannerServiceUrl;
    }
    @Value("${gateway.activityservice.url}")
    public  void setUserServiceUrl(String userServiceUrl) {
        RestClientURLUtil.userServiceUrl = userServiceUrl;
    }

    public final static String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }
    }

    public final static String getBaseUrl(boolean hasUnitInUrl, Long countryId){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(UserContext.getOrgId()).append("/country/").append(countryId).toString();
            return baseUrl;
        }
    }

    public final static String getBaseUrl(){
        return userServiceUrl;
    }

    public static final String getPlannerBaseUrl(){
        String baseUrl=new StringBuilder(plannerServiceUrl+"unit/").toString();
        return baseUrl;

    }
}
