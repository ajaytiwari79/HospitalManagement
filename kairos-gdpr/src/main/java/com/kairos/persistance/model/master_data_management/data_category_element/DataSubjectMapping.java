package com.kairos.persistance.model.master_data_management.data_category_element;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Document(collection = "data_subject_mapping")
public class DataSubjectMapping extends MongoBaseEntity {


    @NotNullOrEmpty(message = "name cannotbe null or empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNullOrEmpty(message = "Description Cannot be empty")
    private String description;

    @NotEmpty(message = "Organization Type cannot be empty")
    @NotNull(message = "Organization Type cannot null")
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    @NotEmpty(message = "Organization Type cannot be empty")
    @NotNull(message = "Organization Sub Type cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

    @NotNull(message = "Data category cannot null")
    @NotEmpty(message = "Data Category cannot be empty")
    private Set<BigInteger> dataCategories;

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

    public DataSubjectMapping(String name, String description) {
        this.name = name;
        this.description = description;

    }

    public DataSubjectMapping() {

    }
}
