package com.kairos.service.staffing_level;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.staffing_level.*;
import com.kairos.dto.activity.staffing_level.presence.StaffingLevelActivityDetails;
import com.kairos.dto.user.skill.SkillLevelDTO;
import com.kairos.dto.user.user.staff.StaffAdditionalInfoDTO;
import com.kairos.enums.SkillLevel;
import com.kairos.enums.phase.PhaseDefaultName;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.staffing_level.StaffingLevelMongoRepository;
import com.kairos.service.phase.PhaseService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.CommonConstants.FULL_DAY_CALCULATION;
import static com.kairos.constants.CommonConstants.FULL_WEEK;

@Service
public class StaffingLevelAvailableCountService {

    @Inject
    private StaffingLevelMongoRepository staffingLevelMongoRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private PhaseService phaseService;

    @Async
    public void updateStaffingLevelAvailableCount(Shift shift, Shift oldShift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, Phase phase){
        LocalDate startDate = null;
        LocalDate endDate = null;
        Set<LocalDate> localDates = newHashSet();
        Long unitId = null;
        if(isNotNull(shift)){
            startDate = asLocalDate(shift.getStartDate());
            endDate = asLocalDate(shift.getEndDate());
            localDates.addAll(newHashSet(startDate, endDate));
            unitId = shift.getUnitId();
        }
        LocalDate oldStartDate = null;
        LocalDate oldEndDate = null;
        if(isNotNull(oldShift)){
             oldStartDate = asLocalDate(oldShift.getStartDate());
             oldEndDate = asLocalDate(oldShift.getEndDate());
             localDates.addAll(newHashSet(oldStartDate, oldEndDate));
             unitId = oldShift.getUnitId();
        }
        Map<LocalDate, StaffingLevel> staffingLevelMap = staffingLevelMongoRepository.findByUnitIdAndDates(unitId,localDates).stream().collect(Collectors.toMap(staffingLevel -> asLocalDate(staffingLevel.getCurrentDate()), v->v));
        Map<LocalDate,Set<StaffingLevelActivityDetails>> localDateSetHashMap = new HashMap<>();
        for (Map.Entry<LocalDate, StaffingLevel> staffingLevelEntry : staffingLevelMap.entrySet()) {
            localDateSetHashMap.put(staffingLevelEntry.getKey(), ObjectMapperUtils.copyCollectionPropertiesByMapper(staffingLevelEntry.getValue().getStaffingLevelActivityDetails(),StaffingLevelActivityDetails.class));
        }
        updateCount(oldShift, staffAdditionalInfoDTO, oldStartDate, oldEndDate, staffingLevelMap, true,phase);
        updateCount(shift, staffAdditionalInfoDTO, startDate, endDate, staffingLevelMap, false,phase);
        updateStaffingActivityDetails(staffingLevelMap,phase);
        staffingLevelMongoRepository.saveEntities(staffingLevelMap.values());
    }

