package com.kairos.dto.kpermissions;

import com.kairos.enums.OrganizationCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class ModelDTO {

    private Long id;
    private String modelName;
    private String modelClass;
    private boolean isPermissionSubModel;
    private List<FieldDTO> fields = new ArrayList<>();
    private List<ModelDTO> subModels = new ArrayList<>();
    private Set<OrganizationCategory> organizationCategories;


}
