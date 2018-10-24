package com.kairos.persistence.model.master_data.data_category_element;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document
public class DataSubjectMapping extends MongoBaseEntity {


    @NotBlank(message = "Name can't be null or empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "Description Cannot be empty")
    private String description;

    @NotEmpty(message = "ManagingOrganization Type cannot be empty")
    @NotNull(message = "ManagingOrganization Type cannot null")
    private List<OrganizationType> organizationTypes;

    @NotEmpty(message = "ManagingOrganization Type cannot be empty")
    @NotNull(message = "ManagingOrganization Sub Type cannot be empty")
    private List<OrganizationSubType> organizationSubTypes;

    @NotNull(message = "Data category cannot null")
    @NotEmpty(message = "Data Category cannot be empty")
    // empty set to get rid of null pointer exception
    private Set<BigInteger> dataCategories=new HashSet<>();

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Set<BigInteger> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(Set<BigInteger> dataCategories) {
        this.dataCategories = dataCategories;
    }

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

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public DataSubjectMapping(String name, String description, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes,
                              Set<BigInteger> dataCategories) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.dataCategories = dataCategories;
    }

    public DataSubjectMapping() {
    }


    public DataSubjectMapping(String name) {
        this.name = name;
    }
}
