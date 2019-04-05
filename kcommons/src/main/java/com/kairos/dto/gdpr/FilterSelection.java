package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.FilterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FilterSelection {

    @NotBlank(message = "Filter Category name cannot be empty")
    private FilterType name;

    @NotEmpty(message = "Value can't be Empty")
    @NotNull(message = "Value can't be  Null")
    private List<Long> value;

}

