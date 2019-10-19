package com.kairos.dto.gdpr.filter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.FilterType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class FilterResponseDTO {


    private FilterType name;

    private List<FilterAttributes> filterData;

    private String displayName;
}
