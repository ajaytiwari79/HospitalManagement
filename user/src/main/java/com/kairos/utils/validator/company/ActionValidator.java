package com.kairos.utils.validator.company;

import com.kairos.annotations.ActionValid;
import com.kairos.dto.kpermissions.PermissionActionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.PermissionAction;
import com.kairos.service.kpermissions.PermissionService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class ActionValidator implements ConstraintValidator<ActionValid, PermissionActionDTO> {
    @Inject
    private PermissionService permissionService;

    @Override
    public void initialize(ActionValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(PermissionActionDTO value, ConstraintValidatorContext context) {
        boolean systemAdmin = UserContext.getUserDetails().isSystemAdmin();
        if(systemAdmin){
            return true;
        }
        return permissionService.validPermissionAction(value, UserContext.getUserDetails().getLastSelectedOrganizationId());
    }
}
