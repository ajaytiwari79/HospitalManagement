package com.kairos.enums.shift;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.*;

public enum ShiftDeletedBy {
    STAFF("Staff"), MANAGEMENT("Management"), SYSTEM("System");
    private String shiftDeletedBy;

    ShiftDeletedBy() {}

    ShiftDeletedBy(String accessGroupRole) {
        this.shiftDeletedBy = accessGroupRole;
    }

    public static Set<ShiftDeletedBy> getAllShiftDeleteBy() {
        return new HashSet<>(EnumSet.allOf(ShiftDeletedBy.class));
    }

    public static List<FilterDetailDTO> getListOfShiftDeleteByForFilters(){
        List<FilterDetailDTO> shiftDeletedByFilterData = new ArrayList<>();
        for(ShiftDeletedBy shiftDeletedBy : EnumSet.allOf(ShiftDeletedBy.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(shiftDeletedBy.name(), shiftDeletedBy.shiftDeletedBy);
            shiftDeletedByFilterData.add(filterDetailDTO);
        }
        return shiftDeletedByFilterData;
    }
}
