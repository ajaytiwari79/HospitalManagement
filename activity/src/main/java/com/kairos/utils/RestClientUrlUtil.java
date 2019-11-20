package com.kairos.utils;

import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.rest_client.RestClientUrlType;
import com.kairos.rest_client.GenericRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Created by vipul on 19/9/17.
 * @lastModifiedBy mohit @date 12-10-2018
 */
@Component
public class RestClientUrlUtil {

    public static final String ORGANIZATION = "organization/";
    public static final String UNIT = "/unit/";
    private static  String userServiceUrl;
    private static  String plannerServiceUrl;
    private static String schedulerServiceUrl;

    //~==============resolve
    @Value("${gateway.plannerservice.url}")
    public void setPlannerServiceUrl(String plannerServiceUrl) {
        RestClientUrlUtil.plannerServiceUrl = plannerServiceUrl;
    }
    @Value("${gateway.schedulerservice.url}")
    public void setSchedulerServiceUrl(String schedulerServiceUrl) {
        RestClientUrlUtil.schedulerServiceUrl = schedulerServiceUrl;
    }
    @Value("${gateway.userservice.url}")
    public  void setUserServiceUrl(String userServiceUrl) {
        RestClientUrlUtil.userServiceUrl = userServiceUrl;
    }

   //~ ================================for {userServiceUrl}======================================

    //TODO Remove
    @Deprecated
    public final static String getBaseUrl(boolean hasUnitInUrl, Long id) {
        boolean idExists=Optional.ofNullable(id).isPresent();
        if (hasUnitInUrl && idExists) {
            String baseUrl = new StringBuilder(userServiceUrl + ORGANIZATION).append(UserContext.getOrgId()).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            return baseUrl;
        } else {
            String baseUrl = new StringBuilder(userServiceUrl + ORGANIZATION).append(UserContext.getOrgId()).toString();
            return baseUrl;
        }
    }

    /**Currently used
     * Called by {@link GenericRestClient#publishRequest(Object, Long, RestClientUrlType, org.springframework.http.HttpMethod, String, List, ParameterizedTypeReference, Object...)}
     * Either prepare url
     * {userServiceUrl/organization/{organizationId}/unit/{unitId}}
     * or
     * {userServiceUrl/organization/{organizationId}/country/{countryId}}
     * or
     * {userServiceUrl/organization/{organizationId}}
     * @param restClientUrlType
     * @param id
     * @return
     * @author mohit
     * @date 12-10-2018
     */
    public static String getUserServiceBaseUrl(RestClientUrlType restClientUrlType,Long id){
        String baseUrl = null;

        switch (restClientUrlType){
            case UNIT:baseUrl = new StringBuilder(userServiceUrl ).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;
            case COUNTRY:baseUrl = new StringBuilder(userServiceUrl ).append("/country/").append(id).toString();
                break;
            case ORGANIZATION:baseUrl = new StringBuilder(userServiceUrl ).toString();
                break;
            case COUNTRY_WITHOUT_PARENT_ORG:
                baseUrl = new StringBuilder(userServiceUrl).append("/country/").append(id).toString();
                break;
            case UNIT_WITHOUT_PARENT_ORG:
                baseUrl = new StringBuilder(userServiceUrl).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;

        }
        return baseUrl;
    }

    public static String getUserServiceBaseUrl(RestClientUrlType restClientUrlType,Long id,Long parentId){
        String baseUrl = null;
        switch (restClientUrlType){
            case UNIT:baseUrl = new StringBuilder(userServiceUrl + ORGANIZATION).append(parentId).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;
            case COUNTRY:baseUrl = new StringBuilder(userServiceUrl + ORGANIZATION).append(parentId).append("/country/").append(id).toString();
                break;
            case ORGANIZATION:baseUrl = new StringBuilder(userServiceUrl + ORGANIZATION).append(parentId).toString();
                break;
            case COUNTRY_WITHOUT_PARENT_ORG:
                baseUrl = new StringBuilder(userServiceUrl).append("/country/").append(id).toString();
                break;
            case UNIT_WITHOUT_PARENT_ORG:
                baseUrl = new StringBuilder(userServiceUrl).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
                break;

        }
        return baseUrl;
    }

    //~ ======================================================================

    //TODO FIX
    @Deprecated
    public static final String getBaseUrl(boolean hasUnitInUrl){
        if(hasUnitInUrl){
            String baseUrl=new StringBuilder(userServiceUrl+ ORGANIZATION).append(UserContext.getOrgId()).append(UNIT).append(UserContext.getUnitId()).toString();
            return baseUrl;
        }else{
            String baseUrl=new StringBuilder(userServiceUrl+ ORGANIZATION).append(UserContext.getOrgId()).toString();
            return baseUrl;
        }

    }
    //TODO FIX
    @Deprecated
    public static final String getBaseUrl(Long organizationId, Long unitId, Long countryId){
        StringBuilder baseUrl=new StringBuilder(userServiceUrl+ ORGANIZATION +organizationId);
        if(Optional.ofNullable(unitId).isPresent()){
            return baseUrl.append("/unitId/").append(unitId).toString();
        }else{
            return baseUrl.append("/countryId/").append(countryId).toString();
        }
    }
    //TODO FIX
    public static final String getBaseUrl(Long id,boolean hasUnitInUrl, Long parentOrganizationId){
        StringBuilder sb = new StringBuilder(userServiceUrl+ ORGANIZATION).append(parentOrganizationId);
            String baseUrl= hasUnitInUrl? sb.append(UNIT).append(id).toString() : sb.append("/country/").append(id).toString();
            return baseUrl;
    }

    //TODO FIX
    public static final String getBaseUrl() {
        return userServiceUrl;
    }


    public static final String getDefaultSchedulerUrl(){
        String baseUrl=new StringBuilder(userServiceUrl+"organization/123").toString();
        return baseUrl;
    }
    public static final String getPlannerBaseUrl(){
        String baseUrl=new StringBuilder(plannerServiceUrl).toString();
        return baseUrl;

    }



    public final static String getSchedulerBaseUrl(boolean hasUnitInUrl, Long id) {
        if (hasUnitInUrl) {

            String baseUrl = new StringBuilder(schedulerServiceUrl).append(UNIT).append((Optional.ofNullable(id).isPresent() ? id : UserContext.getUnitId())).toString();
            return baseUrl;
        } else {
            String baseUrl = schedulerServiceUrl;
            return baseUrl;
        }
    }


}
