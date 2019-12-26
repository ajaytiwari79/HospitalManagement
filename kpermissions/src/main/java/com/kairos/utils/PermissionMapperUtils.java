package com.kairos.utils;

import com.kairos.annotations.KPermissionModel;
import com.kairos.commons.annotation.PermissionClass;
import com.kairos.dto.kpermissions.FieldDTO;
import com.kairos.dto.kpermissions.ModelDTO;
import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpermissions.FieldLevelPermission;
import lombok.*;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermissionMapperUtils {

    public static final Set<String> personalisedModel = newHashSet("Staff");
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final byte DEFAULT_BYTE = 0;
    private static final short DEFAULT_SHORT = 0;
    private static final int DEFAULT_INT = 0;
    private static final long DEFAULT_LONG = 0;
    private static final float DEFAULT_FLOAT = 0;
    private static final double DEFAULT_DOUBLE = 0;
    private static final List DEFAULT_LIST = new ArrayList();
    private static final Set DEFAULT_SET = new HashSet();
    private static final Map DEFAULT_MAP = new HashMap();

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
            return !CollectionUtils.containsAny(fieldDTO.getPermissions(),permissionHelper.getFieldLevelPermissions());
        }else if(permissionHelper.isPersonalizedClass() && !permissionHelper.isSameStaff()){
            return !(fieldDTO.getForOtherPermissions().isValid(permissionHelper.otherPermissions) && CollectionUtils.containsAny(fieldDTO.getForOtherPermissions().getPermissions(),permissionHelper.getFieldLevelPermissions()));
        }
        return true;
    }


    private static void updatePropertyByPermission(PermissionHelper permissionHelper, BeanWrapper targetWrapper, BeanWrapper srcWrapper, String subFieldName, ModelDTO modelDTO) {
        for (FieldDTO field : modelDTO.getFieldPermissions()) {
            try {
                if (verifyFieldPermission(field,permissionHelper) || CollectionUtils.containsAny(permissionHelper.getFieldLevelPermissions(),newHashSet(FieldLevelPermission.READ,FieldLevelPermission.WRITE))) {
                    srcWrapper.setPropertyValue(subFieldName+field.getFieldName(), getPropertyValue(targetWrapper, subFieldName, field,permissionHelper));
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

    private static Object getPropertyValue(BeanWrapper targetWrapper, String subFieldName, FieldDTO field,PermissionHelper permissionHelper) {
        if(permissionHelper.getFieldLevelPermissions().contains(FieldLevelPermission.WRITE)) {
            return targetWrapper.getPropertyValue(subFieldName + field.getFieldName());
        } else if ((!permissionHelper.isPersonalizedClass() || permissionHelper.isSameStaff()) && (!field.getPermissions().contains(FieldLevelPermission.READ)) || field.getPermissions().contains(FieldLevelPermission.HIDE)) {
            return getValueByPropertyType(targetWrapper.getPropertyType(subFieldName + field.getFieldName()));
        }
        return null;
    }

    private static Object getValueByPropertyType(Class<?> propertyType) {
        if(propertyType.isAssignableFrom(List.class)){
            return new ArrayList<>();
        }else if(propertyType.isAssignableFrom(Set.class)){
            return new HashMap<>();
        }else if (propertyType.equals(boolean.class)) {
            return DEFAULT_BOOLEAN;
        } else if (propertyType.equals(byte.class)) {
            return DEFAULT_BYTE;
        } else if (propertyType.equals(short.class)) {
            return DEFAULT_SHORT;
        } else if (propertyType.equals(int.class)) {
            return DEFAULT_INT;
        } else if (propertyType.equals(long.class)) {
            return DEFAULT_LONG;
        } else if (propertyType.equals(float.class)) {
            return DEFAULT_FLOAT;
        } else if (propertyType.equals(double.class)) {
            return DEFAULT_DOUBLE;
        }
        return null;
    }

    public static  <T> List<T> checkAndReturnValidModel(Object[] objects) {
        List<T> validModels = new ArrayList<>();
        if(isNotNull(UserContext.getUserDetails())){
            boolean accessGroupValid = !UserContext.getUserDetails().isHubMember();
            boolean argsValid = objects.length!=0;
            if(accessGroupValid && argsValid){
                validModels = Arrays.stream(objects).filter(arg -> arg.getClass().isAnnotationPresent(KPermissionModel.class) || arg.getClass().isAnnotationPresent(PermissionClass.class)).map(model->(T)model).collect(Collectors.toList());
            }
        }
        return validModels;
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
        private Set<FieldLevelPermission> fieldLevelPermissions;
        private OtherPermissionDTO otherPermissions;
        private boolean personalizedClass;

        public PermissionHelper(ModelDTO modelDTO, Long currentUserStaffId, Map<Long, OtherPermissionDTO> otherPermissionDTOMap, boolean hubMember,Set<FieldLevelPermission> fieldLevelPermissions) {
            this.modelDTO = modelDTO;
            this.currentUserStaffId = currentUserStaffId;
            this.otherPermissionDTOMap = otherPermissionDTOMap;
            this.fieldLevelPermissions = fieldLevelPermissions;
            this.hubMember = hubMember;
        }
    }

}
