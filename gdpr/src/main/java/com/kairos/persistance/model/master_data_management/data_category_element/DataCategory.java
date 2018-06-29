package com.kairos.persistance.model.master_data_management.data_category_element;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Document(collection = "data_category")
public class DataCategory extends MongoBaseEntity {

    @NotNullOrEmpty(message = "Name cannot be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    List<BigInteger> dataElements;

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


}
