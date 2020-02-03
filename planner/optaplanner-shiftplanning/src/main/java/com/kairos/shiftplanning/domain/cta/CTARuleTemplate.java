package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.dto.ActivityIntervalDTO;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;
@Getter
@Setter
public class CTARuleTemplate {

    private Long id;
    private String ruleName;
    private int priority;
    //In minutes
    List<CTAInterval> ctaIntervals;
    //where 1 index is Monday and 7 index is Sunday.
    private boolean[] days;
    private int granularity;
    //We need to add Activity Types also
    private List<Activity> activities;
    private List<LocalDate> holidayDates;


    public BigDecimal getCostForThisCTATemplate(List<ActivityIntervalDTO> aliDTOs, BigDecimal baseCost){
        BigDecimal costPerTemplate = new BigDecimal(0);
        for (ActivityIntervalDTO intervalDTO:aliDTOs) {
            if(!intervalDTO.isProcessedForDay() && activities.contains(intervalDTO.getActivity()) && (days==null || days[intervalDTO.getStart().getDayOfWeek()])){
                costPerTemplate = costPerTemplate.add(getCostByInterval(intervalDTO,baseCost));
                if(holidayDates!=null && !holidayDates.isEmpty() && holidayDates.contains(intervalDTO.getStart().toLocalDate())){
                    intervalDTO.setProcessedForDay(true);
                }
            }
        }
        return costPerTemplate;
    }

    public BigDecimal getCostByInterval(ActivityIntervalDTO ali,BigDecimal baseCost){
        BigDecimal costByInterval = new BigDecimal(0);
        for (CTAInterval ruleInterval:ctaIntervals) {
            if(ruleInterval.getTimeInterval().overlaps(ali.getInterval())){
                costByInterval = costByInterval.add(ruleInterval.getCostForThisIntervalByGranularity(granularity,ali.getInterval(),baseCost));
            }
        }
        return costByInterval;
    }


}
