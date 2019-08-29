package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum Employment {
    STAFF_WITH_EMPLOYMENT("Staff with Employment"),
    STAFF_WITHOUT_EMPLOYMENT("Staff without Employment");
    public String value;

    Employment(String value) {
        this.value = value;
    }

    public static Employment getByValue(String value) {
        for (Employment employment : Employment.values()) {
            if (employment.value.equals(value)) {
                return employment;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfEmploymentForFilters(){
        List<FilterDetailDTO> employmentFilterData = new ArrayList<>();
        for(Employment employment : EnumSet.allOf(Employment.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(employment.name(), employment.value);
            employmentFilterData.add(filterDetailDTO);
        }
        return employmentFilterData;
    }
}
