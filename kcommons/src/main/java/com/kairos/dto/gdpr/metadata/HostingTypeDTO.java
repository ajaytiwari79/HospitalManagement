package com.kairos.dto.gdpr.metadata;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HostingTypeDTO {

    private Long id;

    @NotBlank(message = "Name can't be empty")
    @Pattern(message = "Number and Special characters are not allowed for Name",regexp = "^[a-zA-Z\\s]+$")
    private String name;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getName() { return name.trim(); }

    public void setName(String name) { this.name = name; }
}
