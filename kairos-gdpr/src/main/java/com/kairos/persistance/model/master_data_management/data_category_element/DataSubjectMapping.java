package com.kairos.persistance.model.master_data_management.data_category_element;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Document(collection = "data_subject_mapping")
public class DataSubjectMapping extends MongoBaseEntity {


    @NotNullOrEmpty(message = "name cannotbe null or empty")
    private String name;

    @NotNullOrEmpty(message = "description cannotbe null or empty")
    private String description;

    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;

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
