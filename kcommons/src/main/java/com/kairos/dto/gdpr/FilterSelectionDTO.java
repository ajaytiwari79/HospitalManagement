package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FilterSelectionDTO {


    @NotEmpty(message = "error.message.selection.list.notNull")
    private List<FilterSelection> filtersData;

    private String moduleId;
}
