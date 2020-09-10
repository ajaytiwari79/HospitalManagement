package com.kairos.utils.validator.company;

import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.kpermissions.PermissionService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.Serializable;

@Component
public class AppPermissionEvaluator implements PermissionEvaluator {
    @Inject
    private PermissionService permissionService;
    @Inject
    private ExceptionService exceptionService;

    public <T> boolean isValid(String modelName, PermissionAction action) {
        //TODO will uncomment this code after integrating with FE
//        if(!UserContext.getUserDetails().isSystemAdmin()){
//            boolean authorized= permissionService.validPermissionAction(modelName,action, UserContext.getUserDetails().getLastSelectedOrganizationId());
//            if(!authorized){
//                exceptionService.actionNotPermittedException("message.invalid_action",action.toString(),modelName);
//            }
//        }
        return true;

    }



    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}
