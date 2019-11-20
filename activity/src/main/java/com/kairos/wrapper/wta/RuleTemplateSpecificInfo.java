package com.kairos.wrapper.wta;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.shift.ViolatedRulesDTO;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user.expertise.CareDaysDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pradeep
 * @date - 23/5/18
 */

@Getter
@Setter
public class RuleTemplateSpecificInfo {

    private List<ShiftWithActivityDTO> shifts = new ArrayList<>();
    private ShiftWithActivityDTO shift;
    private Map<String,TimeSlotWrapper> timeSlotWrapperMap;
    private BigInteger phaseId;
    private DateTimeInterval planningPeriod;
    private Map<BigInteger,Integer> counterMap;
    private Map<Long, DayTypeDTO> dayTypeMap;
    private UserAccessRoleDTO user;
    private long totalTimeBank;
    private ViolatedRulesDTO violatedRules;
    private int staffAge;
    private Map<BigInteger,ActivityWrapper> activityWrapperMap;
    private List<CareDaysDTO> childCareDays;
    private List<CareDaysDTO> seniorCareDays;
    private LocalDate lastPlanningPeriodEndDate;
    private boolean nightWorker;
    private ExpertiseNightWorkerSetting expertiseNightWorkerSetting;
    PhaseDefaultName phaseEnum;
    private List<Integer> staffChildAges;


    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift, Map<String,TimeSlotWrapper> timeSlotWrapperMap, BigInteger phaseId, DateTimeInterval planningPeriod, Map<BigInteger,Integer> counterMap, Map<Long, DayTypeDTO> dayTypeMap, long totalTimeBank, Map<BigInteger, ActivityWrapper> activityWrapperMap, int staffAge, List<CareDaysDTO> childCareDays,List<CareDaysDTO> seniorCareDays,LocalDate lastPlanningPeriodEndDate,ExpertiseNightWorkerSetting expertiseNightWorkerSetting,boolean nightWorker, PhaseDefaultName phaseEnum, List<Integer> staffChildAges) {
        this.shifts = shifts;
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
    }

    public RuleTemplateSpecificInfo(List<ShiftWithActivityDTO> shifts, ShiftWithActivityDTO shift, Map<String,TimeSlotWrapper> timeSlotWrapperMap, BigInteger phaseId, DateTimeInterval planningPeriod, Map<BigInteger,Integer> counterMap, Map<Long, DayTypeDTO> dayTypeMap, long totalTimeBank, Map<BigInteger, ActivityWrapper> activityWrapperMap, int staffAge, List<CareDaysDTO> childCareDays,List<CareDaysDTO> seniorCareDays,LocalDate lastPlanningPeriodEndDate,ExpertiseNightWorkerSetting expertiseNightWorkerSetting,boolean nightWorker, PhaseDefaultName phaseEnum) {
        this.shifts = shifts;
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

}
