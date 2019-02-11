package com.kairos.persistence.model.master_data.data_category_element;


import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.OrganizationSubType;
import com.kairos.persistence.model.embeddables.OrganizationType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataSubjectMapping extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;
    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;
    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();
    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();
    @OneToMany(fetch = FetchType.EAGER)
    private List<DataCategory> dataCategories=new ArrayList<>();
    private Long countryId;
    private Long organizationId;



    public DataSubjectMapping() {
    }

    public DataSubjectMapping(String name, String description, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes,
                              List<DataCategory> dataCategories) {
        this.name = name;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.dataCategories = dataCategories;
    }


    public DataSubjectMapping(@NotBlank(message = "error.message.name.notNull.orEmpty")
                               @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description, @NotEmpty List<DataCategory> dataCategories) {
        this.name = name;
        this.description = description;
        this.dataCategories = dataCategories;
    }

    public DataSubjectMapping(@NotBlank(message = "Name can't be null or empty") @Pattern(message = "Numbers and Special characters are not allowed in Name", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description) {
        this.name = name;
        this.description=description;
    }

    public Long getOrganizationId() { return organizationId; }

    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public List<DataCategory> getDataCategories() {
        return dataCategories;
    }

    public void setDataCategories(List<DataCategory> dataCategories) {
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


}
