package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FilterSelectionDTO {


    @NotEmpty(message = "Selection List cannot Empty")
    private List<FilterSelection> filtersData;

    private String moduleId;
}
