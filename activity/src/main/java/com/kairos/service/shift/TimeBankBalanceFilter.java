package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.FilterType.TIME_BANK_BALANCE;

/**
 * Created By G.P.Ranjan on 17/1/20
 **/
public class TimeBankBalanceFilter <G> implements ShiftFilter {
    private Map<FilterType, Set<G>> filterCriteriaMap;
    private Map<Long,Double> employmentIdAndActualTimeBankData;

    public TimeBankBalanceFilter(Map<FilterType, Set<G>> filterCriteriaMap, Map<Long,Double> employmentIdAndActualTimeBankData) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.employmentIdAndActualTimeBankData = employmentIdAndActualTimeBankData;
    }
    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(TIME_BANK_BALANCE) && isCollectionNotEmpty(filterCriteriaMap.get(TIME_BANK_BALANCE));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            Map timeBankRangeMap = (Map) filterCriteriaMap.get(TIME_BANK_BALANCE).iterator().next();

            Long from = timeBankRangeMap.containsKey("from") && isNotNull(timeBankRangeMap.get("from"))  ? Long.parseLong(timeBankRangeMap.get("from").toString()) : null;
            Long to = timeBankRangeMap.containsKey("to") && isNotNull(timeBankRangeMap.get("to")) ? Long.parseLong(timeBankRangeMap.get("to").toString()) : null;

            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(isValidTimeBank(from, to, shiftDTO.getEmploymentId())) {
                    filteredShifts.add((T) shiftDTO);
                }
            }
        }
        return filteredShifts;
    }

    private boolean isValidTimeBank(Long from, Long to, Long employmentId){
        boolean isValid = false;
        if(employmentIdAndActualTimeBankData.containsKey(employmentId) && isNotNull(from) || isNotNull(to)) {
            if (isNull(from)) {
                isValid = employmentIdAndActualTimeBankData.get(employmentId) <= to;
            } else if (isNull(to)) {
                isValid = employmentIdAndActualTimeBankData.get(employmentId) >= from;
            } else {
                isValid = from <= employmentIdAndActualTimeBankData.get(employmentId) && to >= employmentIdAndActualTimeBankData.get(employmentId);
            }
        }
        return isValid;
    }
}
