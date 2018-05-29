package com.kairos.response.dto.filter;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterDetail {


    @NotNull
    private String moduleId;

    @NotNull
    private Long countryId;



}
