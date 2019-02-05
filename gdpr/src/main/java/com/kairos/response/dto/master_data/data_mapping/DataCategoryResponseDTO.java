package com.kairos.response.dto.master_data.data_mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryResponseDTO {

    private Long id;

    private String name;

    public DataCategoryResponseDTO(Long id, String name, List<DataElementBasicResponseDTO> dataElements) {
        this.id = id;
        this.name = name;
        this.dataElements = dataElements;
    }

    private List<DataElementBasicResponseDTO> dataElements=new ArrayList<>();

    public List<DataElementBasicResponseDTO> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementBasicResponseDTO> dataElements) {
        this.dataElements = dataElements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
