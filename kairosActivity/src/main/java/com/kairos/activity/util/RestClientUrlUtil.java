package com.kairos.activity.util;

import com.kairos.activity.util.userContext.UserContext;

/**
 * Created by vipul on 19/9/17.
 */
public class RestClientUrlUtil {
    public static final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).append("/unit/").append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder("http://zuulservice/kairos/user/api/v1/organization/").append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
    public static final String getCommonUrl() {
        String baseUrl = new String("http://zuulservice/kairos/user/api/v1/");
        return baseUrl;


    }
}
