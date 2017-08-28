package com.kairos.config.interceptor;
import com.kairos.persistence.repository.user.access_permission.AccessGroupRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by prabjot on 21/11/16.
 */
@Component
public class VerifyPermissionInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = Logger.getLogger(VerifyPermissionInterceptor.class);

    @Inject
    AccessGroupRepository accessGroupRepository;

    //before the actual handler will be executed
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler)
            throws Exception {

       /* boolean isAccess = false;

        String url = request.getRequestURI();
        Pattern r = Pattern.compile("/api\\/v1\\/organization\\/(\\d*)\\/unit\\/(\\d*)");
        Matcher m = r.matcher(url);
        if (m.find( )) {
            String parentOrganizationId = m.group(1);
            String unitId = m.group(2);

            String tabId = request.getParameter("tabId");
            if(tabId == null)
                tabId = request.getParameter("moduleId");


            Map<String, Object> data = accessGroupRepository.getAccessPermission(Long.parseLong(parentOrganizationId), 22, tabId);


            if (OPTIONS.equals(request.getMethod())) {
                isAccess = true;
            } else if (GET.equals(request.getMethod())) {
                List<Boolean> readPermissions = (List<Boolean>) data.get("readPermission");
                if (readPermissions.contains(true)) {
                    isAccess = true;
                }
            } else {
                List<Boolean> readPermissions = (List<Boolean>) data.get("readPermission");
                List<Boolean> writePermissions = (List<Boolean>) data.get("writePermission");
                if (readPermissions.contains(true) && writePermissions.contains(true)) {
                    isAccess = true;
                }
            }
        }else {
            logger.info("regex not matched, unit id or parent organization id is not present");
        }*/

        return true;


        /*if (!isAccess) {
            throw new InvalidRequestException();
        }*/

        //TODO will change value based on isAccess variable
        //return true;
    }
}
