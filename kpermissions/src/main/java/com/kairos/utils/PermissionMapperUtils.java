package com.kairos.utils;

import com.kairos.dto.kpermissions.FieldDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;

import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionMapperUtils {

    public static final Set<String> personalisedModel = newHashSet("Staff");
    private static  final Logger LOGGER = LoggerFactory.getLogger(PermissionMapperUtils.class);

    public static <E>  E copySpecificPropertiesByMapper(E src, E target, PermissionHelper permissionHelper) {
        if (src != null) {
            BeanWrapper targetWrapper = null;
            try {
                if (isNull(target)) {
                    target = (E)src.getClass().newInstance();
                }
                BeanWrapper srcWrapper = PropertyAccessorFactory.forBeanPropertyAccess(src);
                targetWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
                updatePropertyByPermission(permissionHelper, targetWrapper, srcWrapper,"",permissionHelper.getModelDTO());
                return (E) srcWrapper.getWrappedInstance();
            } catch (Exception ex) {
                LOGGER.error("Error {}",ex);
            }
        }else{
            return null;
        }
        return null;
    }

    private static boolean verifyFieldPermission(FieldDTO fieldDTO, PermissionHelper permissionHelper) {
        if(!permissionHelper.isPersonalizedClass() || permissionHelper.isSameStaff()){
            return !fieldDTO.getPermissions().contains(permissionHelper.getPermission());
        }else if(permissionHelper.isPersonalizedClass() && !permissionHelper.isSameStaff()){
            return !(fieldDTO.getForOtherPermissions().isValid(permissionHelper.otherPermissions) && fieldDTO.getForOtherPermissions().getPermissions().contains(permissionHelper.getPermission()));
        }
        return true;
    }


    private static void updatePropertyByPermission(PermissionHelper permissionHelper, BeanWrapper targetWrapper, BeanWrapper srcWrapper, String subFieldName, ModelDTO modelDTO) {
        for (FieldDTO field : modelDTO.getFieldPermissions()) {
            try {
                if (verifyFieldPermission(field,permissionHelper)) {
                    srcWrapper.setPropertyValue(subFieldName+field.getFieldName(), targetWrapper.getPropertyValue(subFieldName+field.getFieldName()));
                }
            }catch (Exception exception){
               LOGGER.error("Error {}",exception);
            }
        }
        if(isCollectionNotEmpty(modelDTO.getSubModelPermissions())){
            for (ModelDTO subModelPermission : modelDTO.getSubModelPermissions()) {
                updatePropertyByPermission(permissionHelper, targetWrapper, srcWrapper,subModelPermission.getModelName()+".",subModelPermission);

            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class PermissionHelper{
        private ModelDTO modelDTO;
        private Long currentUserStaffId;
        private boolean sameStaff;
        private Map<Long, OtherPermissionDTO> otherPermissionDTOMap;
        private boolean hubMember;
        //Permission To check
        private FieldLevelPermission permission;
        private OtherPermissionDTO otherPermissions;
        private boolean personalizedClass;

        public PermissionHelper(ModelDTO modelDTO, Long currentUserStaffId, Map<Long, OtherPermissionDTO> otherPermissionDTOMap, boolean hubMember,FieldLevelPermission permission) {
            this.modelDTO = modelDTO;
            this.currentUserStaffId = currentUserStaffId;
            this.otherPermissionDTOMap = otherPermissionDTOMap;
            this.permission = permission;
            this.hubMember = hubMember;
        }
    }

}
