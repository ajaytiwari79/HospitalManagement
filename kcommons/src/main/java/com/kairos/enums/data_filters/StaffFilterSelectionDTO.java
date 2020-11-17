package com.kairos.enums.data_filters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffFilterSelectionDTO<T> {

    private StaffFilters name;
    private Set<T> value;

    public StaffFilterSelectionDTO(StaffFilters name, Set<T> value) {
        this.name = name;
        this.value = value;
    }

    public String toString(){
        return this.name.value + " and value "+this.getValue();
    }
}
