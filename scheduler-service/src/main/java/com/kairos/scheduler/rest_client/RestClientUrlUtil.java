package com.kairos.scheduler.rest_client;

import com.kairos.scheduler.utils.user_context.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    public final static String getBaseUrl(boolean hasUnitInUrl, Long id) {
        if(!Optional.ofNullable(id).isPresent()) {
            String baseUrl = userServiceUrl;
            return baseUrl;
        }else {
            if (hasUnitInUrl) {
                String baseUrl = new StringBuilder(userServiceUrl).append("organization/24").append("/unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                return baseUrl;
            } else {
                String baseUrl = new StringBuilder(userServiceUrl).append("country/").append(id).toString();
                return baseUrl;
            }
        }

    }




}
