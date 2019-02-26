package com.kairos.persistence.model.template_type;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Auther vikash patwal
 */


class TemplateTypeDeprecated {


    @NotBlank(message = "templateName cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }
}