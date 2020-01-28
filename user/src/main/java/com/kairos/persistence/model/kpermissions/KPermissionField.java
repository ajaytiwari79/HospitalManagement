package com.kairos.persistence.model.kpermissions;

import com.kairos.dto.kpermissions.OtherPermissionDTO;
import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.access_permission.AccessGroup;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;
import static com.kairos.persistence.model.constants.RelationshipConstants.HAS_PERMISSION;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class KPermissionField extends UserBaseEntity {

    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String fieldName;

    @Relationship(type = HAS_PERMISSION)
    private List<AccessGroup> accessGroups = new ArrayList<>();

    private Set<OrganizationCategory> organizationCategories;


    public KPermissionField(@NotBlank(message = ERROR_NAME_NOTNULL) String fieldName, Set<OrganizationCategory> organizationCategories) {
        this.fieldName = fieldName;
        this.organizationCategories = organizationCategories;
    }
}
