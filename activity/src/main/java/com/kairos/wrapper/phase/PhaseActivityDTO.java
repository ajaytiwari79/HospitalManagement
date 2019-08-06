package com.kairos.wrapper.phase;

import com.kairos.dto.activity.activity.activity_tabs.PhaseSettingsActivityTab;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.phase.PhaseWeeklyDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by vipul on 19/9/17.
 */
public class PhaseActivityDTO {
    private List<ActivityWithCompositeDTO> activities;
    private List<PhaseWeeklyDTO> phases;
    private List<DayType> dayTypes= new ArrayList<>();
    private UserAccessRoleDTO staffAccessRole;
    private List<ShiftTemplateDTO> shiftTemplates;
    private List<PhaseDTO> applicablePhases;
    private List<PhaseDTO> actualPhases;
    private List<ReasonCodeDTO> reasonCodes;
    private LocalDate planningPeriodStartDate;
    private LocalDate planningPeriodEndDate;
    private List<Map<String, Object>> publicHolidays;
    private LocalDate firstRequestPhasePlanningPeriodEndDate;
    private List<PresenceTypeDTO> plannedTimes;
    private List<PhaseSettingsActivityTab> phaseSettingsActivityTab;

    public PhaseActivityDTO() {
        //Default Constructor
    }

    public PhaseActivityDTO(List<ActivityWithCompositeDTO> activities,List<PhaseWeeklyDTO> phases, List<DayType> dayTypes,
                            UserAccessRoleDTO staffAccessRole, List<ShiftTemplateDTO> shiftTemplates, List<PhaseDTO> applicablePhases, List<PhaseDTO> actualPhases,List<ReasonCodeDTO> reasonCodes,LocalDate planningPeriodStartDate,LocalDate planningPeriodEndDate,List<Map<String,Object>> publicHolidays,
                            LocalDate firstRequestPhasePlanningPeriodEndDate,List<PresenceTypeDTO> plannedTimes,List<PhaseSettingsActivityTab> phaseSettingsActivityTab) {
        this.activities=activities;
        this.phases = phases;
        this.dayTypes = dayTypes;
        this.staffAccessRole = staffAccessRole;
        this.shiftTemplates = shiftTemplates;
        this.applicablePhases = applicablePhases;
        this.actualPhases = actualPhases;
        this.reasonCodes = reasonCodes;
        this.planningPeriodStartDate=planningPeriodStartDate;
        this.planningPeriodEndDate=planningPeriodEndDate;
        this.publicHolidays=publicHolidays;
        this.firstRequestPhasePlanningPeriodEndDate = firstRequestPhasePlanningPeriodEndDate;
        this.plannedTimes = plannedTimes;
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }

    public List<Map<String, Object>> getPublicHolidays() { return publicHolidays; }

    public void setPublicHolidays(List<Map<String, Object>> publicHolidays) {
        this.publicHolidays = publicHolidays;
    }

    public List<ActivityWithCompositeDTO> getActivities() {
        return activities;
    }

    public void setActivities(List<ActivityWithCompositeDTO> activities) {
        this.activities = activities;
    }

    public List<PhaseWeeklyDTO> getPhases() {
        return phases;
    }

    public void setPhases(List<PhaseWeeklyDTO> phases) {
        this.phases = phases;
    }

    public List<PhaseDTO> getApplicablePhases() {
        return applicablePhases;
    }

    public void setApplicablePhases(List<PhaseDTO> applicablePhases) {
        this.applicablePhases = applicablePhases;
    }

    public List<DayType> getDayTypes() {
        return dayTypes;
    }

    public void setDayTypes(List<DayType> dayTypes) {
        this.dayTypes = dayTypes;
    }

    public UserAccessRoleDTO getStaffAccessRole() {
        return staffAccessRole;
    }

    public void setStaffAccessRole(UserAccessRoleDTO staffAccessRole) {
        this.staffAccessRole = staffAccessRole;
    }

    public List<PhaseDTO> getActualPhases() {
        return actualPhases;
    }

    public void setActualPhases(List<PhaseDTO> actualPhases) {
        this.actualPhases = actualPhases;
    }

    public List<ShiftTemplateDTO> getShiftTemplates() {
        return shiftTemplates;
    }

    public void setShiftTemplates(List<ShiftTemplateDTO> shiftTemplates) {
        this.shiftTemplates = shiftTemplates;
    }

    public List<ReasonCodeDTO> getReasonCodes() {
        return reasonCodes;
    }

    public void setReasonCodes(List<ReasonCodeDTO> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public LocalDate getPlanningPeriodStartDate() {
        return planningPeriodStartDate;
    }

    public void setPlanningPeriodStartDate(LocalDate planningPeriodStartDate) {
        this.planningPeriodStartDate = planningPeriodStartDate;
    }

    public LocalDate getPlanningPeriodEndDate() {
        return planningPeriodEndDate;
    }

    public void setPlanningPeriodEndDate(LocalDate planningPeriodEndDate) {
        this.planningPeriodEndDate = planningPeriodEndDate;
    }

    public LocalDate getFirstRequestPhasePlanningPeriodEndDate() {
        return firstRequestPhasePlanningPeriodEndDate;
    }

    public void setFirstRequestPhasePlanningPeriodEndDate(LocalDate firstRequestPhasePlanningPeriodEndDate) {
        this.firstRequestPhasePlanningPeriodEndDate = firstRequestPhasePlanningPeriodEndDate;
    }

    public List<PresenceTypeDTO> getPlannedTimes() {
        return plannedTimes;
    }

    public void setPlannedTimes(List<PresenceTypeDTO> plannedTimes) {
        this.plannedTimes = plannedTimes;
    }
    public List<PhaseSettingsActivityTab> getPhaseSettingsActivityTab() {
        return phaseSettingsActivityTab;
    }

    public void setPhaseSettingsActivityTab(List<PhaseSettingsActivityTab> phaseSettingsActivityTab) {
        this.phaseSettingsActivityTab = phaseSettingsActivityTab;
    }
}

