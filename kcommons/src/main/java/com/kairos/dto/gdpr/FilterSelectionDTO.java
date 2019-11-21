package com.kairos.dto.gdpr;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.FilterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class FilterSelectionDTO<T> {

    private Long id;
    @NotEmpty(message = "error.message.selection.list.notNull")
    private List<FilterSelection> filtersData;
    private FilterType name;
    private Set<T> value;


    private String moduleId;

    public FilterSelectionDTO(FilterType name, Set<T> value) {
        this.name = name;
        this.value = value;
    }
}
