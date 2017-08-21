package com.kairos.config.security;

import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.persistence.repository.user.access_profile.AccessGroupRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import static com.kairos.constants.AppConstants.GET;
/**
 * Created by prabjot on 29/5/17.
 * This class provides functionality to check permissions of user
 * for particular organization and unit for specific role against particular page.
 * Class contained two custom methods, one with without unit id--it consider organization {parent organization} and unit {child organization} are same.
 * params {tabId--> id of access page},{http servlet request -> to identify type of operation,type can be get,put,post,delete}
 *
 * return {true or false based on the permission}
 */
@Component(value = "customPermissionEvaluator")
public class CustomPermissionEvaluator {

    @Inject
    private AccessGroupRepository accessGroupRepository;


    public boolean isAuthorized(Long orgId,String tabId, HttpServletRequest request) {

        User user = UserAuthentication.getCurrentUser();
        Assert.notNull(user,"User can not be null");
        Assert.notNull(user,"tab id can not be null");
        return hasPermission(orgId,orgId,user.getId(),tabId,request);
    }

    public boolean isAuthorized(long orgId,long unitId,String tabId, HttpServletRequest request) {

        User user = UserAuthentication.getCurrentUser();
        Assert.notNull(user,"User can not be null");
        Assert.notNull(user,"tab id can not be null");
        return hasPermission(orgId,unitId,user.getId(),tabId,request);
    }

    private boolean hasPermission(long organizationId, long unitId, long userId,String tabId, HttpServletRequest request) {
        if (GET.equals(request.getMethod())) {
            return accessGroupRepository.hasReadPermission(organizationId, unitId, userId, tabId);
        }
        return accessGroupRepository.hasWritePermission(organizationId, unitId, userId, tabId);
    }

}
