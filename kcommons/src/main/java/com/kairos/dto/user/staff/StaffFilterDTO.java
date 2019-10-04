package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.enums.FilterType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.FilterType.*;

/**
 * Created by Jasgeet on 13/10/17.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class StaffFilterDTO {
    private String moduleId;
    private String filterJson;
    private List<FilterSelectionDTO> filtersData;
    private long id;
    private String searchText;
    private String name;
    private List<Long> staffIds;
    private List<BigInteger> planningPeriodIds;

    public void setFiltersData(List<FilterSelectionDTO> filtersData) {
        this.filtersData = isNullOrElse(filtersData,new ArrayList<>());
    }

    public boolean isValidFilterForShift(){
        boolean isValidFilterForShift = false;
        Set<FilterType> filterTypeSet = newHashSet(TIME_TYPE,ACTIVITY_TIMECALCULATION_TYPE,ACTIVITY_STATUS,TIME_SLOT,ABSENCE_ACTIVITY,VALIDATED_BY,PLANNED_TIME_TYPE);
        for (FilterSelectionDTO filterSelectionDTO : this.getFiltersData()) {
            isValidFilterForShift = isCollectionNotEmpty(filterSelectionDTO.getValue()) && filterTypeSet.contains(filterSelectionDTO.getName());
            if(isValidFilterForShift){
                break;
            }
        }
        return isValidFilterForShift;
    }
}

