package com.kairos.dto.planner.constarints.unit;

import com.kairos.dto.planner.constarints.ConstraintDTO;

public class UnitConstraintDTO extends ConstraintDTO {
    //~
    private Long organizationId;
    private Long parentOrganizationConstraintId;
   

    //======================================================

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getParentOrganizationConstraintId() {
        return parentOrganizationConstraintId;
    }

    public void setParentOrganizationConstraintId(Long parentOrganizationConstraintId) {
        this.parentOrganizationConstraintId = parentOrganizationConstraintId;
    }

    
}
