package com.kairos.enums;

import com.kairos.user.country.filter.FilterDetailDTO;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by pavan on 14/2/18.
 */
public enum StaffStatusEnum {

    ACTIVE("Active"),INACTIVE("inactive"),FICTIVE("Fictive");
    public String value;

    StaffStatusEnum(String value) {
        this.value = value;
    }

    public static List<FilterDetailDTO> getListOfStaffStatusForFilters(){
        List<FilterDetailDTO> staffStatusFilterData = new ArrayList<>();
        for(StaffStatusEnum staffStatusEnum : EnumSet.allOf(StaffStatusEnum.class)){
            FilterDetailDTO filterDetailDTO = new FilterDetailDTO(staffStatusEnum.name(), staffStatusEnum.value);
            staffStatusFilterData.add(filterDetailDTO);
        }
        return staffStatusFilterData;
    }
}
