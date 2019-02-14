package com.kairos.response.dto.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class MetaDataCommonResponseDTO {

    private Long id;

    private String name;


    public MetaDataCommonResponseDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public MetaDataCommonResponseDTO() {
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
