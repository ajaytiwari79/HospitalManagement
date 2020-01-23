package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.StaffWorkingType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kairos.enums.FilterType.NIGHT_WORKERS;

/**
 * Created By G.P.Ranjan on 10/12/19
 **/
public class NightWorkerFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Map<Long, Boolean> nightWorkerMap;

    public NightWorkerFilter(Map<Long, Boolean> nightWorkerMap, Map<FilterType, Set<G>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.nightWorkerMap = nightWorkerMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(NIGHT_WORKERS) && filterCriteriaMap.get(NIGHT_WORKERS).size() == 1;
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            Set<Long> staffIds = filterCriteriaMap.get(NIGHT_WORKERS).contains(StaffWorkingType.NIGHT_WORKER.toString()) ? nightWorkerMap.keySet().stream().filter(k->nightWorkerMap.get(k)).collect(Collectors.toSet()) : nightWorkerMap.keySet().stream().filter(k->!nightWorkerMap.containsKey(k) || !nightWorkerMap.get(k)).collect(Collectors.toSet());
            return shiftDTOS.stream().filter(k->staffIds.contains(k.getStaffId())).collect(Collectors.toList());
        }
        return filteredShifts;
    }
}
