package com.kairos.dto.gdpr.filter;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@NoArgsConstructor
public class FilterCategoryResult {

    private List<FilterAttributes> organizationTypes = new ArrayList<>();
    private List<FilterAttributes> organizationSubTypes = new ArrayList<>();
    private List<FilterAttributes> organizationServices = new ArrayList<>();
    private List<FilterAttributes> organizationSubServices = new ArrayList<>();
    private List<FilterAttributes> accountTypes = new ArrayList<>();

}

