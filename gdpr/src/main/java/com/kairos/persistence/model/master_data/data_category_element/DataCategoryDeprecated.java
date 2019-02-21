package com.kairos.persistence.model.master_data.data_category_element;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


class DataCategoryDeprecated {

    @NotBlank(message = "Name cannot be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    // empty array to get rid of null pointer
    private List<BigInteger> dataElements=new ArrayList<>();

    private Long countryId;

    public List<BigInteger> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<BigInteger> dataElements) {
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

    public DataCategoryDeprecated(@NotBlank(message = "Name cannot be empty")
                         @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
                                 String name, List<BigInteger> dataElements) {
        this.name = name;
        this.dataElements = dataElements;
    }



    public DataCategoryDeprecated() {
    }

    public DataCategoryDeprecated(String name) {
        this.name = name;
    }
}
