package com.kairos.dto.user.organization;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MunicipalityDTO {

    private Long id;
    private String name;
    private ProvinceDTO province;
}
