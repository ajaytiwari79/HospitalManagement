package com.kairos.shiftplanning.domain.cta;

import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.dto.ActivityIntervalDTO;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<CTAInterval> getCtaIntervals() {
        return ctaIntervals;
    }

    public void setCtaIntervals(List<CTAInterval> ctaIntervals) {
        this.ctaIntervals = ctaIntervals;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public int getGranularity() {
        return granularity;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }

    public List<LocalDate> getHolidayDates() {
        return holidayDates;
    }

    public void setHolidayDates(List<LocalDate> holidayDates) {
        this.holidayDates = holidayDates;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

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


   /* public BigDecimal getCostByGranularity(int overLapMin,BigDecimal baseCost){
        calculateCostByGranularity(overLapMin,baseCost);
//        switch (granularity){
//            case 1:return calculateCostByGranularity(overLapMin);
//            case 5:return calculateCostByGranularity(overLapMin);
//            case 10:return calculateCostByGranularity(overLapMin);
//            case 15:return calculateCostByGranularity(overLapMin);
//            case 30:return calculateCostByGranularity(overLapMin);
//            case 60:return calculateCostByGranularity(overLapMin);
//        }
        return null;
    }

    private BigDecimal calculateCostByGranularity(int overLapMin,BigDecimal baseCost){
        return new BigDecimal(overLapMin).divide(new BigDecimal(granularity),0).multiply(getCostByCompensationType(baseCost));
    }*/



    /*private Integer getOverLapMin(ActivityLineInterval ali){
        if(ali.getStart().getMinuteOfDay()<startFrom && ali.getEnd().getMinuteOfDay()<endTo){
            return new Period(startFrom,ali.getEnd().getMinuteOfDay()).getMinutes();
        }else if(ali.getStart().getMinuteOfDay()>startFrom && ali.getEnd().getMinuteOfDay()<endTo){
            return ali.getInterval().toPeriod().getMinutes();
        }else if(ali.getStart().getMinuteOfDay()>startFrom && ali.getEnd().getMinuteOfDay()>endTo){
            return new Period(ali.getStart().getMinuteOfDay(),endTo).getMinutes();
        }
        return null;
    };*/

}
