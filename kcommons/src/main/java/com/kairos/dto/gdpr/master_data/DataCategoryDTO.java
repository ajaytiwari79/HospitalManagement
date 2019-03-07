package com.kairos.dto.gdpr.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataCategoryDTO {

    private Long id;
    @NotBlank(message = "error.message.name.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNull(message = "error.message.dataelement.notNull")
    @NotEmpty(message = "error.message.dataelement.notNull")
    @Valid
    List<DataElementDTO> dataElements;


    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DataElementDTO> getDataElements() {
        return dataElements;
    }

    public void setDataElements(List<DataElementDTO> dataElements) {
        this.dataElements = dataElements;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
