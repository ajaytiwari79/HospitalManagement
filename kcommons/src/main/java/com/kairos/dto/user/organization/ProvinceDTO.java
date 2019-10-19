package com.kairos.dto.user.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDTO {
    private Long id;
    private String name;
    private RegionDTO region;
}
