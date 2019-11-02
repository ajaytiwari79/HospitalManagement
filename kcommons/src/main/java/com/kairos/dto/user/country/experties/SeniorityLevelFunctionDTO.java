package com.kairos.dto.user.country.experties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SeniorityLevelFunctionDTO {
    private Long seniorityLevelId;
    private Integer from; // added these 2 fields just FE needs them
    private Integer to;

    private List<FunctionsDTO> functions;
}
