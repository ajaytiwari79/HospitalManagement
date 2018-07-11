package com.kairos.persistance.model.master_data.data_category_element;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Pattern;

@Document(collection = "data_element")
public class DataElement extends MongoBaseEntity {


    @NotNullOrEmpty(message = "Name can't be empty")
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

    public DataElement() {
    }
}
