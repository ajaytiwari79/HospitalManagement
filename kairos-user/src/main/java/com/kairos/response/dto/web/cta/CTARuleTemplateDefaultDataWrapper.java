package com.kairos.response.dto.web.cta;

import com.kairos.client.dto.TimeTypeDTO;
import com.kairos.persistence.model.user.agreement.cta.CalculateValueIfPlanned;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CTARuleTemplateDefaultDataWrapper {
    private List<CalculateValueIfPlanned> calculateValueIfPlanned=new ArrayList<>();
    private List<DayTypeDTO> dayTypes=new ArrayList<>();
    private List<PhaseDTO> phases=new ArrayList<>();
    private List<TimeTypeDTO> timeTypes=new ArrayList<>();
    private List<ActivityTypeDTO> activityTypes=new ArrayList<>();
    private List<EmploymentTypeDTO> employmentTypes=new ArrayList<>();
    private List<Map<String, Object>>currencies=new ArrayList<>();
    private List<Map<String, Object>>holidayMapList=new ArrayList<>();


    public CTARuleTemplateDefaultDataWrapper() {
        //default
    }

    public List<CalculateValueIfPlanned> getCalculateValueIfPlanned() {
        return calculateValueIfPlanned;
    }

    public void setCalculateValueIfPlanned(List<CalculateValueIfPlanned> calculateValueIfPlanned) {
        this.calculateValueIfPlanned = calculateValueIfPlanned;
    }

    public List<DayTypeDTO> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayTypeDTO> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public List<PhaseDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseDTO> phases) {
        this.phases = phases;
    }

    public List<TimeTypeDTO> getTimeTypes() {
        return timeTypes;
    }

    public void setTimeTypes(List<TimeTypeDTO> timeTypes) {
        this.timeTypes = timeTypes;
    }

    public List<ActivityTypeDTO> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityTypeDTO> activityTypes) {
        this.activityTypes = activityTypes;
    }

    public List<EmploymentTypeDTO> getEmploymentTypes() {
        return employmentTypes;
    }

    public void setEmploymentTypes(List<EmploymentTypeDTO> employmentTypes) {
        this.employmentTypes = employmentTypes;
    }

    public List<Map<String, Object>> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Map<String, Object>> currencies) {
        this.currencies = currencies;
    }

    public List<Map<String, Object>> getHolidayMapList() {
        return holidayMapList;
    }

    public void setHolidayMapList(List<Map<String, Object>> holidayMapList) {
        this.holidayMapList = holidayMapList;
    }
}
