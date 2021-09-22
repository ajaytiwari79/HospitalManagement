package com.kairos.interceptor;

import com.kairos.dto.user_context.UserContext;
import com.kairos.rest_client.UserIntegrationService;
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

/**
 * Created by anil on 10/8/17.
 */
public class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @Inject
    private UserIntegrationService userIntegrationService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        if(request.getRequestURI().contains("swagger-ui")) return true;
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        updateUserInfo(request, pathVariables);

        String tabId = request.getParameter("moduleId");
        if(Optional.ofNullable(tabId).isPresent()){
            UserContext.setTabId(tabId);
        }
        return isNotNull(request);
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
