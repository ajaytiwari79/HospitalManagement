package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum StaffWorkingType {
    NIGHT_WORKER("Night Worker"), NOT_NIGHT_WORKER("Not Night Worker");
    public String value;

    StaffWorkingType(String value) {
        this.value = value;
    }

    public static StaffWorkingType getByValue(String value) {
        for (StaffWorkingType staffWorkingType : StaffWorkingType.values()) {
            if (staffWorkingType.value.equals(value)) {
                return staffWorkingType;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfStaffWorkingTypeForFilters(){
        List<FilterDetailDTO> staffWorkingTypeFilterData = new ArrayList<>();
        for(StaffWorkingType staffWorkingType : EnumSet.allOf(StaffWorkingType.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(staffWorkingType.name(), staffWorkingType.value);
            staffWorkingTypeFilterData.add(filterDetailDTO);
        }
        return staffWorkingTypeFilterData;
    }
}
