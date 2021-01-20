package com.kairos.persistence.model.shift;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.break_settings.BreakSettingsDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.period.PlanningPeriodDTO;
import com.kairos.dto.activity.unit_settings.activity_configuration.ActivityConfigurationDTO;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.night_worker.ExpertiseNightWorkerSetting;
import com.kairos.persistence.model.night_worker.NightWorker;
import com.kairos.persistence.model.period.PlanningPeriod;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.time_slot.TimeSlotSet;
import com.kairos.persistence.model.wta.StaffWTACounter;
import com.kairos.persistence.model.wta.WTAQueryResultDTO;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.service.shift.ShiftValidatorService.throwException;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ShiftDataHelper {
    private List<DayTypeDTO> dayTypes;
    private List<ActivityConfigurationDTO> activityConfigurations;
    private List<ActivityDTO> activities;
    private Map<BigInteger,ActivityDTO> activityMap;
    private List<NightWorker> nightWorkers;
    private Map<Long,NightWorker> nightWorkerMap;
    private List<StaffWTACounter> staffWTACounters;
    private Map<Long,List<StaffWTACounter>> staffWTACountersMap;
    private Shift shift;
    private List<ExpertiseNightWorkerSetting> expertiseNightWorkerSettings;
    private Map<Long,ExpertiseNightWorkerSetting> expertiseNightWorkerSettingMap;
    private List<ExpertiseNightWorkerSetting> countryExpertiseNightWorkerSettings;
    private Map<Long,ExpertiseNightWorkerSetting> countryExpertiseNightWorkerSettingMap;
    private List<WTAQueryResultDTO> workingTimeAgreements;
    private Map<Long,List<WTAQueryResultDTO>> workingTimeAgreementMap;
    private List<CTAResponseDTO> costTimeAgreements;
    private Map<Long,List<CTAResponseDTO>> costTimeAgreementMap;
    private TimeSlotSet timeSlot;
    private PlanningPeriodDTO planningPeriod;
    private List<Phase> phases;
    private List<BreakSettingsDTO> breakSettings;
    private Map<Long, BreakSettingsDTO> breakSettingsMap;
    private Phase tentativePhase;
    private List<Phase> actualPhases;
    private String timeZone;
    private List<PlanningPeriod> planningPeriods;
    private Map<LocalDate,BigInteger> localDatePhaseMap;
    private DateTimeInterval planningPeriodInterval;
    private Map<LocalDate, Boolean> dateAndPublishPlanningPeriod;
    private Map<LocalDate, PhaseDefaultName> dateAndPhaseDefaultName;
    private Map<LocalDate,Phase> phaseMap;

    public WTAQueryResultDTO getWtaByDate(LocalDate localDate,Long employmentId){
        if(isMapEmpty(workingTimeAgreementMap)) {
            workingTimeAgreementMap = workingTimeAgreements.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(),Collectors.toList()));
        }if(workingTimeAgreementMap.containsKey(employmentId)){
            return workingTimeAgreementMap.get(employmentId).stream().filter(wta -> wta.isValidWorkTimeAgreement(localDate)).findFirst().orElse(null);
        }
        return null;
    }

    public Map<Long, List<WTAQueryResultDTO>> getWorkingTimeAgreementMap() {
        if(isMapEmpty(workingTimeAgreementMap)) {
            workingTimeAgreementMap = workingTimeAgreements.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(),Collectors.toList()));
        }
        return workingTimeAgreementMap;
    }

    public Map<Long, List<CTAResponseDTO>> getCostTimeAgreementMap() {
        if(isMapEmpty(costTimeAgreementMap)) {
            costTimeAgreementMap = costTimeAgreements.stream().collect(Collectors.groupingBy(wtaQueryResultDTO -> wtaQueryResultDTO.getEmploymentId(),Collectors.toList()));
        }
        return costTimeAgreementMap;
    }

    public CTAResponseDTO getCtaByDate(LocalDate localDate, Long employmentId){
        if(isMapEmpty(costTimeAgreementMap)) {
            costTimeAgreementMap = costTimeAgreements.stream().collect(Collectors.groupingBy(cta -> cta.getEmploymentId(),Collectors.toList()));
        }if(costTimeAgreementMap.containsKey(employmentId)){
            return costTimeAgreementMap.get(employmentId).stream().filter(cta -> cta.isValidCostTimeAgreement(localDate)).findFirst().orElse(null);
        }
        return null;
    }

    public ActivityDTO getActivityById(BigInteger activityId){
        if(isMapEmpty(activityMap)){
            activityMap = activities.stream().collect(Collectors.toMap(activityDTO -> activityDTO.getId(),v->v));
        }if(activityMap.containsKey(activityId)){
            return activityMap.get(activityId);
        }
        throwException(MESSAGE_ACTIVITY_ID,activityId);
        return null;
    }

    public Map<BigInteger, ActivityDTO> getActivityMap() {
        if(isMapEmpty(activityMap)){
            activityMap = activities.stream().collect(Collectors.toMap(activityDTO -> activityDTO.getId(),v->v));
        }
        return activityMap;
    }

    public NightWorker getNightWorkerByStaffId(Long staffId){
        if(isMapEmpty(nightWorkerMap)){
            nightWorkerMap = nightWorkers.stream().collect(Collectors.toMap(k->k.getStaffId(),v->v));
        }if (nightWorkerMap.containsKey(staffId)) {
            return nightWorkerMap.get(staffId);
        }
        return null;
    }

    public ExpertiseNightWorkerSetting getExpertiseNightWorkerSettingByExpertiseId(Long expertiseId){
        if(isMapEmpty(expertiseNightWorkerSettingMap)){
            expertiseNightWorkerSettingMap = expertiseNightWorkerSettings.stream().collect(Collectors.toMap(k->k.getExpertiseId(),v->v));
        }if (expertiseNightWorkerSettingMap.containsKey(expertiseId)) {
            return expertiseNightWorkerSettingMap.get(expertiseId);
        }if(isMapEmpty(countryExpertiseNightWorkerSettingMap)){
            countryExpertiseNightWorkerSettingMap = countryExpertiseNightWorkerSettings.stream().collect(Collectors.toMap(k->k.getExpertiseId(),v->v));
        }if(countryExpertiseNightWorkerSettingMap.containsKey(expertiseId)){
            return countryExpertiseNightWorkerSettingMap.get(expertiseId);
        }
        throwException(MESSAGE_NIGHTWORKER_SETTING_NOTFOUND,expertiseId);
        return null;
    }

    public PlanningPeriodDTO getPlanningPeriod(LocalDate localDate){
        if(isNull(planningPeriod)){
            throwException(MESSAGE_SHIFT_PLANNING_PERIOD_EXITS,localDate);
        }
        return planningPeriod;
    }

    public List<StaffWTACounter> getStaffWTACounter(Long employementId){
        if(isMapEmpty(staffWTACountersMap)){
            staffWTACountersMap = staffWTACounters.stream().collect(Collectors.groupingBy(staffWTACounter -> staffWTACounter.getEmploymentId()));
        }if(staffWTACountersMap.containsKey(employementId)){
            return staffWTACountersMap.get(employementId);
        }
        return new ArrayList<>();
    }

    public BreakSettingsDTO getBreakSetting(Long expertiseId){
        if(isMapEmpty(breakSettingsMap)){
            breakSettingsMap = breakSettings.stream().collect(Collectors.toMap(breakSettingsDTO -> breakSettingsDTO.getExpertiseId(),v->v));
        }if(breakSettingsMap.containsKey(expertiseId)){
            return breakSettingsMap.get(expertiseId);
        }
        return null;
    }

    public Phase getTentativePhase(){
        if(isNull(tentativePhase)){
            tentativePhase = phases.stream().filter(phase -> phase.getPhaseEnum().equals(PhaseDefaultName.TENTATIVE)).findFirst().get();
        }
        return tentativePhase;
    }

    public List<Phase> getActualPhases(){
        if(isCollectionEmpty(actualPhases)){
            actualPhases = phases.stream().filter(phase -> phase.getPhaseType().equals(PhaseType.ACTUAL)).collect(Collectors.toList());
        }
        return actualPhases;
    }

    public Map<LocalDate,BigInteger> getDatePhaseIdMap() {
        if(isMapEmpty(localDatePhaseMap)){
            localDatePhaseMap = new HashMap<>();
            for (PlanningPeriod period : planningPeriods) {
                localDatePhaseMap.putAll(period.getLocalDatePhaseIdMap());
            }
        }
        return localDatePhaseMap;
    }

    public DateTimeInterval getPlanningPeriodDateTimeInterval() {
        if(isNull(planningPeriodInterval)){
            planningPeriodInterval = new DateTimeInterval(planningPeriods.get(0).getStartDate(),planningPeriods.get(planningPeriods.size()-1).getEndDate());
        }
        return planningPeriodInterval;
    }

    public Map<java.time.LocalDate, Boolean> getDateWisePublishPlanningPeriod(Long employmentTypeId, java.time.LocalDate startDate, java.time.LocalDate endDate, Long unitId) {
        if(isMapEmpty(dateAndPublishPlanningPeriod)){
            Map<java.time.LocalDate, Boolean> dateAndPublishPlanningPeriod = new HashMap<>();
            boolean publish;
            for (PlanningPeriod planningPeriod : this.planningPeriods) {
                publish = planningPeriod.getPublishEmploymentIds().contains(employmentTypeId);
                startDate = planningPeriod.getStartDate();
                endDate = planningPeriod.getEndDate();
                while (!startDate.isAfter(endDate)) {
                    dateAndPublishPlanningPeriod.put(startDate, publish);
                    startDate = startDate.plusDays(1);
                }
            }
        }
        return dateAndPublishPlanningPeriod;
    }

    public Map<java.time.LocalDate, PhaseDefaultName> getDatePhaseDefaultName() {
        if(isMapEmpty(dateAndPhaseDefaultName)){
            dateAndPhaseDefaultName = phaseMap.entrySet().stream().collect(Collectors.toMap(localDatePhaseEntry -> localDatePhaseEntry.getKey(),v->v.getValue().getPhaseEnum()));
        }
        return dateAndPhaseDefaultName;
    }

    public Map<java.time.LocalDate, Boolean> getDateAndPublishPlanningPeriod(Long employmentTypeId) {
        if(isMapEmpty(dateAndPublishPlanningPeriod)){
            dateAndPublishPlanningPeriod = new HashMap<>();
            planningPeriods.forEach(planningPeriod1 -> dateAndPublishPlanningPeriod.putAll(planningPeriod1.getLocalDatePublishPlanningMap(employmentTypeId)));
        }
        return dateAndPublishPlanningPeriod;
    }
}

