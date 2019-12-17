package com.kairos.service.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.FilterType;
import com.kairos.persistence.model.shift.ShiftViolatedRules;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.ESCALATION_CAUSED_BY;
import static com.kairos.enums.FilterType.GROUPS;

/**
 * Created By G.P.Ranjan on 17/12/19
 **/
public class EscalationFilter implements ShiftFilter  {
    private Map<FilterType, Set<String>> filterCriteriaMap;
    Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap;

    public EscalationFilter(Map<BigInteger, ShiftViolatedRules> shiftViolatedRulesMap, Map<FilterType, Set<String>> filterCriteriaMap) {
        this.filterCriteriaMap = filterCriteriaMap;
        this.shiftViolatedRulesMap = shiftViolatedRulesMap;
    }

    @Override
    public <T extends ShiftDTO> List<T> meetCriteria(List<T> shiftDTOS) {
        boolean validFilter = filterCriteriaMap.containsKey(ESCALATION_CAUSED_BY) && isCollectionNotEmpty(filterCriteriaMap.get(ESCALATION_CAUSED_BY));
        List<T> filteredShifts = validFilter ? new ArrayList<>() : shiftDTOS;
        if(validFilter){
            for (ShiftDTO shiftDTO : shiftDTOS) {
                if(shiftViolatedRulesMap.containsKey(shiftDTO.getId()) && isCollectionNotEmpty(shiftViolatedRulesMap.get(shiftDTO.getId()).getEscalationReasons()) &&  !shiftViolatedRulesMap.get(shiftDTO.getId()).isEscalationResolved() && filterCriteriaMap.get(ESCALATION_CAUSED_BY).contains(shiftViolatedRulesMap.get(shiftDTO.getId()).getEscalationCausedBy().toString())){
                    filteredShifts.add((T)shiftDTO);
                }
            }
        }
        return filteredShifts;
    }
}
