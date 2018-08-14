package com.kairos.interceptor;


import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.utils.user_context.UserContext;
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

/**
 * Created by anil on 10/8/17.
 */

public class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        if(request.getRequestURI().indexOf("swagger-ui")>-1) return true;
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null) {
            throw new InvalidRequestException("Url or Parameter is not correct");
        }

        String orgIdString = pathVariables.get("organizationId");
        String unitIdString = pathVariables.get("unitId");
        String countryIdString = pathVariables.get("countryId");
        log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI() + "[ organizationID ,Unit Id " + orgIdString + " ," + unitIdString + " ]");

        if (orgIdString != null) {
            final Long orgId = Long.valueOf(orgIdString);
            UserContext.setOrgId(orgId);
        }
        if (countryIdString != null) {
            final Long countryId = Long.valueOf(countryIdString);
            UserContext.setCountryId(countryId);

        }
        if (unitIdString != null) {
            final Long unitId = Long.valueOf(unitIdString);
            UserContext.setUnitId(unitId);
        }

        ServletRequestAttributes servletRequest = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest httpServletRequest = servletRequest.getRequest();

        String tabId = httpServletRequest.getParameter("moduleId");
        if (Optional.ofNullable(tabId).isPresent()) {
            UserContext.setTabId(tabId);
        }



        return true;
    }


}
