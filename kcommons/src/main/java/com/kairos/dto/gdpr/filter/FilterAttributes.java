package com.kairos.dto.gdpr.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterAttributes {


    @NotNull
    private Long id;

    @NotNull
    private String name;


}
