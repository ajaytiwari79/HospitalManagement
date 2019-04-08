package com.kairos.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
/**
 * Created by shiv on 01/04/2019.
 */
@Component
public class RestClientUrlUtil {

    private static String userServiceUrl;

    @Value("${gateway.userservice.url}")
    public void setUserServiceUrl(String userServiceUrl) {
        RestClientUrlUtil.userServiceUrl = userServiceUrl;
    }

    public final static String getUserServiceUrl() {
        return userServiceUrl;
    }

}
