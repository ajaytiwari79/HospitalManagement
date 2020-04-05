package com.kairos.aspects;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.service.kpermissions.PermissionService;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.commons.utils.ObjectUtils.removeNull;
import static com.kairos.utils.PermissionMapperUtils.checkAndReturnValidModel;


@Aspect
@Component
public class ReadPermissionAspect {


    private static PermissionService permissionService;
    @Inject
    public void setPermissionService(PermissionService permissionService) {
        ReadPermissionAspect.permissionService = permissionService;
    }


   // @Before("execution(* com.kairos.utils.response.ResponseHandler.generateResponse(..))")
    public static <T> void validateResponseAsPerPermission(Object object) {
        Collection<Object> objectCollection=object instanceof Collection ?(Collection) object: Arrays.asList(object);
        Object[] objectArray = removeNull(objectCollection);
        List<T> objects = checkAndReturnValidModel(objectArray);
        if(isCollectionNotEmpty(objects)) {
            permissionService.updateModelBasisOfPermission(objects, newHashSet(FieldLevelPermission.READ));
        }
    }
}
