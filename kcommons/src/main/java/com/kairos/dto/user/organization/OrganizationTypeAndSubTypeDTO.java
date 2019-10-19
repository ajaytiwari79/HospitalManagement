package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by vipul on 8/9/17.
 */
@Getter
@Setter
public class OrganizationTypeAndSubTypeDTO {
    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;
    private Long unitId;
    private boolean isParent=false;
    private Long parentOrganizationId;
}
