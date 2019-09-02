package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
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
    private List<Long> employmentTypeIds=new ArrayList<>();


    public OrgTypeAndSubTypeDTO(Long countryId, Long parentOrganizationId) {
        this.countryId = countryId;
        this.parentOrganizationId = parentOrganizationId;
    }



    public OrgTypeAndSubTypeDTO(Long organizationTypeId, List<Long> subTypeId, Long countryId,boolean isParentOrganization,List<Long> employmentTypeIds) {
        this.organizationTypeId = organizationTypeId;
        this.subTypeId = subTypeId;
        this.countryId = countryId;
        this.isParentOrganization=isParentOrganization;
        this.employmentTypeIds=employmentTypeIds;
    }

}
