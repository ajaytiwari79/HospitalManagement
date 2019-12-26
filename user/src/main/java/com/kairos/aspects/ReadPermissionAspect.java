package com.kairos.aspects;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.kpermissions.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.utils.PermissionMapperUtils.checkAndReturnValidModel;

@Aspect
@Component
public class ReadPermissionAspect {


    private static PermissionService permissionService;
    @Inject
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Inject private AccessPageService accessPageService;

    //@Before("execution(* com.kairos.utils.response.ResponseHandler.generateResponse(..))")
    public static <T> void validateStaffResponseAsPerPermission(Object object) {
        Object[] objectArray = object instanceof Collection ? ((Collection) object).toArray() : new Object[]{object};
        List<T> objects = checkAndReturnValidModel(objectArray);
        if(isCollectionNotEmpty(objects)) {
            permissionService.updateModelBasisOfPermission(objects, newHashSet(FieldLevelPermission.READ,FieldLevelPermission.HIDE));
        }
    }
}
