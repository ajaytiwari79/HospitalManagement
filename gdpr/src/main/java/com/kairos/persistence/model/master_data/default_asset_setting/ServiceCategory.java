package com.kairos.persistence.model.master_data.default_asset_setting;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Embeddable
public class ServiceCategory {

    @NotNull(message = "id can't be null")
    private Long id;

    @NotBlank(message = "name can't be null or empty")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceCategory() {
    }

    public ServiceCategory(@NotNull(message = "id can't be null") Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
