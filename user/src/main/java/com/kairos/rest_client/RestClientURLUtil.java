package com.kairos.rest_client;

import com.kairos.dto.user_context.UserContext;
import com.kairos.utils.RestClientUrlUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestClientURLUtil {

    public static final String UNIT = "unit/";
    private static String activityServiceUrl;
    private static String plannerServiceUrl;
    private static String schedulerServiceUrl;
    private static String gdprServiceUrl;
    public static String kpiServiceUrl;

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
        RestClientURLUtil.activityServiceUrl = userServiceUrl;
    }

    @Value("${gateway.gdprservice.url}")
    public void setGdprServiceUrl(String gdprServiceUrl) {
        RestClientURLUtil.gdprServiceUrl = gdprServiceUrl;
    }

    @Value("${gateway.kpiservice.url}")
    public  void setKPIServiceUrl(String kpiServiceUrl) {RestClientURLUtil.kpiServiceUrl = kpiServiceUrl;
    }

    public final static String getBaseUrl(boolean hasUnitInUrl) {
        if (hasUnitInUrl) {
            return new StringBuilder(activityServiceUrl).append("/unit/").append(UserContext.getUnitId()).toString();
        } else {
            return new StringBuilder(activityServiceUrl).toString();        }
    }

    public final static String getBaseUrl(boolean hasUnitInUrl, Long id) {
        if (!Optional.ofNullable(id).isPresent()) {
            return activityServiceUrl;
        } else {
            if (hasUnitInUrl) {
                return new StringBuilder(activityServiceUrl)
                        .append("/unit/").append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            } else {
                return new StringBuilder(activityServiceUrl).append("/country/").append(id).toString();
            }
        }

    }

    public final static String getBaseUrl() {
        return activityServiceUrl;
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
