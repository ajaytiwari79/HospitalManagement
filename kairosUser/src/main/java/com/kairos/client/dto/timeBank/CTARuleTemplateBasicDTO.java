package com.kairos.client.dto.timeBank;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CTARuleTemplateBasicDTO {


    private Long id;
    private String name;
    //Base monday
    private List<Integer> days;
    private List<LocalDate> publicHolidays;
    private int granularity;
    private List<BigInteger> activityIds;
    private BigInteger timeTypeId;
    private List<CTAIntervalDTO> ctaIntervalDTOS;
    private boolean calculateScheduledHours;
    private Long plannedTimeId;
    private List<Long> employmentTypes =new ArrayList<>();
    private String accountType;

    public boolean isCalculateScheduledHours() {
        return calculateScheduledHours;
    }

    public void setCalculateScheduledHours(boolean calculateScheduledHours) {
        this.calculateScheduledHours = calculateScheduledHours;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public List<Long> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<Long> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public Long getPlannedTimeId() {
        return plannedTimeId;
    }

    public void setPlannedTimeId(Long plannedTimeId) {
        this.plannedTimeId = plannedTimeId;
    }

    public CTARuleTemplateBasicDTO() {
    }

    public CTARuleTemplateBasicDTO(int granularity, List<BigInteger> timeTypes) {
        this.granularity = granularity;
        this.timeTypeId = timeTypeId;
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

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public List<CTAIntervalDTO> getCtaIntervalDTOS() {
        return ctaIntervalDTOS;
    }

    public void setCtaIntervalDTOS(List<CTAIntervalDTO> ctaIntervalDTOS) {
        this.ctaIntervalDTOS = ctaIntervalDTOS;
    }
}
