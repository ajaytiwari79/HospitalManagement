package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.gdpr.FilterType;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FilterSelection {

    @NotBlank(message = "error.message.filter.category.notNull")
    private FilterType name;

    @NotBlank(message = "error.message.value.notNull")
    private List<Long> value;

}

