package com.kairos.client.dto.timeBank;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class CTARuleTemplateBasicDTO {


    private Long id;
    private String name;
    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private List<BigInteger> timeTypes;
    private List<CTAIntervalDTO> ctaIntervalDTOS;

    public CTARuleTemplateBasicDTO() {
    }

    public CTARuleTemplateBasicDTO(int granularity, List<BigInteger> timeTypes) {
        this.granularity = granularity;
        this.timeTypes = timeTypes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<BigInteger> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<BigInteger> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<CTAIntervalDTO> getCtaIntervalDTOS() {
        return ctaIntervalDTOS;
    }

    public void setCtaIntervalDTOS(List<CTAIntervalDTO> ctaIntervalDTOS) {
        this.ctaIntervalDTOS = ctaIntervalDTOS;
    }
}
