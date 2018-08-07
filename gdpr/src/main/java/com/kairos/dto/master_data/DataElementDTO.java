package com.kairos.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;

import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataElementDTO {


    private BigInteger id;

    @NotNullOrEmpty(message = "Name can't be empty")
    @Pattern(message = "Numbers and Special characters are not allowed in Name", regexp = "^[a-zA-Z\\s]+$")
    private String name;


    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
