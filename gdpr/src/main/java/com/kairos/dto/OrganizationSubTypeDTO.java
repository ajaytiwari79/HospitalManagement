package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.utils.custom_annotation.NotNullOrEmpty;
import org.javers.core.metamodel.annotation.ValueObject;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@ValueObject
public class OrganizationSubTypeDTO {

    @NotNull(message = "id can't be null")
    private Long id;

    @NotNullOrEmpty(message = "name can't be null or empty")
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
}
