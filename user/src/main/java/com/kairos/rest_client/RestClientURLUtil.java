package com.kairos.rest_client;

import com.kairos.dto.user_context.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestClientURLUtil {

    public static final String UNIT = "unit/";
    private static String userServiceUrl;
    private static String plannerServiceUrl;
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
    public void setUserServiceUrl(String userServiceUrl) {
        RestClientURLUtil.userServiceUrl = userServiceUrl;
    }

    @Value("${gateway.gdprservice.url}")
    public void setGdprServiceUrl(String gdprServiceUrl) {
        RestClientURLUtil.gdprServiceUrl = gdprServiceUrl;
    }

    public final static String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
            return new StringBuilder(userServiceUrl).append("/unit/").append(UserContext.getUnitId()).toString();
        } else {
            return new StringBuilder(userServiceUrl).toString();        }
    }

    public final static String getBaseUrl(boolean hasUnitInUrl, Long id) {
        if (!Optional.ofNullable(id).isPresent()) {
            return userServiceUrl;
        } else {
            if (hasUnitInUrl) {
                return new StringBuilder(userServiceUrl)
                        .append("/unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            } else {
                return new StringBuilder(userServiceUrl).append("/country/").append(id).toString();
            }
        }

    }

    public final static String getBaseUrl() {
        return userServiceUrl;
    }

    public static final String getPlannerBaseUrl() {
        return new StringBuilder(plannerServiceUrl + UNIT).toString();
    }

    public final static String getSchedulerBaseUrl(boolean hasUnitInUrl, Long id) {
        if (hasUnitInUrl) {

            return new StringBuilder(schedulerServiceUrl).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
        } else {
            return schedulerServiceUrl;
        }
    }

    public final static String getGdprServiceBaseUrl(boolean hasUnitInUrl, Long id) {
        if (!Optional.ofNullable(id).isPresent()) {
            return gdprServiceUrl;
        } else {
            if (hasUnitInUrl) {
                return new StringBuilder(gdprServiceUrl).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            } else {
                return new StringBuilder(gdprServiceUrl + UNIT).append(Optional.ofNullable(UserContext.getOrgId()).isPresent() ? UserContext.getOrgId() : "24").append("/country/").append(id).toString();
            }
        }

    }


}
