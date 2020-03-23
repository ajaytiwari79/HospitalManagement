package com.kairos.dto.kpermissions;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class FieldPermissionHelperDTO<T , E> {
    private List<T> objects;
    private Map<String, ModelDTO> modelMap;
    private Map<Long, E> mapOfDataBaseObject;
    private Map<Long, OtherPermissionDTO> otherPermissionDTOMap;
    private Long currentUserStaffId;
    private boolean hubMember;
    private Long staffId;
}
