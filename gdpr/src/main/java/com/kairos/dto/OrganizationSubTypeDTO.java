package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganizationSubTypeDTO {

    @NotNull(message = "id can't be null")
    private Long id;

    @NotBlank(message = "Name can't be empty")
    private String name;

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

    public OrganizationSubTypeDTO() {
    }

    public OrganizationSubTypeDTO(@NotNull(message = "id can't be null") Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
