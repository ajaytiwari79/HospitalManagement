package com.kairos.persistence.model.master_data.data_category_element;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


public class DataElementDeprecated {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

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

    public DataElementDeprecated(String name, Long countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public DataElementDeprecated(String name) {
        this.name = name;
    }

    public DataElementDeprecated() {
    }
}
