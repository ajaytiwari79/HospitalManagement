package com.kairos.persistence.model.master_data.data_category_element;


import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Document(collection = "data_element")
public class DataElement extends MongoBaseEntity {


    @NotBlank(message = "Name can't be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name", regexp = "^[a-zA-Z\\s]+$")
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

    public DataElement( String name, Long countryId) {
        this.name = name;
        this.countryId = countryId;
    }

    public DataElement( String name) {
        this.name = name;
    }

    public DataElement() {
    }
}
