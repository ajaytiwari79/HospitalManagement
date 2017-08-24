package com.kairos.response.dto.web;

import java.util.List;

/**
 * Created by pawanmandhan on 8/8/17.
 */
public class WTARuleTemplateDTO {

    protected String name;
    protected String templateType;

    protected String category;
    protected boolean isActive;
    protected String description;

    protected String time;
    protected List<String> balanceType;//multiple check boxes
    protected boolean checkAgainstTimeRules;
    protected long days;//no of days
    protected String minimumRest;//hh:mm
    protected long daysWorked;
    protected long nightsWorked;
    protected long interval;//
    protected String intervalUnit;
    protected long validationStartDate;
    protected long minimumDaysOff;
    protected long maximumVeto;
    protected long numberShiftsPerPeriod;
    protected long numberOfWeeks;
    protected String fromDayOfWeek; //(day of week)
    protected long fromTime;
    protected long proportional;
    protected String toDayOfWeek;
    protected long toTime;// (number)
    protected String continousDayRestHours;// (number)
    protected String minimumDurationBetweenShifts ;//hours(number)
    protected String continuousWeekRest;//(hours number)
    protected String averageRest;//(hours number)
    protected String shiftAffiliation;//(List checkbox)
    protected long number;
    protected boolean onlyCompositeShifts;//(checkbox)
    protected List<String> activityType;// checkbox)
    protected String activityCode;
    protected String maximumAvgTime;

    public String getMaximumAvgTime() {
        return maximumAvgTime;
    }

    public void setMaximumAvgTime(String maximumAvgTime) {
        this.maximumAvgTime = maximumAvgTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(List<String> balanceType) {
        this.balanceType = balanceType;
    }

    public boolean isCheckAgainstTimeRules() {
        return checkAgainstTimeRules;
    }

    public void setCheckAgainstTimeRules(boolean checkAgainstTimeRules) {
        this.checkAgainstTimeRules = checkAgainstTimeRules;
    }


    @Override
    public String toString() {
        return "WTARuleTemplateDTO{" +
                "name='" + name + '\'' +
                ", templateType='" + templateType + '\'' +
                ", ruleTemplateCategory='" + category + '\'' +
                ", isActive=" + isActive +
                ", description='" + description + '\'' +
                ", time='" + time + '\'' +
                ", balanceType=" + balanceType +
                ", checkAgainstTimeRules=" + checkAgainstTimeRules +
                ", days=" + days +
                ", minimumRest='" + minimumRest + '\'' +
                ", daysWorked=" + daysWorked +
                ", nightsWorked=" + nightsWorked +
                ", interval=" + interval +
                ", intervalUnit='" + intervalUnit + '\'' +
                ", validationStartDate=" + validationStartDate +
                ", minimumDaysOff=" + minimumDaysOff +
                ", maximumVeto=" + maximumVeto +
                ", numberShiftsPerPeriod=" + numberShiftsPerPeriod +
                ", numberOfWeeks=" + numberOfWeeks +
                ", fromDayOfWeek='" + fromDayOfWeek + '\'' +
                ", fromTime=" + fromTime +
                ", proportional=" + proportional +
                ", toDayOfWeek='" + toDayOfWeek + '\'' +
                ", toTime=" + toTime +
                ", continousDayRestHours='" + continousDayRestHours + '\'' +
                ", minimumDurationBetweenShifts='" + minimumDurationBetweenShifts + '\'' +
                ", continuousWeekRest='" + continuousWeekRest + '\'' +
                ", averageRest='" + averageRest + '\'' +
                ", shiftAffiliation=" + shiftAffiliation +
                ", number=" + number +
                ", onlyCompositeShifts=" + onlyCompositeShifts +
                ", activityType=" + activityType +
                ", activityCode=" + activityCode +
                '}';
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public String getMinimumRest() {
        return minimumRest;
    }

    public void setMinimumRest(String minimumRest) {
        this.minimumRest = minimumRest;
    }

    public long getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(long daysWorked) {
        this.daysWorked = daysWorked;
    }

    public long getNightsWorked() {
        return nightsWorked;
    }

    public void setNightsWorked(long nightsWorked) {
        this.nightsWorked = nightsWorked;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public void setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
    }

    public long getValidationStartDate() {
        return validationStartDate;
    }

    public void setValidationStartDate(long validationStartDate) {
        this.validationStartDate = validationStartDate;
    }

    public long getMinimumDaysOff() {
        return minimumDaysOff;
    }

    public void setMinimumDaysOff(long minimumDaysOff) {
        this.minimumDaysOff = minimumDaysOff;
    }

    public long getMaximumVeto() {
        return maximumVeto;
    }

    public void setMaximumVeto(long maximumVeto) {
        this.maximumVeto = maximumVeto;
    }

    public long getNumberShiftsPerPeriod() {
        return numberShiftsPerPeriod;
    }

    public void setNumberShiftsPerPeriod(long numberShiftsPerPeriod) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
    }

    public long getNumberOfWeeks() {
        return numberOfWeeks;
    }

    public void setNumberOfWeeks(long numberOfWeeks) {
        this.numberOfWeeks = numberOfWeeks;
    }

    public String getFromDayOfWeek() {
        return fromDayOfWeek;
    }

    public void setFromDayOfWeek(String fromDayOfWeek) {
        this.fromDayOfWeek = fromDayOfWeek;
    }

    public long getFromTime() {
        return fromTime;
    }

    public void setFromTime(long fromTime) {
        this.fromTime = fromTime;
    }

    public long getProportional() {
        return proportional;
    }

    public void setProportional(long proportional) {
        this.proportional = proportional;
    }

    public String getToDayOfWeek() {
        return toDayOfWeek;
    }

    public void setToDayOfWeek(String toDayOfWeek) {
        this.toDayOfWeek = toDayOfWeek;
    }

    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public String getContinousDayRestHours() {
        return continousDayRestHours;
    }

    public void setContinousDayRestHours(String continousDayRestHours) {
        this.continousDayRestHours = continousDayRestHours;
    }

    public String getMinimumDurationBetweenShifts() {
        return minimumDurationBetweenShifts;
    }

    public void setMinimumDurationBetweenShifts(String minimumDurationBetweenShifts) {
        this.minimumDurationBetweenShifts = minimumDurationBetweenShifts;
    }

    public String getContinuousWeekRest() {
        return continuousWeekRest;
    }

    public void setContinuousWeekRest(String continuousWeekRest) {
        this.continuousWeekRest = continuousWeekRest;
    }

    public String getAverageRest() {
        return averageRest;
    }

    public void setAverageRest(String averageRest) {
        this.averageRest = averageRest;
    }

    public String getShiftAffiliation() {
        return shiftAffiliation;
    }

    public void setShiftAffiliation(String shiftAffiliation) {
        this.shiftAffiliation = shiftAffiliation;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public boolean isOnlyCompositeShifts() {
        return onlyCompositeShifts;
    }

    public void setOnlyCompositeShifts(boolean onlyCompositeShifts) {
        this.onlyCompositeShifts = onlyCompositeShifts;
    }

    public List<String> getActivityType() {
        return activityType;
    }

    public void setActivityType(List<String> activityType) {
        this.activityType = activityType;
    }
}
