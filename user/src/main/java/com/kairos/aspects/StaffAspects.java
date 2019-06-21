package com.kairos.aspects;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.kpermissions.KPermissionModelFieldDTO;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.staff.personal_details.Staff;
import com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO;
import com.kairos.service.kpermissions.PermissionService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Configuration
public class StaffAspects {

    @Inject
    private PermissionService permissionService;


    @Around("execution(public com.kairos.persistence.model.staff.personal_details.Staff com.kairos.service.staff.*.*(..))")
    public <T extends UserBaseEntity> Staff validateStaffResponseAsPerPermission(ProceedingJoinPoint proceedingJoinPoint){
        try {
            Staff staff = (Staff)proceedingJoinPoint.proceed();
            List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(staff.getClass(), Arrays.asList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
            return ObjectMapperUtils.copyObjectSpecificPropertiesByMapper(staff, staff.getClass().newInstance(), kPermissionModelFieldDTOS, UserBaseEntity.class);
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Around("execution(public java.util.List<com.kairos.persistence.model.staff.personal_details.Staff> com.kairos.service.staff.*.*(..))")
    public List<Staff> validateStaffListResponseAsPerPermission(ProceedingJoinPoint proceedingJoinPoint){
        List<Staff> newStaffList = new ArrayList<>();
        try {
            List<Staff> staffList = (List<Staff>)proceedingJoinPoint.proceed();
            if(!staffList.isEmpty()) {
                List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(staffList.get(0).getClass(), Arrays.asList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
                for(Staff staffObj : staffList){
                    newStaffList.add(ObjectMapperUtils.copyObjectSpecificPropertiesByMapper(staffObj, staffObj.getClass().newInstance(), kPermissionModelFieldDTOS, UserBaseEntity.class));
                }
            }
            return newStaffList;
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return null;
    }


    @Around("execution(public java.util.List<com.kairos.persistence.model.staff.personal_details.StaffPersonalDetailDTO> com.kairos.service.staff.*.*(..))")
    public List<StaffPersonalDetailDTO> validateStaffDTOResponseAsPerPermission(ProceedingJoinPoint proceedingJoinPoint){
        List<StaffPersonalDetailDTO> staffPersonalDetailDTOList = new ArrayList<>();
        try {
            List<StaffPersonalDetailDTO> staffList = (List<StaffPersonalDetailDTO>)proceedingJoinPoint.proceed();
            if(!staffList.isEmpty()) {
                List<KPermissionModelFieldDTO> kPermissionModelFieldDTOS = permissionService.fetchPermissionFields(Staff.class, Arrays.asList(FieldLevelPermission.WRITE, FieldLevelPermission.READ));
                for(StaffPersonalDetailDTO staffObj : staffList){
                    staffPersonalDetailDTOList.add(ObjectMapperUtils.copySpecificPropertiesByMapper(staffObj, null, kPermissionModelFieldDTOS));
                }
            }
            return staffPersonalDetailDTOList;
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return null;
    }


}
