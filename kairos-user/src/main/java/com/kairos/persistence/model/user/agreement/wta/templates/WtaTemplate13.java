package com.kairos.persistence.model.user.agreement.wta.templates;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by pawanmandhan on 5/8/17.
 */
@NodeEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WtaTemplate13 extends WTABaseRuleTemplate {
    private long numberShiftsPerPeriod;
    private long numberOfWeeks;
    private String fromDayOfWeek; //(day of week)
    private long fromTime;
    private long proportional;

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

    public WtaTemplate13(String name, String templateType,  boolean isActive,
                         String description, long numberShiftsPerPeriod, long numberOfWeeks, String fromDayOfWeek, long fromTime, long proportional) {
        this.name = name;
        this.templateType = templateType;
        this.isActive = isActive;
        this.description = description;

        this.numberShiftsPerPeriod=numberShiftsPerPeriod;
        this.numberOfWeeks=numberOfWeeks;
        this.proportional=proportional;
        this.fromDayOfWeek=fromDayOfWeek;
        this.fromTime=fromTime;



    }
    public WtaTemplate13() {
    }


}
