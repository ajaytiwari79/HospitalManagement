package com.kairos.persistence.model.master_data.data_category_element;

import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataCategory extends BaseEntity {

    @NotBlank(message = "Name cannot be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    // empty array to get rid of null pointer
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "data_category_id")
    private List<DataElement> dataElements=new ArrayList<>();
    private Long countryId;
    private Long organizationId;

    public DataCategory(@NotBlank(message = "Name cannot be empty")
                        @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
                                String name, List<DataElement> dataElements) {
        this.name = name;
        this.dataElements = dataElements;
    }

    public DataCategory(@NotBlank(message = "Name cannot be empty") @Pattern(message = "Numbers and Special characters are not allowed in Name", regexp = "^[a-zA-Z\\s]+$") String name, List<DataElement> dataElements, Long organizationId) {
        this.name = name;
        this.dataElements = dataElements;
        this.organizationId = organizationId;
    }

    public DataCategory() {
    }

    public DataCategory(String name) {
        this.name = name;
    }


    public List<DataElement> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElement> dataElements) {
        this.dataElements = dataElements;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


}
