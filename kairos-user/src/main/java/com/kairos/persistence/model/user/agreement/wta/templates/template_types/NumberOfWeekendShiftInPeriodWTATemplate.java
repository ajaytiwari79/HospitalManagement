package com.kairos.persistence.model.user.agreement.wta.templates.template_types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.user.agreement.wta.templates.WTABaseRuleTemplate;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 * TEMPLATE13
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NumberOfWeekendShiftInPeriodWTATemplate extends WTABaseRuleTemplate {

    private long numberShiftsPerPeriod;
    private long numberOfWeeks;
    private String fromDayOfWeek; //(day of week)
    private long fromTime;
    private boolean proportional;
    private long toTime;
    private String toDayOfWeek;


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

    public NumberOfWeekendShiftInPeriodWTATemplate(String name, String templateType, boolean isActive,
                                                   String description, long numberShiftsPerPeriod, long numberOfWeeks, String fromDayOfWeek, long fromTime, boolean proportional,
                                                   String toDayOfWeek, long toTime) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

        this.numberShiftsPerPeriod=numberShiftsPerPeriod;
        this.numberOfWeeks=numberOfWeeks;
        this.proportional=proportional;
        this.fromDayOfWeek=fromDayOfWeek;
        this.fromTime=fromTime;
        this.toDayOfWeek=toDayOfWeek;
        this.toTime=toTime;



    }
    public NumberOfWeekendShiftInPeriodWTATemplate() {
    }


}
