package com.kairos.gdpr.data_inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.master_data.DataCategoryDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationDataSubjectDTO {

    @NotEmpty(message = "Data Subject name Can't be Empty")
    private Set<String> dataSubjectNames;

    @Valid
    private List<DataCategoryDTO> dataCategories;

    public Set<String> getDataSubjectNames() { return dataSubjectNames; }

    public void setDataSubjectNames(Set<String> dataSubjectNames) { this.dataSubjectNames = dataSubjectNames; }

    public List<DataCategoryDTO> getDataCategories() { return dataCategories; }

    public void setDataCategories(List<DataCategoryDTO> dataCategories) { this.dataCategories = dataCategories; }

}
