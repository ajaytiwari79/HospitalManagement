package com.kairos.config.interceptor;

import com.kairos.util.userContext.UserContext;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by anil on 10/8/17.
 */
public class ExtractOrganizationAndUnitInfoInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = Logger.getLogger(ExtractOrganizationAndUnitInfoInterceptor.class);

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {


        final Map<String, String> pathVariables = (Map<String, String>) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String orgIdStirng=pathVariables.get("organizationId");
        String unitIdString=pathVariables.get("unitId");
        log.info("[preHandle][" + request + "]" + "[" + request.getMethod()
                + "]" + request.getRequestURI()+"[ orgainzationID ,Unit Id " +orgIdStirng+" ,"+unitIdString+" ]") ;

        if(orgIdStirng!=null&&unitIdString!=null){
              final Long orgId = Long.valueOf(orgIdStirng);
              final Long unitId = Long.valueOf(unitIdString);
              UserContext.setOrgId(orgId);
              UserContext.setUnitId(unitId);
          }


        return true;
    }
}
