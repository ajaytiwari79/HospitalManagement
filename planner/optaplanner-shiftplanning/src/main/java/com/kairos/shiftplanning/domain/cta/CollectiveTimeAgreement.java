package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.dto.ActivityIntervalDTO;
import com.kairos.shiftplanning.utils.StaticFields;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class CollectiveTimeAgreement {

    private String id;
    private List<CTARuleTemplate> ctaRuleTemplates;

    public BigDecimal getTotalCostOfShift(ShiftImp shift){
        List<ActivityIntervalDTO> activityIntervalDTOS = getActivityIntervalDTO(shift.getActivityLineIntervals());
        BigDecimal shiftCost = shift.getEmployee().getBaseCost().divide(new BigDecimal(60), StaticFields.DECIMAL_PLACES,BigDecimal.ROUND_CEILING).multiply(new BigDecimal(shift.getInterval().toDuration().getStandardMinutes()));
        for (CTARuleTemplate cta:ctaRuleTemplates){
            shiftCost = shiftCost.add(cta.getCostForThisCTATemplate(activityIntervalDTOS,shift.getEmployee().getBaseCost()));
        }
        return shiftCost;
    }

    private List<ActivityIntervalDTO> getActivityIntervalDTO(List<ActivityLineInterval> activityLineIntervals){
       List<ActivityIntervalDTO> activityIntervalDTOS = new ArrayList<>(activityLineIntervals.size());
        activityLineIntervals.forEach(ali->{
            activityIntervalDTOS.add(new ActivityIntervalDTO(ali));
        });
        return activityIntervalDTOS;
    }

}
