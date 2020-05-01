package com.kairos.dto.planner.shift_planning;

import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.night_worker.ExpertiseNightWorkerSettingDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelDto;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.user.country.day_type.DayType;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.organization.TimeSlot;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author pradeep
 * @date - 23/11/18
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftPlanningProblemSubmitDTO {

    private BigInteger planningProblemId;
    private SolverConfigDTO solverConfig;
    private BigInteger solverConfigId;
    private List<Long> staffIds=new ArrayList<>();
    private Long unitId;
    private BigInteger planningPeriodId;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<StaffDTO> staffs;
    private Map<Long, DayType> dayTypeMap;
    private Map<String, TimeSlotDTO> timeSlotMap;
    private List<ShiftDTO> shifts;
    private List<ActivityDTO> activities;
    private PlanningPeriodDTO planningPeriod;
    private StaffingLevelDto staffingLevel;
    private ActivityConfigurationDTO activityConfiguration;
    private Set<BigInteger> lockedShiftIds;
    private Map<BigInteger,Integer> activityOrderMap;
    private Map<Long, List<CTAResponseDTO>> employmentIdAndCTAResponseMap;
    private Map<Long, List<WTAResponseDTO>> employmentIdAndWTAResponseMap;
    Map<Long, ExpertiseNightWorkerSettingDTO> expertiseNightWorkerSettingMap;
    Map<Long, BreakSettingsDTO> breakSettingMap;
    Map<Long, Boolean> nightWorkerDetails;


    public Map<BigInteger,Integer> getActivityOrderMap(){
        AtomicInteger atomicInteger = new AtomicInteger(0);
        activityOrderMap = activities.stream().sorted(Comparator.comparing(ActivityDTO::getActivitySequence)).collect(Collectors.toMap(ActivityDTO::getId,v->atomicInteger.addAndGet(1)));
        return activityOrderMap;
    }


}
