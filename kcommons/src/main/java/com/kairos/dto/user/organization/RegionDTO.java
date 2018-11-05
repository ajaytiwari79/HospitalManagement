package com.kairos.dto.user.organization;

import javax.validation.constraints.NotBlank;

public class RegionDTO {

    private String name;
    private Long id;

    public RegionDTO() {

    }

    public RegionDTO(Long id, String name) {

        this.id = id;
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
