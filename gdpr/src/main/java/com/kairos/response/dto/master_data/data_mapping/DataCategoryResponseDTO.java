package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryResponseDTO {

    private BigInteger id;

    private String name;

    private List<DataElementBasicResponseDTO> dataElements=new ArrayList<>();

    public List<DataElementBasicResponseDTO> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementBasicResponseDTO> dataElements) {
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


    public DataCategoryResponseDTO( ){

    }
}
