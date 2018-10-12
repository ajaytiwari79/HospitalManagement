package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum UnitPosition {
    STAFF_WITH_UNIT_POSITION("Staff with Unit Position");
    public String value;

    UnitPosition(String value) {
        this.value = value;
    }

    public static UnitPosition getByValue(String value) {
        for (UnitPosition unitPosition : UnitPosition.values()) {
            if (unitPosition.value.equals(value)) {
                return unitPosition;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfUnitPositionForFilters(){
        List<FilterDetailDTO> unitPositionFilterData = new ArrayList<>();
        for(UnitPosition unitPosition : EnumSet.allOf(UnitPosition.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(unitPosition.name(), unitPosition.value);
            unitPositionFilterData.add(filterDetailDTO);
        }
        return unitPositionFilterData;
    }
}
