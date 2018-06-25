package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSubjectMappingDTO {

    @NotNullOrEmpty(message = "name  can't be empty")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNullOrEmpty(message = "description  can't be empty")
    private String description;

    @NotNull(message = "Organization  Type  can't be  null")
    @NotEmpty(message = "Oraganization Type  can't be  empty")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    @NotNull(message = "Organization Sub Type  can't be  null")
    @NotEmpty(message = "Organization Sub Type   can't be  empty")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;

    @NotEmpty(message = "DataCategorie  can't be  empty")
    @NotNull(message = "Data category  can't be  null")
    private Set<BigInteger> dataCategories;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<BigInteger> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Set<BigInteger> dataCategories) {
        this.dataCategories = dataCategories;
    }
}
