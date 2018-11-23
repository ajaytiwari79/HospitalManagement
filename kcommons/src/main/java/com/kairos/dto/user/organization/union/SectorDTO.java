package com.kairos.dto.user.organization.union;

import com.kairos.commons.utils.NotNullOrEmpty;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;

public class SectorDTO {
    @NotEmpty
    private String name;
    private Long id;

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
        this.name = StringUtils.trim(name);
    }
}