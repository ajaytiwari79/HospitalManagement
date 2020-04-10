package com.kairos.config.interceptor;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import com.kairos.service.auth.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.persistence.model.auth.UserAuthentication.getCurrentUser;

/**
 * Created by anil on 10/8/17.
 */
public class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @Inject
    private UserService userService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        if(request.getRequestURL().toString().contains("/scheduler_execute_job")) return true;
        if(request.getRequestURI().indexOf("swagger-ui")>-1) return true;
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        getCurrentUserDetails();
        String orgIdString=isNotNull(pathVariables) ? pathVariables.get("organizationId") : null;
        String unitIdString=isNotNull(pathVariables) ? pathVariables.get("unitId") : null;
        LOGGER.debug("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI()+"[ organizationID ,Unit Id " +orgIdString+" ,"+unitIdString+" ]"); ;
        updateOrganizationId(orgIdString);
        updateUnitId(unitIdString);

        String tabId = request.getParameter("moduleId");
        if(Optional.ofNullable(tabId).isPresent()){
            UserContext.setTabId(tabId);
        }
        return isNotNull(request);
    }

    private void getCurrentUserDetails() {
        try {
            CurrentUserDetails currentUserDetails = ObjectMapperUtils.copyPropertiesOrCloneByMapper(userService.getCurrentUser(), CurrentUserDetails.class);
            if(isNotNull(UserContext.getUserDetails()) && isNotNull(currentUserDetails)) {
                UserContext.setUserDetails(currentUserDetails);
            }
        } catch (Exception e) {
            LOGGER.error("exception {}",e);
        }
    }

    private void updateOrganizationId(String orgIdString) {
        if(orgIdString!=null && !"null".equalsIgnoreCase(orgIdString)){
            final Long orgId = Long.valueOf(orgIdString);
            UserContext.setOrgId(orgId);
        }
    }

    private void updateUnitId(String unitIdString) {
        if(unitIdString!=null){
            final Long unitId = Long.valueOf(unitIdString);
            UserContext.getUserDetails().setLastSelectedOrganizationId(unitId);
            UserContext.setUnitId(unitId);
            UserContext.getUserDetails().setLastSelectedOrganizationId(unitId);
        }
    }
}
