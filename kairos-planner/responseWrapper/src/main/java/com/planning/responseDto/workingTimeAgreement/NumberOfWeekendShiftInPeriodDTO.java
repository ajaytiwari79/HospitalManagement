package com.planning.responseDto.workingTimeAgreement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pradeep singh on 5/8/17.
 * TEMPLATE13
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfWeekendShiftInPeriodDTO {

    private long numberShiftsPerPeriod;
    private long numberOfWeeks;
    private String fromDayOfWeek; //(day of week)
    private long fromTime;
    private boolean proportional;
    private long toTime;
    private String toDayOfWeek;
    private int weight;
    private String level;
    private String templateType;

    public NumberOfWeekendShiftInPeriodDTO(long numberShiftsPerPeriod, int weight, String level) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
        this.weight = weight;
        this.level = level;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }


    public long getToTime() {
        return toTime;
    }

    public void setToTime(long toTime) {
        this.toTime = toTime;
    }

    public String getToDayOfWeek() {
        return toDayOfWeek;
    }

    public void setToDayOfWeek(String toDayOfWeek) {
        this.toDayOfWeek = toDayOfWeek;
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

    public boolean getProportional() {
        return proportional;
    }

    public void setProportional(boolean proportional) {
        this.proportional = proportional;
    }

    public NumberOfWeekendShiftInPeriodDTO(long numberShiftsPerPeriod, long numberOfWeeks, String fromDayOfWeek, long fromTime, boolean proportional, long toTime, String toDayOfWeek) {
        this.numberShiftsPerPeriod = numberShiftsPerPeriod;
        this.numberOfWeeks = numberOfWeeks;
        this.fromDayOfWeek = fromDayOfWeek;
        this.fromTime = fromTime;
        this.proportional = proportional;
        this.toTime = toTime;
        this.toDayOfWeek = toDayOfWeek;
    }

    public NumberOfWeekendShiftInPeriodDTO() {
    }

    public List<LocalDate> getWeekEndDate(DateTime fromTime,DateTime toTime){
        List<LocalDate> localDates = new ArrayList<>();
        while (fromTime.isBefore(toTime)){
            if(fromTime.getDayOfWeek()==DateTimeConstants.SATURDAY || fromTime.getDayOfWeek()==DateTimeConstants.SUNDAY){
                localDates.add(fromTime.toLocalDate());
            }
            fromTime = fromTime.plusDays(1);
        }
        if(toTime.getDayOfWeek()==DateTimeConstants.SATURDAY || toTime.getDayOfWeek()==DateTimeConstants.SUNDAY){
            localDates.add(toTime.toLocalDate());
        }
        return localDates;
    }

}
