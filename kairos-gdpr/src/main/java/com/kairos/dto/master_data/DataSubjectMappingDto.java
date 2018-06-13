package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSubjectMappingDto {

    @NotNullOrEmpty(message = "name cannot be null or empty")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNullOrEmpty(message = "description cannotbe null or empty")
    private String description;

    @NotEmpty(message = "oraganization type list cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    @NotEmpty(message = "organization Sub Types list cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    @NotEmpty(message = "dataCategories list cannot be empty")
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

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<BigInteger> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Set<BigInteger> dataCategories) {
        this.dataCategories = dataCategories;
    }
}
