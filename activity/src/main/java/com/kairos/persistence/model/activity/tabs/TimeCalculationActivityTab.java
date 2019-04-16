package com.kairos.persistence.model.activity.tabs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.enums.TimeCalaculationType;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimeCalculationActivityTab implements Serializable {

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
    private DayOfWeek fullWeekStart;
    private DayOfWeek fullWeekEnd;
    private String breakTemplates;
    private int historyDuration;
    private LocalTime defaultStartTime;
    private List<Long> dayTypes = new ArrayList<>();
    private boolean midnightToMidnightShift;

    public TimeCalculationActivityTab() {
    }

    public LocalTime getDefaultStartTime() {
        return defaultStartTime;
    }

    public void setDefaultStartTime(LocalTime defaultStartTime) {
        this.defaultStartTime = defaultStartTime;
    }

    public TimeCalculationActivityTab(String methodForCalculatingTime) {
        this.methodForCalculatingTime = methodForCalculatingTime;
    }

    public String getMethodForCalculatingTime() {
        return methodForCalculatingTime;
    }

    public void setMethodForCalculatingTime(String methodForCalculatingTime) {
        this.methodForCalculatingTime = methodForCalculatingTime;
    }

    public int getHistoryDuration() {
        return historyDuration;
    }

    public void setHistoryDuration(int historyDuration) {
        this.historyDuration = historyDuration;
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
    public TimeCalculationActivityTab(String methodForCalculatingTime, Long fixedTimeValue, Boolean multiplyWith, LocalTime defaultStartTime,Double multiplyWithValue) {
        this.methodForCalculatingTime = methodForCalculatingTime;
        this.fixedTimeValue = fixedTimeValue;
        this.multiplyWith = multiplyWith;
        this.defaultStartTime = defaultStartTime;
        this.multiplyWithValue = multiplyWithValue;
    }

    public boolean isMidnightToMidnightShift() {
        return midnightToMidnightShift;
    }

    public void setMidnightToMidnightShift(boolean midnightToMidnightShift) {
        this.midnightToMidnightShift = midnightToMidnightShift;
    }
}