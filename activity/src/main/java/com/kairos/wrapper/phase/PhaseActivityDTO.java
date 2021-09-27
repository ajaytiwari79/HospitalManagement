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
import com.kairos.persistence.model.day_type.DayType;
import com.kairos.wrapper.activity.ActivityWithCompositeDTO;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vipul on 19/9/17.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhaseActivityDTO {
    private List<ActivityWithCompositeDTO> activities;
    private List<PhaseWeeklyDTO> phases;
    @Builder.Default
    private List<DayType> dayTypes= new ArrayList<>();
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

}

