package com.kairos.utils;

import com.kairos.utils.user_context.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by vipul on 19/9/17.
 */
@Component
public class RestClientUrlUtil {

    private static String userServiceUrl;
    private static String plannerServiceUrl;
    private static String schedulerServiceUrl;

    @Value("${gateway.plannerservice.url}")
    public void setPlannerServiceUrl(String plannerServiceUrl) {
        RestClientUrlUtil.plannerServiceUrl = plannerServiceUrl;
    }

    @Value("${gateway.schedulerservice.url}")
    public void setSchedulerServiceUrl(String schedulerServiceUrl) {
        RestClientUrlUtil.schedulerServiceUrl = schedulerServiceUrl;
    }

    @Value("${gateway.userservice.url}")
    public void setUserServiceUrl(String userServiceUrl) {
        RestClientUrlUtil.userServiceUrl = userServiceUrl;
    }


    public final static String getBaseUrl(boolean hasUnitInUrl) {

        String baseUrl;
        if (hasUnitInUrl) {
            baseUrl = new StringBuilder(userServiceUrl).append("/unit/").append(UserContext.getUnitId()).toString();
        } else {
            baseUrl = userServiceUrl;
        }
        return baseUrl;
    }

    public final static String getBaseUrl(boolean hasUnitInUrl, Long id) {
        String baseUrl;
        if (!Optional.ofNullable(id).isPresent()) {
            baseUrl = userServiceUrl;
        } else {
            if (hasUnitInUrl) {
                baseUrl = new StringBuilder(userServiceUrl)
                        .append("unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            } else {
                baseUrl = new StringBuilder(userServiceUrl).append("country/").append(id).toString();
            }
        }
        return baseUrl;

    }

    public final static String getBaseUrl() {
        return userServiceUrl;
    }

    public static final String getPlannerBaseUrl() {
        return new String(plannerServiceUrl + "unit/");
    }

    public final static String getSchedulerBaseUrl(boolean hasUnitInUrl, Long id) {
        String baseUrl;
        if (hasUnitInUrl) {
            baseUrl = new StringBuilder(schedulerServiceUrl).append("unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
        } else {
            baseUrl = schedulerServiceUrl;
        }
        return baseUrl;

    }


}
