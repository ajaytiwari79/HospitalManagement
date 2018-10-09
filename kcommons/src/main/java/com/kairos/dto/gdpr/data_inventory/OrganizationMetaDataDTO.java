package com.kairos.dto.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationMetaDataDTO {


    private Long typeId;

    private List<Long> subTypeIds;

    private List<Long> serviceCategoryIds;

    private List<Long> subServiceCategoryIds;

    public Long getTypeId() { return typeId; }

    public void setTypeId(Long typeId) { this.typeId = typeId; }

    public List<Long> getSubTypeIds() { return subTypeIds; }

    public void setSubTypeIds(List<Long> subTypeIds) { this.subTypeIds = subTypeIds; }

    public List<Long> getServiceCategoryIds() { return serviceCategoryIds; }

    public void setServiceCategoryIds(List<Long> serviceCategoryIds) { this.serviceCategoryIds = serviceCategoryIds; }

    public List<Long> getSubServiceCategoryIds() { return subServiceCategoryIds; }

    public void setSubServiceCategoryIds(List<Long> subServiceCategoryIds) { this.subServiceCategoryIds = subServiceCategoryIds; }
}
