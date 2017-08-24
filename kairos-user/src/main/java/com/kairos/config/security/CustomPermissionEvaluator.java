package com.kairos.config.security;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.persistence.model.organization.Organization;
import com.kairos.persistence.model.user.auth.User;
import com.kairos.persistence.model.user.auth.UserAuthentication;
import com.kairos.persistence.repository.organization.OrganizationGraphRepository;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.kairos.constants.AppConstants.GET;
import static com.kairos.constants.AppConstants.ORGANIZATION_ID;
import static com.kairos.util.userContext.UserContext.UNIT_ID;

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

    @Inject
    private OrganizationGraphRepository organizationGraphRepository;

    @Autowired
    private HttpServletRequest request;

    public boolean isAuthorized() {

        if(hasCountryAdminRole()){
            return true;
        }
        User user = UserAuthentication.getCurrentUser();
        Long organizationId = extractIdOrgIdFromUrl();
        String tabId = request.getParameter("moduleId");
        Assert.notNull(user,"User can not be null");
        Assert.notNull(tabId,"tab id can not be null");
        Assert.notNull(organizationId,"Organization id can not be null");
        if(!hasPermission(organizationId,user.getId(),tabId)){
            throw new InvalidRequestException("You don't have permission to perform this operation");
        }
        return true;
    }

    private boolean hasPermission(long organizationId,long userId,String tabId) {

        Organization organization = organizationGraphRepository.findOne(organizationId,0);
        if(organization == null){
            return false;
        }

        if (GET.equals(request.getMethod())) {
            return accessGroupRepository.hasReadPermission(organizationId, userId, tabId);
        }
        return accessGroupRepository.hasWritePermission(organizationId, userId, tabId);
    }

    private Long extractIdOrgIdFromUrl(){

        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if(pathVariables.get(UNIT_ID) != null){
            return Long.valueOf(pathVariables.get(UNIT_ID));
        } else if(pathVariables.get(ORGANIZATION_ID) != null){
            return Long.valueOf(pathVariables.get(ORGANIZATION_ID));
        }else {
            return null;
        }
    }

    private boolean hasCountryAdminRole(){
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long organizationId = (pathVariables.get(ORGANIZATION_ID) == null)?null:Long.valueOf(pathVariables.get("organizationId"));
        final String message = "Organization not found";
        if(organizationId == null){
            throw new InternalError(message);
        }
        Organization organization = organizationGraphRepository.findOne(organizationId,0);
        if(organization == null){
            throw new InternalError(message);
        }
        return organization.isKairosHub();
    }

}
