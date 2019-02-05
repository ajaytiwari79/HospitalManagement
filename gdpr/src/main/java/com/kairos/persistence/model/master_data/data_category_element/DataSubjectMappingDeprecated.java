package com.kairos.persistence.model.master_data.data_category_element;


import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DataSubjectMappingDeprecated {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    private List<OrganizationTypeDTO> organizationTypeDTOS;

    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;

    private Set<BigInteger> dataCategories = new HashSet<>();

    private Long countryId;


    public DataSubjectMappingDeprecated() {
    }

    public DataSubjectMappingDeprecated(String name, String description, List<OrganizationTypeDTO> organizationTypeDTOS, List<OrganizationSubTypeDTO> organizationSubTypeDTOS,
                                        Set<BigInteger> dataCategories) {
        this.name = name;
        this.description = description;
        this.organizationTypeDTOS = organizationTypeDTOS;
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
        this.dataCategories = dataCategories;
    }


    public DataSubjectMappingDeprecated(@NotBlank(message = "error.message.name.notNull.orEmpty")
                               @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description, @NotEmpty Set<BigInteger> dataCategories) {
        this.name = name;
        this.description = description;
        this.dataCategories = dataCategories;
    }

    public DataSubjectMappingDeprecated(@NotBlank(message = "Name can't be null or empty") @Pattern(message = "Numbers and Special characters are not allowed in Name", regexp = "^[a-zA-Z\\s]+$") String name, @NotBlank(message = "error.message.description.notNull.orEmpty") String description) {
        this.name = name;
        this.description=description;
    }

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

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public void setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) {
        this.organizationTypeDTOS = organizationTypeDTOS;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) {
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
    }


}
