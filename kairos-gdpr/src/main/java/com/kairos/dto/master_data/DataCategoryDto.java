package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistance.model.master_data_management.data_category_element.DataElement;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryDto {


    @NotNullOrEmpty(message = "error.name.cannotbe.empty.or.null")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @Valid
    List<DataElement> dataElements;

    private Long countryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
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
}
