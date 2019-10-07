package com.kairos.enums;

import com.kairos.dto.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum RealTimeStatus {
    UPCOMING("Upcoming"), CURRENTLY_WORKING("Currently Working"), ON_BREAK("On Break"), ON_LEAVE("On Leave"), RESTING("Resting"), SICK("Sick,");
    public String value;

    RealTimeStatus(String value) {
        this.value = value;
    }

    public static RealTimeStatus getByValue(String value) {
        for (RealTimeStatus realTimeStatus : RealTimeStatus.values()) {
            if (realTimeStatus.value.equals(value)) {
                return realTimeStatus;
            }
        }
        return null;
    }

    public static List<FilterDetailDTO> getListOfRealtimeStatusForFilters(){
        List<FilterDetailDTO> realTimeStatusFilterData = new ArrayList<>();
        for(RealTimeStatus realTimeStatus : EnumSet.allOf(RealTimeStatus.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(realTimeStatus.name(), realTimeStatus.value);
            realTimeStatusFilterData.add(filterDetailDTO);
        }
        return realTimeStatusFilterData;
    }
}
