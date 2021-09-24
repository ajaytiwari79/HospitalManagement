package com.kairos.persistence.model.kpermissions;

import com.kairos.enums.OrganizationCategory;
import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.neo4j.ogm.annotation.NodeEntity;

import javax.validation.constraints.NotBlank;
import java.util.Set;

import static com.kairos.constants.UserMessagesConstants.ERROR_NAME_NOTNULL;

@Getter
@Setter
@NoArgsConstructor
@NodeEntity
public class KPermissionField extends UserBaseEntity {

    @NotBlank(message = ERROR_NAME_NOTNULL)
    private String fieldName;

    private Set<OrganizationCategory> organizationCategories;


    public KPermissionField(@NotBlank(message = ERROR_NAME_NOTNULL) String fieldName, Set<OrganizationCategory> organizationCategories) {
        this.fieldName = fieldName;
        this.organizationCategories = organizationCategories;
    }
}
