package com.kairos.interceptor;

import com.kairos.dto.user_context.CurrentUserDetails;
import com.kairos.dto.user_context.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.rest_client.UserIntegrationService.getCurrentUser;

/**
 * Created by anil on 10/8/17.
 */
public class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if(request.getRequestURI().indexOf("swagger-ui")>-1) return true;
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        getCurrentUserDetails();
        updateUserInfo(request, pathVariables);
        ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequest.getRequest();

        String tabId = httpServletRequest.getParameter("moduleId");
        if(Optional.ofNullable(tabId).isPresent()){
            UserContext.setTabId(tabId);
        }
        return isNotNull(httpServletRequest);
    }

    private void getCurrentUserDetails() {
        try {
            CurrentUserDetails userDetails = getCurrentUser();
            if(isNotNull(UserContext.getUserDetails()) && isNotNull(userDetails)) {
                UserContext.setUserDetails(userDetails);
            }
    } catch (Exception e) {
LOGGER.error("exception {}",e);
    }
    }

    private void updateUserInfo(HttpServletRequest request, Map<String, String> pathVariables) {
        String orgIdString=isNotNull(pathVariables) ? pathVariables.get("organizationId") : null;
        String unitIdString=isNotNull(pathVariables) ? pathVariables.get("unitId") : null;
        LOGGER.debug("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI()+"[ organizationId ,Unit Id " +orgIdString+" ,"+unitIdString+" ]");

        updateOrganizationId(orgIdString);
        updateUnitId(unitIdString);
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
            UserContext.setUnitId(unitId);
            if(isNotNull(UserContext.getUserDetails())) {
                UserContext.getUserDetails().setLastSelectedOrganizationId(unitId);
            }
        }
    }
}
