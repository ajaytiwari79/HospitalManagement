package com.kairos.dto.user.organization;

import javax.validation.constraints.NotBlank;

public class ProvinceDTO {
    private Long id;
    private String name;
    private RegionDTO region;

    public ProvinceDTO() {

    }
    public ProvinceDTO(Long id, String name, RegionDTO region) {
        this.id = id;
        this.name= name;
        this.region = region;

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

    public RegionDTO getRegion() {
        return region;
    }

    public void setRegion(RegionDTO region) {
        this.region = region;
    }
}