    private void updateStaffingActivityDetails(Map<LocalDate, StaffingLevel> staffingLevelMap, Phase phase){
        for (StaffingLevel staffingLevel : staffingLevelMap.values()) {
            if (!PhaseDefaultName.REQUEST.equals(phase.getPhaseEnum())) {
                Set<StaffingLevelActivityDetails> staffingLevelActivityDetailsSet = new HashSet<>();
                for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
                    for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                        updateIfShiftDeleted(staffingLevelActivity);
                        updateIFShiftCreatedOrUpdated(staffingLevelActivity);
                        if ((staffingLevelActivity.getMinNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff()) > staffingLevelActivity.getInitialUnderStaffing()) {
                            staffingLevelActivity.setInitialUnderStaffing(staffingLevelActivity.getMinNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff());
                        }
                        if ((staffingLevelActivity.getAvailableNoOfStaff() - staffingLevelActivity.getMaxNoOfStaff()) > staffingLevelActivity.getInitialOverStaffing()) {
                            staffingLevelActivity.setInitialOverStaffing(staffingLevelActivity.getAvailableNoOfStaff() - staffingLevelActivity.getMaxNoOfStaff());
                        }
                        staffingLevelActivity.setPreviousAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff());
                        Optional<StaffingLevelActivityDetails> staffingLevelActivityDetailsOptional = staffingLevelActivityDetailsSet.stream().filter(staffingLevelActivityDetails -> staffingLevelActivityDetails.getActivityId().equals(staffingLevelActivity.getActivityId())).findFirst();
                        StaffingLevelActivityDetails staffingLevelActivityDetails;
                        if (staffingLevelActivityDetailsOptional.isPresent()) {
                            staffingLevelActivityDetails = staffingLevelActivityDetailsOptional.get();
                        } else {
                            staffingLevelActivityDetails = new StaffingLevelActivityDetails(staffingLevelActivity.getActivityId());
                        }
                        staffingLevelActivityDetails.setInitialUnderStaffing(staffingLevelActivityDetails.getInitialUnderStaffing() + staffingLevelActivity.getInitialUnderStaffing());
                        staffingLevelActivityDetails.setRemainingUnderStaffing(staffingLevelActivityDetails.getRemainingUnderStaffing() + staffingLevelActivity.getRemainingUnderStaffing());
                        staffingLevelActivityDetails.setSolvedUnderStaffing(staffingLevelActivityDetails.getSolvedUnderStaffing() + staffingLevelActivity.getSolvedUnderStaffing());
                        staffingLevelActivityDetails.setInitialOverStaffing(staffingLevelActivityDetails.getInitialOverStaffing() + staffingLevelActivity.getInitialOverStaffing());
                        staffingLevelActivityDetails.setRemainingOverStaffing(staffingLevelActivityDetails.getRemainingOverStaffing() + staffingLevelActivity.getRemainingOverStaffing());
                        staffingLevelActivityDetails.setSolvedOverStaffing(staffingLevelActivityDetails.getSolvedOverStaffing() + staffingLevelActivity.getSolvedOverStaffing());
                        staffingLevelActivityDetails.setMinNoOfStaff(staffingLevelActivityDetails.getMinNoOfStaff() + staffingLevelActivity.getMinNoOfStaff());
                        staffingLevelActivityDetails.setMaxNoOfStaff(staffingLevelActivityDetails.getMaxNoOfStaff() + staffingLevelActivity.getMaxNoOfStaff());
                        staffingLevelActivityDetails.setAvailableCount(staffingLevelActivityDetails.getAvailableCount() + staffingLevelActivity.getAvailableNoOfStaff());
                        staffingLevelActivityDetailsSet.add(staffingLevelActivityDetails);
                    }
                }
                staffingLevel.setStaffingLevelActivityDetails(staffingLevelActivityDetailsSet);
            }
        }
    }

    private void updateIfShiftDeleted(StaffingLevelActivity staffingLevelActivity) {
        if(staffingLevelActivity.getAvailableNoOfStaff() < staffingLevelActivity.getPreviousAvailableNoOfStaff()){
            int diff = staffingLevelActivity.getPreviousAvailableNoOfStaff() - staffingLevelActivity.getAvailableNoOfStaff();
            if(staffingLevelActivity.getMinNoOfStaff()>=staffingLevelActivity.getAvailableNoOfStaff()) {
                staffingLevelActivity.setSolvedUnderStaffing(Math.max(staffingLevelActivity.getSolvedUnderStaffing() - diff,0));
            }
            if(staffingLevelActivity.getMinNoOfStaff()>staffingLevelActivity.getAvailableNoOfStaff()){
                staffingLevelActivity.setRemainingUnderStaffing(staffingLevelActivity.getRemainingUnderStaffing() + diff);
            }
            if(staffingLevelActivity.getMaxNoOfStaff()<=staffingLevelActivity.getAvailableNoOfStaff()) {
                staffingLevelActivity.setSolvedOverStaffing(staffingLevelActivity.getSolvedOverStaffing() + diff);
            }
            if(staffingLevelActivity.getMaxNoOfStaff()<=staffingLevelActivity.getAvailableNoOfStaff()){
                staffingLevelActivity.setRemainingOverStaffing(staffingLevelActivity.getRemainingOverStaffing() - diff);
            }
        }
    }

    private void updateIFShiftCreatedOrUpdated(StaffingLevelActivity staffingLevelActivity) {
        if(staffingLevelActivity.getAvailableNoOfStaff()>staffingLevelActivity.getPreviousAvailableNoOfStaff()){
            int diff = staffingLevelActivity.getAvailableNoOfStaff() - staffingLevelActivity.getPreviousAvailableNoOfStaff();
            if(staffingLevelActivity.getMinNoOfStaff()>=staffingLevelActivity.getAvailableNoOfStaff()) {
                staffingLevelActivity.setSolvedUnderStaffing(staffingLevelActivity.getSolvedUnderStaffing() + diff);
            }
            if(staffingLevelActivity.getMinNoOfStaff()>staffingLevelActivity.getAvailableNoOfStaff()){
                staffingLevelActivity.setRemainingUnderStaffing(Math.max(staffingLevelActivity.getRemainingUnderStaffing() - diff,0));
            }
            if(staffingLevelActivity.getMaxNoOfStaff()<=staffingLevelActivity.getAvailableNoOfStaff()) {
                staffingLevelActivity.setSolvedOverStaffing(Math.max(staffingLevelActivity.getSolvedOverStaffing() - diff,0));

            }
            if(staffingLevelActivity.getMaxNoOfStaff()<staffingLevelActivity.getAvailableNoOfStaff()){
                staffingLevelActivity.setRemainingOverStaffing(staffingLevelActivity.getRemainingOverStaffing() + diff);
            }
        }
    }

    private void updateCount(Shift shift, StaffAdditionalInfoDTO staffAdditionalInfoDTO, LocalDate startDate, LocalDate endDate, Map<LocalDate, StaffingLevel> staffingLevelMap, boolean removedShift,Phase phase) {
        if (isNotNull(startDate) && isNotNull(shift)) {
            updatePresenceStaffingLevelAvailableStaffCount(staffingLevelMap.get(startDate), newArrayList(shift), staffAdditionalInfoDTO, removedShift,phase);
            if (!startDate.equals(endDate)) {
                updatePresenceStaffingLevelAvailableStaffCount(staffingLevelMap.get(endDate), newArrayList(shift), staffAdditionalInfoDTO, removedShift,phase);
            }
        }
    }

    public StaffingLevel updatePresenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel, List<Shift> shifts, StaffAdditionalInfoDTO staffAdditionalInfoDTO,boolean removedShift,Phase phase) {
        for (Shift shift : shifts) {
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                if ((FULL_WEEK.equals(shiftActivity.getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(shiftActivity.getMethodForCalculatingTime()))) {
                    updateAbsenceStaffingLevelAvailableStaffCount(staffingLevel, shiftActivity.getActivityId(),removedShift);
                } else {
                    int durationMinutes = staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                    updateStaffingLevelInterval(shift.getBreakActivities(), durationMinutes, staffingLevel, shiftActivity, shift.getStaffId(), staffAdditionalInfoDTO,removedShift,phase);
                }
            }
        }
        return staffingLevel;
    }

    private StaffingLevel updateAbsenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel, BigInteger activityId,boolean removedShift) {
        if (!staffingLevel.getAbsenceStaffingLevelInterval().isEmpty()) {
            StaffingLevelInterval absenceStaffingLevelInterval = staffingLevel.getAbsenceStaffingLevelInterval().get(0);
            absenceStaffingLevelInterval.setAvailableNoOfStaff(absenceStaffingLevelInterval.getAvailableNoOfStaff() + 1);
            updateInnerAbsenceStaffingAvailableNoOfStaff(absenceStaffingLevelInterval, activityId,removedShift);
        } else {
            Duration duration = new Duration(LocalTime.MIN, LocalTime.MAX);
            StaffingLevelInterval absenceStaffingLevelInterval = new StaffingLevelInterval(0, 0, duration, 1);
            staffingLevel.getAbsenceStaffingLevelInterval().add(absenceStaffingLevelInterval);
        }
        return staffingLevel;
    }

    private void updateInnerAbsenceStaffingAvailableNoOfStaff(StaffingLevelInterval absenceStaffingLevelInterval, BigInteger activityId, boolean removedShift) {
        for (StaffingLevelActivity staffingLevelActivity : absenceStaffingLevelInterval.getStaffingLevelActivities()) {
            if (activityId.equals(staffingLevelActivity.getActivityId())) {
                staffingLevelActivity.setAvailableNoOfStaff(removedShift ? staffingLevelActivity.getAvailableNoOfStaff() - 1 : staffingLevelActivity.getAvailableNoOfStaff() + 1);
            }
        }
    }

    private void updateStaffingLevelInterval(List<ShiftActivity> breakActivities, int durationMinutes, StaffingLevel staffingLevel, ShiftActivity shiftActivity, Long staffId, StaffAdditionalInfoDTO staffAdditionalInfoDTO,boolean removedShift,Phase phase) {
        for (StaffingLevelInterval staffingLevelInterval : staffingLevel.getPresenceStaffingLevelInterval()) {
            Date startDate = getDateByLocalTime(staffingLevel.getCurrentDate(), staffingLevelInterval.getStaffingLevelDuration().getFrom());
            Date endDate = staffingLevelInterval.getStaffingLevelDuration().getFrom().isAfter(staffingLevelInterval.getStaffingLevelDuration().getTo()) ? asDate(asLocalDate(staffingLevel.getCurrentDate()).plusDays(1)) : getDateByLocalTime(staffingLevel.getCurrentDate(), staffingLevelInterval.getStaffingLevelDuration().getTo());
            DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
            updateShiftActivityStaffingLevel(durationMinutes, shiftActivity, staffingLevelInterval, interval, breakActivities,removedShift,phase);
            int availableNoOfStaff = staffingLevelInterval.getStaffingLevelActivities().stream().mapToInt(staffingLevelActivity -> staffingLevelActivity.getAvailableNoOfStaff()).sum();
            staffingLevelInterval.setAvailableNoOfStaff(availableNoOfStaff);
            if (isCollectionNotEmpty(staffingLevelInterval.getStaffingLevelSkills()) && isNotNull(staffAdditionalInfoDTO)) {
                List<SkillLevelDTO> skillLevelDTOS = staffAdditionalInfoDTO.getSkillsByLocalDate(asLocalDate(shiftActivity.getStartDate()));
                updateStaffingLevelSkills(staffingLevelInterval,skillLevelDTOS, interval, shiftActivity);
            }
        }
    }

    private void updateStaffingLevelSkills(StaffingLevelInterval staffingLevelInterval, List<SkillLevelDTO> skillLevelDTOS, DateTimeInterval interval, ShiftActivity shiftActivity) {
        for (StaffingLevelSkill staffingLevelSkill : staffingLevelInterval.getStaffingLevelSkills()) {
            for (SkillLevelDTO staffSkill : skillLevelDTOS) {
                if (isNotNull(staffingLevelSkill.getSkillId()) && staffingLevelSkill.getSkillId().equals(staffSkill.getSkillId()) && interval.overlaps(shiftActivity.getInterval())) {
                    updateAvailableNoOfSkill(staffingLevelSkill, staffSkill);
                }
            }
        }
    }

    private void updateAvailableNoOfSkill(StaffingLevelSkill staffingLevelSkill, SkillLevelDTO staffSkill) {
        SkillLevelSetting basicSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.BASIC);
        SkillLevelSetting advanceSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.ADVANCE);
        SkillLevelSetting expertSkillLevelSetting = staffingLevelSkill.getSkillLevelSettingBySkillLevel(SkillLevel.EXPERT);
        if (isNotNull(basicSkillLevelSetting) && SkillLevel.BASIC.toString().equals(staffSkill.getSkillLevel())) {
            basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff() + 1);
        } else if (isNotNull(advanceSkillLevelSetting) && SkillLevel.ADVANCE.toString().equals(staffSkill.getSkillLevel())) {
            if (advanceSkillLevelSetting.getNoOfStaff() > advanceSkillLevelSetting.getAvailableNoOfStaff() || basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff()) {
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff() + 1);
            } else if (basicSkillLevelSetting.getNoOfStaff() > basicSkillLevelSetting.getAvailableNoOfStaff()) {
                basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff() + 1);
            } else {
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff() + 1);
            }
        } else if(isNotNull(expertSkillLevelSetting)){
            if (expertSkillLevelSetting.getNoOfStaff() > expertSkillLevelSetting.getAvailableNoOfStaff() || (advanceSkillLevelSetting.getNoOfStaff() <= advanceSkillLevelSetting.getAvailableNoOfStaff() && basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff())) {
                expertSkillLevelSetting.setAvailableNoOfStaff(expertSkillLevelSetting.getAvailableNoOfStaff() + 1);
            } else if (advanceSkillLevelSetting.getNoOfStaff() > advanceSkillLevelSetting.getAvailableNoOfStaff() || basicSkillLevelSetting.getNoOfStaff() <= basicSkillLevelSetting.getAvailableNoOfStaff()) {
                advanceSkillLevelSetting.setAvailableNoOfStaff(advanceSkillLevelSetting.getAvailableNoOfStaff() + 1);
            } else if (basicSkillLevelSetting.getNoOfStaff() > basicSkillLevelSetting.getAvailableNoOfStaff()) {
                basicSkillLevelSetting.setAvailableNoOfStaff(basicSkillLevelSetting.getAvailableNoOfStaff() + 1);
            } else {
                expertSkillLevelSetting.setAvailableNoOfStaff(expertSkillLevelSetting.getAvailableNoOfStaff() + 1);
            }
        }
    }

    private void updateShiftActivityStaffingLevel(int durationMinutes, ShiftActivity shiftActivity, StaffingLevelInterval staffingLevelInterval, DateTimeInterval interval, List<ShiftActivity> breakActivities,boolean removedShift,Phase phase) {
        boolean breakValid = breakActivities.stream().anyMatch(shiftActivity1 -> !shiftActivity1.isBreakNotHeld() && interval.overlaps(shiftActivity1.getInterval()) && interval.overlap(shiftActivity1.getInterval()).getMinutes() >= durationMinutes);
        Optional<StaffingLevelActivity> staffingLevelActivityOptional = staffingLevelInterval.getStaffingLevelActivities().stream().filter(staffingLevelActivity -> staffingLevelActivity.getActivityId().equals(shiftActivity.getActivityId())).findFirst();
        if (!breakValid && interval.overlaps(shiftActivity.getInterval()) && interval.overlap(shiftActivity.getInterval()).getMinutes() >= durationMinutes) {
            if(staffingLevelActivityOptional.isPresent()){
                StaffingLevelActivity staffingLevelActivity = staffingLevelActivityOptional.get();
                if(removedShift){
                    staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() - 1);
                } else {
                    staffingLevelActivity.setAvailableNoOfStaff(staffingLevelActivity.getAvailableNoOfStaff() + 1);
                }
            }
        }
    }


    @Async
    public StaffingLevel updatePresenceStaffingLevelAvailableStaffCount(StaffingLevel staffingLevel) {
        List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(getStartOfDay(staffingLevel.getCurrentDate()), getEndOfDay(staffingLevel.getCurrentDate()), newArrayList(staffingLevel.getUnitId()));
        resetAvailableCount(staffingLevel);
        for (Shift shift : shifts) {
            Phase phase = phaseService.getCurrentPhaseByUnitIdAndDate(shift.getUnitId(),shift.getStartDate(),shift.getEndDate());
            for (ShiftActivity shiftActivity : shift.getActivities()) {
                if ((FULL_WEEK.equals(shiftActivity.getMethodForCalculatingTime()) || FULL_DAY_CALCULATION.equals(shiftActivity.getMethodForCalculatingTime()))) {
                    updateAbsenceStaffingLevelAvailableStaffCount(staffingLevel, shiftActivity.getId(),false);
                } else {
                    int durationMinutes = staffingLevel.getStaffingLevelSetting().getDefaultDetailLevelMinutes();
                    updateStaffingLevelInterval(shift.getBreakActivities(), durationMinutes, staffingLevel, shiftActivity, shift.getStaffId(), null,false,phase);
                }
            }
        }
        staffingLevelMongoRepository.save(staffingLevel);
        return staffingLevel;
    }

    private void resetAvailableCount(StaffingLevel staffingLevel) {
        staffingLevel.getPresenceStaffingLevelInterval().parallelStream().forEach(staffingLevelInterval -> {
            staffingLevelInterval.setAvailableNoOfStaff(0);
            staffingLevelInterval.getStaffingLevelActivities().stream().forEach(staffingLevelActivity -> staffingLevelActivity.setAvailableNoOfStaff(0));
        });
    }
}
