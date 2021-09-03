package com.kairos.wrapper.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ViolatedRulesDTO;
import com.kairos.dto.activity.unit_settings.UnitGeneralSettingDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.shift.ShiftOperationType;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.unit_settings.UnitGeneralSetting;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

/**
 * @author pradeep
 * @date - 23/5/18
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTemplateSpecificInfo {
    @Builder.Default
    private List<ShiftWithActivityDTO> shifts = new ArrayList<>();
    private ShiftWithActivityDTO shift;
    private Map<String,TimeSlot> timeSlotWrapperMap;
    private BigInteger phaseId;
    private DateTimeInterval planningPeriod;
    private Map<BigInteger,Integer> counterMap;
    private Map<BigInteger, DayTypeDTO> dayTypeMap;
    private UserAccessRoleDTO user;
    private long totalTimeBank;
    @Builder.Default
    private ViolatedRulesDTO violatedRules=new ViolatedRulesDTO();
    private int staffAge;
    private Map<BigInteger,ActivityWrapper> activityWrapperMap;
    private List<CareDaysDTO> childCareDays;
    private List<CareDaysDTO> seniorCareDays;
    private LocalDate lastPlanningPeriodEndDate;
    private boolean nightWorker;
    private ExpertiseNightWorkerSetting expertiseNightWorkerSetting;
    PhaseDefaultName phaseEnum;
    private List<Integer> staffChildAges;
    private ShiftOperationType shiftOperationType;
    private UnitGeneralSettingDTO unitGeneralSetting;
    private int totalWeeklyMinutes;


    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift, Map<String, TimeSlot> timeSlotWrapperMap, BigInteger phaseId, DateTimeInterval planningPeriod, Map<BigInteger,Integer> counterMap, Map<BigInteger, DayTypeDTO> dayTypeMap, long totalTimeBank, Map<BigInteger, ActivityWrapper> activityWrapperMap, int staffAge, List<CareDaysDTO> childCareDays, List<CareDaysDTO> seniorCareDays, LocalDate lastPlanningPeriodEndDate, ExpertiseNightWorkerSetting expertiseNightWorkerSetting, boolean nightWorker, PhaseDefaultName phaseEnum, List<Integer> staffChildAges, ShiftOperationType shiftOperationType,int totalWeeklyMinutes) {
        this.shifts = isNullOrElse(shifts,new ArrayList<>());
        Collections.sort(this.shifts);
        this.shift = shift;
        this.timeSlotWrapperMap = timeSlotWrapperMap;
        this.phaseId = phaseId;
        this.planningPeriod = planningPeriod;
        this.counterMap = counterMap;
        this.dayTypeMap = dayTypeMap;
        this.totalTimeBank = totalTimeBank;
        this.violatedRules = new ViolatedRulesDTO();
        this.activityWrapperMap = activityWrapperMap;
        this.staffAge = staffAge;
        this.childCareDays = childCareDays;
        this.seniorCareDays = seniorCareDays;
        this.lastPlanningPeriodEndDate = lastPlanningPeriodEndDate;
        this.expertiseNightWorkerSetting = expertiseNightWorkerSetting;
        this.nightWorker = nightWorker;
        this.phaseEnum=phaseEnum;
        this.staffChildAges=staffChildAges;
        this.shiftOperationType=shiftOperationType;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift, Map<String,TimeSlot> timeSlotWrapperMap, BigInteger phaseId, DateTimeInterval planningPeriod, Map<BigInteger,Integer> counterMap, Map<BigInteger, DayTypeDTO> dayTypeMap, long totalTimeBank, Map<BigInteger, ActivityWrapper> activityWrapperMap, int staffAge, List<CareDaysDTO> childCareDays,List<CareDaysDTO> seniorCareDays,LocalDate lastPlanningPeriodEndDate,ExpertiseNightWorkerSetting expertiseNightWorkerSetting,boolean nightWorker, PhaseDefaultName phaseEnum, UnitGeneralSettingDTO unitGeneralSetting,int totalWeeklyMinutes) {
        this.shifts = isNullOrElse(shifts,new ArrayList<>());
        Collections.sort(this.shifts);
        this.shift = shift;
        this.timeSlotWrapperMap = timeSlotWrapperMap;
        this.phaseId = phaseId;
        this.planningPeriod = planningPeriod;
        this.counterMap = counterMap;
        this.dayTypeMap = dayTypeMap;
        this.totalTimeBank = totalTimeBank;
        this.violatedRules = new ViolatedRulesDTO();
        this.activityWrapperMap = activityWrapperMap;
        this.staffAge = staffAge;
        this.childCareDays = childCareDays;
        this.seniorCareDays = seniorCareDays;
        this.lastPlanningPeriodEndDate = lastPlanningPeriodEndDate;
        this.expertiseNightWorkerSetting = expertiseNightWorkerSetting;
        this.nightWorker = nightWorker;
        this.phaseEnum=phaseEnum;
        this.unitGeneralSetting = unitGeneralSetting;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, boolean nightWorker, ExpertiseNightWorkerSetting expertiseNightWorkerSetting,BigInteger phaseId,ShiftWithActivityDTO shift,UserAccessRoleDTO user,ViolatedRulesDTO violatedRules) {
        this.shifts = shifts;
        this.nightWorker = nightWorker;
        this.expertiseNightWorkerSetting = expertiseNightWorkerSetting;
        this.phaseId = phaseId;
        this.shift = shift;
        this.user = user;
        this.violatedRules = violatedRules;
    }

    public boolean isWTARuletemplateBroken(BigInteger wtaRuletemplateId){
        return violatedRules.getWorkTimeAgreements().stream().anyMatch(workTimeAgreementRuleViolation -> workTimeAgreementRuleViolation.getRuleTemplateId().equals(wtaRuletemplateId) && workTimeAgreementRuleViolation.isBroken());
    }

    public ViolatedRulesDTO getViolatedRules() {
        this.violatedRules = isNullOrElse(violatedRules,new ViolatedRulesDTO());
        return this.violatedRules;
    }
}
