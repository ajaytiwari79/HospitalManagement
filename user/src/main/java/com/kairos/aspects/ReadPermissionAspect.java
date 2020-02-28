package com.kairos.aspects;

import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.kpermissions.PermissionService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;

@Aspect
@Component
public class ReadPermissionAspect {

    @Inject private AccessPageService accessPageService;

    //@Before("execution(* com.kairos.utils.response.ResponseHandler.generateResponse(..))")
    /* public static <T> void validateStaffResponseAsPerPermission(Object object) {
       Object[] objectArray = object instanceof Collection ? ((Collection) object).toArray() : new Object[]{object};
        List<T> objects = checkAndReturnValidModel(objectArray);
        if(isCollectionNotEmpty(objects)) {
            permissionService.updateModelBasisOfPermission(objects, newHashSet(FieldLevelPermission.READ,FieldLevelPermission.HIDE));
        }
    }*/
}
