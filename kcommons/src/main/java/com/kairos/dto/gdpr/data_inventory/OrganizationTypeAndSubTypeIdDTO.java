package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationTypeAndSubTypeIdDTO {

    private Long countryId;
    private List<Long> organizationTypeId;
    private List<Long> organizationSubTypeIds;
    private List<Long> serviceCategoryIds;
    private List<Long> subServiceCategoryIds;

    public OrganizationTypeAndSubTypeIdDTO() {
    }

    public OrganizationTypeAndSubTypeIdDTO(List<Long> organizationTypeId, List<Long> organizationSubTypeIds, List<Long> serviceCategoryIds, List<Long> subServiceCategoryIds) {
        this.organizationTypeId = organizationTypeId;
        this.organizationSubTypeIds = organizationSubTypeIds;
        this.serviceCategoryIds = serviceCategoryIds;
        this.subServiceCategoryIds = subServiceCategoryIds;
    }

    public List<Long> getOrganizationTypeId() { return organizationTypeId; }

    public void setOrganizationTypeId(List<Long> organizationTypeId) { this.organizationTypeId = organizationTypeId; }

    public List<Long> getOrganizationSubTypeIds() { return organizationSubTypeIds; }

    public void setOrganizationSubTypeIds(List<Long> organizationSubTypeIds) { this.organizationSubTypeIds = organizationSubTypeIds; }

    public List<Long> getServiceCategoryIds() { return serviceCategoryIds; }

    public void setServiceCategoryIds(List<Long> serviceCategoryIds) { this.serviceCategoryIds = serviceCategoryIds; }

    public List<Long> getSubServiceCategoryIds() { return subServiceCategoryIds; }

    public void setSubServiceCategoryIds(List<Long> subServiceCategoryIds) { this.subServiceCategoryIds = subServiceCategoryIds; }

    public Long getCountryId() { return countryId; }

    public void setCountryId(Long countryId) { this.countryId = countryId; }
}
