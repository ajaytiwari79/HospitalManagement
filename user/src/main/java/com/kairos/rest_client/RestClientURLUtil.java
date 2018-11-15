package com.kairos.rest_client;

import com.kairos.commons.utils.RestClientUrlUtil;
import com.kairos.utils.user_context.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestClientURLUtil {

    private static  String userServiceUrl;
    private static  String plannerServiceUrl;
    private static String schedulerServiceUrl;
    private static String gdprServiceUrl;

    @Value("${gateway.plannerservice.url}")
    public void setPlannerServiceUrl(String plannerServiceUrl) {
        RestClientURLUtil.plannerServiceUrl = plannerServiceUrl;
    }

    @Value("${gateway.schedulerservice.url}")
    public void setSchedulerServiceUrl(String schedulerServiceUrl) {
        RestClientURLUtil.schedulerServiceUrl = schedulerServiceUrl;
    }

    @Value("${gateway.activityservice.url}")
    public  void setUserServiceUrl(String userServiceUrl) {
        RestClientURLUtil.userServiceUrl = userServiceUrl;
    }

    @Value("${gateway.gdprservice.url}")
    public void setGdprServiceUrl(String gdprServiceUrl) {
        RestClientURLUtil.gdprServiceUrl = gdprServiceUrl;
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

    public final static String getBaseUrl(boolean hasUnitInUrl, Long id){
        if(!Optional.ofNullable(id).isPresent()) {
            String baseUrl = userServiceUrl;
            return baseUrl;
        }else {
            if(hasUnitInUrl){
                String baseUrl=new StringBuilder(userServiceUrl+"organization/")
                        .append(Optional.ofNullable(UserContext.getOrgId()).isPresent()?UserContext.getOrgId():"24").append("/unit/").append((Optional.ofNullable(id).isPresent()?id:UserContext.getUnitId())).toString();
                return baseUrl;
            }else{
                String baseUrl=new StringBuilder(userServiceUrl+"organization/").append(Optional.ofNullable(UserContext.getOrgId()).isPresent()?UserContext.getOrgId():"24").append("/country/").append(id).toString();
                return baseUrl;
            }
        }

    }

    public final static String getBaseUrl(){
        return userServiceUrl;
    }

    public static final String getPlannerBaseUrl(){
        String baseUrl=new StringBuilder(plannerServiceUrl+"unit/").toString();
        return baseUrl;

    }

    public final static String getSchedulerBaseUrl(boolean hasUnitInUrl, Long id) {
        if (hasUnitInUrl) {

            String baseUrl = new StringBuilder(schedulerServiceUrl).append("/unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            return baseUrl;
        } else {
            String baseUrl = schedulerServiceUrl;
            return baseUrl;
        }
    }

    public final static String getGdprServiceBaseUrl(boolean hasUnitInUrl, Long id){
        if(!Optional.ofNullable(id).isPresent()) {
            String baseUrl = gdprServiceUrl;
            return baseUrl;
        }else {
            if(hasUnitInUrl){
                String baseUrl=new StringBuilder(gdprServiceUrl+"organization/")
                        .append(Optional.ofNullable(UserContext.getOrgId()).isPresent()?UserContext.getOrgId():"24").append("/unit/").append((Optional.ofNullable(id).isPresent()?id:UserContext.getUnitId())).toString();
                return baseUrl;
            }else{
                String baseUrl=new StringBuilder(gdprServiceUrl+"organization/").append(Optional.ofNullable(UserContext.getOrgId()).isPresent()?UserContext.getOrgId():"24").append("/country/").append(id).toString();
                return baseUrl;
            }
        }

    }


}
