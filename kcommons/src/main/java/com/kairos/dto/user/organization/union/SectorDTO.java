package com.kairos.dto.user.organization.union;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
@Getter
@Setter
@NoArgsConstructor
public class SectorDTO {
    @NotBlank(message = "error.name.notnull")
    private String name;
    private Long id;

    public SectorDTO(Long id, @NotBlank String name) {
        this.name = name;
        this.id = id;
    }


    public void setName(String name) {
        this.name = StringUtils.trim(name);
    }
}