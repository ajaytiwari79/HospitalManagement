package com.kairos.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManagingOrganization {


    @NotNull
    private Long id;

    @NotBlank(message = "ManagingOrganization name can't be empty")
    private String name;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }


    public ManagingOrganization(Long id, String name, String email) {
        this.id = id;
        this.name = name;
    }

    public ManagingOrganization() {
    }
}
