package com.kairos.client.dto.time_bank;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

public class CTARuleTemplateDTO {


    private Long id;
    private String name;
    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private List<BigInteger> timeTypeIds;
    private List<CTAIntervalDTO> ctaIntervalDTOS;
    private int minutesFromCta;
    private List<BigInteger> timeTypeIdsWithParentTimeType;
    private boolean calculateScheduledHours;
    private List<Long> plannedTimeIds;
    private List<Long> employmentTypes;
    private String accountType;

    public CTARuleTemplateDTO() {
    }

    public CTARuleTemplateDTO(Long id, String name, int granularity, List<BigInteger> timeTypeIds) {
        this.id = id;
        this.name = name;
        this.granularity = granularity;
        this.timeTypeIds = timeTypeIds;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<BigInteger> getTimeTypeIdsWithParentTimeType() {
        return timeTypeIdsWithParentTimeType;
    }

    public void setTimeTypeIdsWithParentTimeType(List<BigInteger> timeTypeIdsWithParentTimeType) {
        this.timeTypeIdsWithParentTimeType = timeTypeIdsWithParentTimeType;
    }

    public boolean isCalculateScheduledHours() {
        return calculateScheduledHours;
    }

    public void setCalculateScheduledHours(boolean calculateScheduledHours) {
        this.calculateScheduledHours = calculateScheduledHours;
    }

    public List<BigInteger> getTimeTypeIds() {
        return timeTypeIds;
    }

    public void setTimeTypeIds(List<BigInteger> timeTypeIds) {
        this.timeTypeIds = timeTypeIds;
    }

    public List<Long> getPlannedTimeIds() {
        return plannedTimeIds;
    }

    public void setPlannedTimeIds(List<Long> plannedTimeIds) {
        this.plannedTimeIds = plannedTimeIds;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
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


    public List<CTAIntervalDTO> getCtaIntervalDTOS() {
        return ctaIntervalDTOS;
    }

    public void setCtaIntervalDTOS(List<CTAIntervalDTO> ctaIntervalDTOS) {
        this.ctaIntervalDTOS = ctaIntervalDTOS;
    }
}
