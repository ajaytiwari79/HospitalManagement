package com.kairos.commons.utils;


import java.util.Optional;

/**
 * Created by vipul on 19/9/17.
 */
public class RestClientUrlUtil {



    public final static String getBaseUrl(boolean hasUnitInUrl, Long id,String userServiceUrl) {
        if(!Optional.ofNullable(id).isPresent()) {
            String baseUrl = userServiceUrl;
            return baseUrl;
        }else {
            if (hasUnitInUrl) {
                String baseUrl = new StringBuilder(userServiceUrl).append("unit/").append(id ).toString();
                return baseUrl;
            } else {
                String baseUrl = new StringBuilder(userServiceUrl).append("country/").append(id).toString();
                return baseUrl;
            }
        }

    }




}
