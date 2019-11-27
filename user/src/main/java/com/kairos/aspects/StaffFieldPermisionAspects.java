package com.kairos.aspects;

import com.kairos.annotations.KPermissionModel;
import com.kairos.dto.user_context.UserContext;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.service.access_permisson.AccessPageService;
import com.kairos.service.kpermissions.PermissionService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;

@Aspect
@Component
public class StaffFieldPermisionAspects {

    @Inject
    private PermissionService permissionService;
    @Inject private AccessPageService accessPageService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StaffFieldPermisionAspects.class);


  /*  @Around("execution(public com.kairos.persistence.model.staff.personal_details.Staff com.kairos.service.staff.*.*(..))")
    public <T extends UserBaseEntity> Staff validateStaffResponseAsPerPermission(ProceedingJoinPoint proceedingJoinPoint) {
        try {
            Staff staff = (Staff) proceedingJoinPoint.proceed();
            List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(staff.getClass(), newArrayList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
            return ObjectMapperUtils.copyObjectSpecificPropertiesByMapper(staff, staff.getClass().newInstance(), kPermissionModelFieldDTOS, UserBaseEntity.class);
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }

    @Around("execution(public java.util.List<com.kairos.persistence.model.staff.personal_details.Staff> com.kairos.service.staff.*.*(..))")
    public List<Staff> validateStaffListResponseAsPerPermission(ProceedingJoinPoint proceedingJoinPoint) {
        List<Staff> newStaffList = new ArrayList<>();
        try {
            List<Staff> staffList = (List<Staff>) proceedingJoinPoint.proceed();
            if (!staffList.isEmpty()) {
                List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(staffList.get(0).getClass(), newArrayList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
                for (Staff staffObj : staffList) {
                    newStaffList.add(ObjectMapperUtils.copyObjectSpecificPropertiesByMapper(staffObj, staffObj.getClass().newInstance(), kPermissionModelFieldDTOS, UserBaseEntity.class));
                }
            }
            return newStaffList;
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }


    @Around("execution(public java.util.List<com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO> com.kairos.service.staff.*.*(..))")
    public List<StaffPersonalDetailDTO> validateStaffDTOResponseAsPerPermission(JoinPoint proceedingJoinPoint) {
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOList = new ArrayList<>();
        try {
            List<StaffPersonalDetailDTO> staffList = (List<StaffPersonalDetailDTO>) proceedingJoinPoint;
            if (!staffList.isEmpty()) {
                List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(Staff.class, newArrayList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
                for (StaffPersonalDetailDTO staffObj : staffList) {
                    staffPersonalDetailDTOList.add(ObjectMapperUtils.copySpecificPropertiesByMapper(staffObj, null, kPermissionModelFieldDTOS));
                }
            }
            return staffPersonalDetailDTOList;
        } catch (Throwable ex) {
            LOGGER.error(ex.getMessage());
        }
        return null;
    }*/

    //@Around("execution(public com.kairos.persistence.model.staff.personal_details.StaffPersonalDetail com.kairos.persistence.repository.user.staff.StaffGraphRepository*.*(..))")
    @Before("execution(* com.kairos.persistence.repository.custom_repository.Neo4jBaseRepository.save(..))")
    public <T extends UserBaseEntity> void validateStaffResponseAsPerPermissdsaadion(JoinPoint joinPoint) {
        List<T> objects = checkAndReturnValidModel(joinPoint);
        if(isCollectionNotEmpty(objects)) {
            permissionService.updateModelBasisOfPermission(objects);
        }
    }

    private <T extends UserBaseEntity> List<T> checkAndReturnValidModel(JoinPoint joinPoint) {
        List<T> validModels = new ArrayList<>();
        if(isNotNull(UserContext.getUserDetails())){
            boolean accessGroupValid = !accessPageService.isHubMember(UserContext.getUserDetails().getId());
            boolean argsValid = joinPoint.getArgs().length!=0;
            if(accessGroupValid && argsValid){
                validModels = Arrays.stream(joinPoint.getArgs()).filter(arg -> arg.getClass().isAnnotationPresent(KPermissionModel.class)).map(model->(T)model).collect(Collectors.toList());
            }
        }
        return validModels;
    }


}
