package com.kairos.wrapper.phase;

import com.kairos.dto.activity.activity.activity_tabs.ActivityPhaseSettings;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.phase.PhaseWeeklyDTO;
import com.kairos.dto.activity.presence_type.PresenceTypeDTO;
import com.kairos.dto.activity.shift.ShiftTemplateDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.CountryHolidayCalenderDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 19/9/17.
 */
@Getter
@Setter
public class PhaseActivityDTO {
    private List<ActivityWithCompositeDTO> activities;
    private List<PhaseWeeklyDTO> phases;
    private List<DayTypeDTO> dayTypes= new ArrayList<>();
    private UserAccessRoleDTO staffAccessRole;
    private List<ShiftTemplateDTO> shiftTemplates;
    private List<PhaseDTO> applicablePhases;
    private List<PhaseDTO> actualPhases;
    private List<ReasonCodeDTO> reasonCodes;
    private LocalDate planningPeriodStartDate;
    private LocalDate planningPeriodEndDate;
    private List<CountryHolidayCalenderDTO> publicHolidays;
    private LocalDate firstRequestPhasePlanningPeriodEndDate;
    private List<PresenceTypeDTO> plannedTimes;
    private List<ActivityPhaseSettings> activityPhaseSettings;
    private List<ActivityConfigurationDTO> activityConfigurations;
    private LocalDate gracePeriodExpireDate;

    public PhaseActivityDTO() {
        //Default Constructor
    }

    public PhaseActivityDTO(List<ActivityWithCompositeDTO> activities, List<PhaseWeeklyDTO> phases, List<DayTypeDTO> dayTypes,
                            UserAccessRoleDTO staffAccessRole, List<ShiftTemplateDTO> shiftTemplates, List<PhaseDTO> applicablePhases, List<PhaseDTO> actualPhases, List<ReasonCodeDTO> reasonCodes, LocalDate planningPeriodStartDate, LocalDate planningPeriodEndDate, List<CountryHolidayCalenderDTO> publicHolidays,
                            LocalDate firstRequestPhasePlanningPeriodEndDate, List<PresenceTypeDTO> plannedTimes, List<ActivityPhaseSettings> activityPhaseSettings, List<ActivityConfigurationDTO> activityConfigurations, LocalDate gracePeriodEndDate) {
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
        this.activityPhaseSettings = activityPhaseSettings;
        this.activityConfigurations = activityConfigurations;
        this.gracePeriodExpireDate=gracePeriodEndDate;
    }
}

