package com.kairos.scheduler.rest_client;

import com.kairos.scheduler.utils.user_context.UserContext;

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
                String baseUrl = new StringBuilder(userServiceUrl).append("unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                return baseUrl;
            } else {
                String baseUrl = new StringBuilder(userServiceUrl).append("country/").append(id).toString();
                return baseUrl;
            }
        }

    }




}
