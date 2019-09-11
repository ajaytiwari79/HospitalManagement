package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationTypeAndSubTypeIdDTO {

    private Long countryId;
    private List<Long> organizationTypeId;
    private List<Long> organizationSubTypeIds;
    private List<Long> serviceCategoryIds;
    private List<Long> subServiceCategoryIds;

    public OrganizationTypeAndSubTypeIdDTO(List<Long> organizationTypeId, List<Long> organizationSubTypeIds, List<Long> serviceCategoryIds, List<Long> subServiceCategoryIds) {
        this.organizationTypeId = organizationTypeId;
        this.organizationSubTypeIds = organizationSubTypeIds;
        this.serviceCategoryIds = serviceCategoryIds;
        this.subServiceCategoryIds = subServiceCategoryIds;
    }

}
