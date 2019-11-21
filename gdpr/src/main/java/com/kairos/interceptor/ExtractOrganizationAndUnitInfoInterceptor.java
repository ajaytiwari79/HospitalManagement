package com.kairos.interceptor;


import com.kairos.commons.custom_exception.InvalidRequestException;
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

/**
 * Created by anil on 10/8/17.
 */

class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) {

        LOGGER.debug("request uri is "+request.getRequestURI());

        if(request.getRequestURI().contains("swagger-ui")) return true;
        else if(request.getRequestURI().contains("css")) return true;
        else if(request.getRequestURI().contains("js")) return true;
        else if(request.getRequestURI().contains("images")) return true;
//        else if(request.getRequestURI().contains("public/legal")) return true;
        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        if (pathVariables == null) {
            throw new InvalidRequestException("Url or Parameter is not correct");
        }

        String orgIdString = pathVariables.get("organizationId");
        String unitIdString = pathVariables.get("unitId");
        String countryIdString = pathVariables.get("countryId");
        LOGGER.info("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI() + "[ organizationID ,Unit Id " + orgIdString + " ," + unitIdString + " ]");

        if (orgIdString!=null && !"null".equalsIgnoreCase(orgIdString)) {
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
        return isNotNull(httpServletRequest);
    }


}
