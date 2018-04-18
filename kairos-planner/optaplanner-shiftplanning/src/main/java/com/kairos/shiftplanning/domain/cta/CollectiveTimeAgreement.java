package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.ActivityLineInterval;
import com.kairos.shiftplanning.domain.ShiftRequestPhase;
import com.kairos.shiftplanning.dto.ActivityIntervalDTO;
import com.kairos.shiftplanning.utils.StaticFields;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CollectiveTimeAgreement {

    private String id;
    private List<CTARuleTemplate> ctaRuleTemplates;
    private WorkingExtraTimeCtaRuleTemplate workingExtraTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CTARuleTemplate> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplate> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }

    public WorkingExtraTimeCtaRuleTemplate getWorkingExtraTime() {
        return workingExtraTime;
    }

    public void setWorkingExtraTime(WorkingExtraTimeCtaRuleTemplate workingExtraTime) {
        this.workingExtraTime = workingExtraTime;
    }

    public BigDecimal getTotalCostOfShift(ShiftRequestPhase shift){
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
