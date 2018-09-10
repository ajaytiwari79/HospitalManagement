package com.kairos.dto.activity.activity.activity_tabs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.enums.TimeCalaculationType;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by vipul on 23/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeCalculationActivityDTO {

    private Long activityId;
    private String methodForCalculatingTime;
    private TimeCalaculationType fullDayCalculationType;
    private TimeCalaculationType fullWeekCalculationType;
    private Boolean allowBreakReduction;
    private Long fixedTimeValue;
    private String methodForCalculatingTimeInMonths;
    private List<String> balanceType;
    private Boolean multiplyWith;
    private Double multiplyWithValue;
    private Boolean multiplyByVacationFactor;
    private Boolean multiplyByFinalSchedule;
    private String breakTemplates;
    private List<Long> dayTypes;
    private DayOfWeek fullWeekStart;
    private DayOfWeek fullWeekEnd;
    private int historyDuration;
    private LocalTime defaultStartTime;

    public TimeCalculationActivityDTO() {
        //dc
    }

    public DayOfWeek getFullWeekStart() {
        return fullWeekStart;
    }

    public void setFullWeekStart(DayOfWeek fullWeekStart) {
        this.fullWeekStart = fullWeekStart;
    }

    public DayOfWeek getFullWeekEnd() {
        return fullWeekEnd;
    }

    public void setFullWeekEnd(DayOfWeek fullWeekEnd) {
        this.fullWeekEnd = fullWeekEnd;
    }


    public int getHistoryDuration() {
        return historyDuration;
    }

    public void setHistoryDuration(int historyDuration) {
        this.historyDuration = historyDuration;
    }

    public LocalTime getDefaultStartTime() {
        return defaultStartTime;
    }

    public void setDefaultStartTime(LocalTime defaultStartTime) {
        this.defaultStartTime = defaultStartTime;
    }


    public String getMethodForCalculatingTime() {
        return methodForCalculatingTime;
    }

    public void setMethodForCalculatingTime(String methodForCalculatingTime) {
        this.methodForCalculatingTime = methodForCalculatingTime;
    }

    public Boolean getAllowBreakReduction() {
        return allowBreakReduction;
    }

    public void setAllowBreakReduction(Boolean allowBreakReduction) {
        this.allowBreakReduction = allowBreakReduction;
    }

    public Long getFixedTimeValue() {
        return fixedTimeValue;
    }

    public void setFixedTimeValue(Long fixedTimeValue) {
        this.fixedTimeValue = fixedTimeValue;
    }

    public String getMethodForCalculatingTimeInMonths() {
        return methodForCalculatingTimeInMonths;
    }

    public void setMethodForCalculatingTimeInMonths(String methodForCalculatingTimeInMonths) {
        this.methodForCalculatingTimeInMonths = methodForCalculatingTimeInMonths;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public Boolean getMultiplyWith() {
        return multiplyWith;
    }

    public void setMultiplyWith(Boolean multiplyWith) {
        this.multiplyWith = multiplyWith;
    }



    public Double getMultiplyWithValue() {
        return multiplyWithValue;
    }

    public void setMultiplyWithValue(Double multiplyWithValue) {
        this.multiplyWithValue = multiplyWithValue;
    }


    public Boolean getMultiplyByVacationFactor() {
        return multiplyByVacationFactor;
    }

    public void setMultiplyByVacationFactor(Boolean multiplyByVacationFactor) {
        this.multiplyByVacationFactor = multiplyByVacationFactor;
    }

    public Boolean getMultiplyByFinalSchedule() {
        return multiplyByFinalSchedule;
    }

    public void setMultiplyByFinalSchedule(Boolean multiplyByFinalSchedule) {
        this.multiplyByFinalSchedule = multiplyByFinalSchedule;
    }


    public String getBreakTemplates() {
        return breakTemplates;
    }

    public void setBreakTemplates(String breakTemplates) {
        this.breakTemplates = breakTemplates;
    }



    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public List<Long> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<Long> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public TimeCalaculationType getFullDayCalculationType() {
        return fullDayCalculationType;
    }

    public void setFullDayCalculationType(TimeCalaculationType fullDayCalculationType) {
        this.fullDayCalculationType = fullDayCalculationType;
    }

    public TimeCalaculationType getFullWeekCalculationType() {
        return fullWeekCalculationType;
    }

    public void setFullWeekCalculationType(TimeCalaculationType fullWeekCalculationType) {
        this.fullWeekCalculationType = fullWeekCalculationType;
    }
}
