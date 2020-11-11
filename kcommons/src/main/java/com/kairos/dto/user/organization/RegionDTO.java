package com.kairos.dto.user.organization;

import com.kairos.dto.TranslationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegionDTO {

    private String name;
    private Long id;
    private String geoFence;
    private String code;
    private Double latitude;
    private Double Longitude;
    private Map<String, TranslationInfo> translations;

    public RegionDTO(String name, Long id) {
        this.name = name;
        this.id = id;
    }

}
