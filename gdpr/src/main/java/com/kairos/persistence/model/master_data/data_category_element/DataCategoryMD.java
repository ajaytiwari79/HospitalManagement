package com.kairos.persistence.model.master_data.data_category_element;

import com.kairos.persistence.model.common.BaseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataCategoryMD extends BaseEntity {

    @NotBlank(message = "Name cannot be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    // empty array to get rid of null pointer
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DataElementMD> dataElements=new ArrayList<>();

    private Long countryId;

    public List<DataElementMD> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementMD> dataElements) {
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

    public DataCategoryMD(@NotBlank(message = "Name cannot be empty")
                         @Pattern(message = "Numbers and Special characters are not allowed in Name",regexp = "^[a-zA-Z\\s]+$")
                                 String name, List<DataElementMD> dataElements) {
        this.name = name;
        this.dataElements = dataElements;
    }



    public DataCategoryMD() {
    }

    public DataCategoryMD(String name) {
        this.name = name;
    }
}
