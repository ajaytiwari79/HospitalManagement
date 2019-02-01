package com.kairos.persistence.model.master_data.data_category_element;


import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Entity
public class DataElement extends BaseEntity {


    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    private Long countryId;

   /* @ManyToOne
    @JoinColumn(name="dataCategory_id")
    private DataCategory dataCategory;*/

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

    public DataElement(String name, Long countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public DataElement(String name) {
        this.name = name;
    }

    public DataElement() {
    }

}
