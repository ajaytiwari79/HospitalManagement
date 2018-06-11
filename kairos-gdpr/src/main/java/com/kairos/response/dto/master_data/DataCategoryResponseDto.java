package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryResponseDto {

    private BigInteger id;

    private String name;

    private Long countryId;

    private List<DataElementBasicResponseDto> dataElements=new ArrayList<>();

    public List<DataElementBasicResponseDto> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementBasicResponseDto> dataElements) {
        this.dataElements = dataElements;
    }

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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public DataCategoryResponseDto( ){

    }
}
