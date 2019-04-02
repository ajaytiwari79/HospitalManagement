package com.kairos.dto.user.organization.union;

import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;

public class SectorDTO {
    @NotBlank
    private String name;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SectorDTO() {
    }

    public SectorDTO(Long id,@NotBlank String name) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name.trim();
    }

    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }
}