package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.StaffWorkingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.enums.FilterType.NIGHT_WORKERS;

/**
 * Created By G.P.Ranjan on 10/12/19
 **/
public class NightWorkerFilter implements ShiftFilter {
    private Map<FilterType, Set<String>> filterCriteriaMap;
    private Map<Long, Boolean> nightWorkerMap;

    public NightWorkerFilter(Map<Long, Boolean> nightWorkerMap, Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.nightWorkerMap = nightWorkerMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(NIGHT_WORKERS) && filterCriteriaMap.get(NIGHT_WORKERS).size() == 1;
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            if(filterCriteriaMap.get(NIGHT_WORKERS).contains(StaffWorkingType.NIGHT_WORKER.toString())) {
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if (nightWorkerMap.get(shiftDTO.getStaffId())) {
                        filteredShifts.add((T) shiftDTO);
                    }
                }
            }else{
                for (ShiftDTO shiftDTO : shiftDTOS) {
                    if (!nightWorkerMap.containsKey(shiftDTO.getStaffId()) || !nightWorkerMap.get(shiftDTO.getStaffId())) {
                        filteredShifts.add((T) shiftDTO);
                    }
                }
            }
        }
        return filteredShifts;
    }
}
