package com.kairos.response.dto.master_data.data_mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigInteger;


@JsonIgnoreProperties(ignoreUnknown = true)
public class DataElementBasicResponseDTO {

    private Long id;

    private String name;

    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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

    public DataElementBasicResponseDTO() {
    }
}
