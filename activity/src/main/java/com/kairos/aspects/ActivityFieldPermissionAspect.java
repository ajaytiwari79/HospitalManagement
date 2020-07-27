package com.kairos.aspects;

import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.service.kpermissions.ActivityPermissionService;
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
public class ActivityFieldPermissionAspect {

    @Inject
    private ActivityPermissionService activityPermissionService;

    @Before("execution(* com.kairos.persistence.repository.custom_repository.MongoBaseRepository.save(..))")
    public <T extends MongoBaseEntity> void validateStaffPermission(JoinPoint joinPoint) {
        List<T> objects = checkAndReturnValidModel(joinPoint.getArgs());
        if(isCollectionNotEmpty(objects)) {
            activityPermissionService.updateModelBasisOfPermission(objects,newHashSet(FieldLevelPermission.WRITE));
        }
    }

    @Before("execution(* com.kairos.persistence.repository.custom_repository.MongoBaseRepository.saveEntities(..))")
    public <T extends MongoBaseEntity> void validateStaffsPermission(JoinPoint joinPoint) {
        if(joinPoint.getArgs().length>0) {
            Collection collection = (Collection) joinPoint.getArgs()[0];
            List<T> objects = checkAndReturnValidModel(collection.toArray());
            if (isCollectionNotEmpty(objects)) {
                activityPermissionService.updateModelBasisOfPermission(objects, newHashSet(FieldLevelPermission.WRITE));
            }
        }
    }
}
