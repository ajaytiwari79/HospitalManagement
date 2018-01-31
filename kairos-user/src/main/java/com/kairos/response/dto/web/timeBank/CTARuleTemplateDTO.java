package com.kairos.response.dto.web.timeBank;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class CTARuleTemplateDTO {

    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private List<String> timeTypes;
    private List<CTAIntervalDTO> ctaIntervalDTOS;

    public CTARuleTemplateDTO(int granularity,List<String> timeTypes) {
        this.timeTypes = timeTypes;
        this.granularity = granularity;
    }

    public List<Integer> getDays() {
        return days;
    }

    public void setDays(List<Integer> days) {
        this.days = days;
    }

    public List<LocalDate> getPublicHolidays() {
        return publicHolidays;
    }

    public void setPublicHolidays(List<LocalDate> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    public int getGranularity() {
        return granularity;
    }

    public void setGranularity(int granularity) {
        this.granularity = granularity;
    }

    public List<BigInteger> getActivityIds() {
        return activityIds;
    }

    public void setActivityIds(List<BigInteger> activityIds) {
        this.activityIds = activityIds;
    }

    public List<String> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<String> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<CTAIntervalDTO> getCtaIntervalDTOS() {
        return ctaIntervalDTOS;
    }

    public void setCtaIntervalDTOS(List<CTAIntervalDTO> ctaIntervalDTOS) {
        this.ctaIntervalDTOS = ctaIntervalDTOS;
    }
}
