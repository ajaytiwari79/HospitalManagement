package com.kairos.scheduler.rest_client;

import com.kairos.scheduler.utils.user_context.UserContext;

import java.util.Optional;

/**
 * Created by vipul on 19/9/17.
 */
public class RestClientUrlUtil {



    public final static String getBaseUrl(boolean hasUnitInUrl, Long id,String userServiceUrl) {
        if(!Optional.ofNullable(id).isPresent()) {
            return userServiceUrl;
        }else {
            if (hasUnitInUrl) {
                return new StringBuilder(userServiceUrl).append("unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            } else {
                return new StringBuilder(userServiceUrl).append("country/").append(id).toString();
            }
        }

    }




}
