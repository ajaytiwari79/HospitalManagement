package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrgTypeAndSubTypeDTO {
    private Long organizationTypeId;
    private String organizationTypeName;
    private Long organizationSubTypeId;
    private String organizationSubTypeName;
    private List<Long> subTypeId; // same as above but its list We will change
    private Long countryId;
    private Long parentOrganizationId;
    private boolean workcentre;
    private boolean isParentOrganization;
    private List<Long> employmentTypeIds;

    public OrgTypeAndSubTypeDTO() {
        //Default Constructor
    }

    public OrgTypeAndSubTypeDTO(Long countryId, Long parentOrganizationId) {
        this.countryId = countryId;
        this.parentOrganizationId = parentOrganizationId;
    }

    public OrgTypeAndSubTypeDTO(Long organizationTypeId, Long organizationSubTypeId, Long countryId) {
        this.organizationTypeId = organizationTypeId;
        this.organizationSubTypeId = organizationSubTypeId;
        this.countryId = countryId;
    }

    public OrgTypeAndSubTypeDTO(Long organizationTypeId, List<Long> subTypeId, Long countryId,boolean isParentOrganization) {
        this.organizationTypeId = organizationTypeId;
        this.subTypeId = subTypeId;
        this.countryId = countryId;
        this.isParentOrganization=isParentOrganization;
    }

}
