package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryDTO {

    private BigInteger bigInteger;
    @NotBlank(message = "Name  can't be  Empty")
    @Pattern(message = "Number and Special characters are not allowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNull(message = "Data Element can't be  Empty")
    @NotEmpty(message = "Data Element can't be empty")
    @Valid
    List<DataElementDTO> dataElements;


    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataElementDTO> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementDTO> dataElements) {
        this.dataElements = dataElements;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }
}
