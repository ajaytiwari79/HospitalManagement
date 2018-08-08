package com.kairos.persistance.model.master_data.data_category_element;

import com.kairos.persistance.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;

@Document(collection = "data_category")
public class DataCategory extends MongoBaseEntity {

    @NotBlank(message = "Name cannot be empty")
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

    public DataCategory(String name, List<BigInteger> dataElements, Long countryId) {
        this.name = name;
        this.dataElements = dataElements;
        this.countryId = countryId;
    }



    public DataCategory() {
    }

    public DataCategory(String name) {
        this.name = name;
    }
}
