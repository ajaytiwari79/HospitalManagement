package com.kairos.rule_validator.activity.specification;
/*
 *Created By Pavan on 9/8/19
 *
 */

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.user.access_group.UserAccessRoleDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.ActivityWrapper;
import com.kairos.persistence.model.phase.Phase;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.persistence.model.staffing_level.StaffingLevelActivityRank;
import com.kairos.persistence.model.unit_settings.PhaseSettings;
import com.kairos.rule_validator.AbstractSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.constants.AppConstants.BALANCED;
import static com.kairos.constants.AppConstants.OVERSTAFFING;
import static com.kairos.constants.AppConstants.UNDERSTAFFING;
import static com.kairos.service.shift.ShiftValidatorService.throwException;

public class StaffingLevelAndRankSpecification extends AbstractSpecification<ShiftWithActivityDTO> {
    private static final Logger logger=LoggerFactory.getLogger(StaffingLevelAndRankSpecification.class);
    private List<ShiftActivityDTO> shiftActivities;
    private Long unitId;
    private Collection<ActivityWrapper> activities;
    private Phase phase;
    private UserAccessRoleDTO userAccessRoleDTO;
    private PhaseSettings phaseSettings;

    public StaffingLevelAndRankSpecification(List<ShiftActivityDTO> shiftActivities,Long unitId, Collection<ActivityWrapper> activities, Phase phase, UserAccessRoleDTO userAccessRoleDTO) {
    }

    @Override
    public boolean isSatisfied(ShiftWithActivityDTO shiftWithActivityDTO) {
        return false;
    }

    @Override
    public void validateRules(ShiftWithActivityDTO shiftWithActivityDTO) {
        if (!shiftActivities.isEmpty()) {
            PhaseSettings phaseSettings = phaseSettingsRepository.getPhaseSettingsByUnitIdAndPhaseId(unitId, phase.getId());
            if (!phaseSettings.isManagementEligibleForOverStaffing() || !phaseSettings.isManagementEligibleForUnderStaffing() || !phaseSettings.isStaffEligibleForOverStaffing() || !phaseSettings.isStaffEligibleForUnderStaffing()) {
                Activity existing = activities.stream().filter(k -> k.getActivity().getId().equals(shiftActivities.get(0).getActivityId())).findFirst().get().getActivity();
                Activity arrived = activities.stream().filter(k -> k.getActivity().getId().equals(shiftActivities.get(1).getActivityId())).findFirst().get().getActivity();
                if (existing.getRulesActivityTab().isEligibleForStaffingLevel() && arrived.getRulesActivityTab().isEligibleForStaffingLevel()) {
                    Date startDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftActivities.get(0).getStartDate()).truncatedTo(ChronoUnit.DAYS));
                    Date endDate = DateUtils.getDateByZoneDateTime(DateUtils.asZoneDateTime(shiftActivities.get(0).getEndDate()).truncatedTo(ChronoUnit.DAYS));
                    List<StaffingLevel> staffingLevels = staffingLevelMongoRepository.findByUnitIdAndDates(unitId, startDate, endDate);
                    if (!Optional.ofNullable(staffingLevels).isPresent() || staffingLevels.isEmpty()) {
                        throwException(MESSAGE_STAFFINGLEVEL_ABSENT);
                    }
                    List<Shift> shifts = shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(shiftActivities.get(0).getStartDate(), shiftActivities.get(0).getEndDate(), unitId);
                    StaffingLevelActivityRank rankOfExisting = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(asLocalDate(shiftActivities.get(0).getStartDate()), shiftActivities.get(0).getActivityId());
                    StaffingLevelActivityRank rankOfReplaced = staffingLevelActivityRankRepository.findByStaffingLevelDateAndActivityId(asLocalDate(shiftActivities.get(1).getStartDate()), shiftActivities.get(1).getActivityId());
                    String staffingLevelForExistingActivity = getStaffingLevel(existing, staffingLevels, shifts, false);
                    String staffingLevelForReplacedActivity = getStaffingLevel(arrived, staffingLevels, shifts, true);
                    boolean checkForStaff = userAccessRoleDTO.getStaff();
                    boolean checkForManagement = userAccessRoleDTO.getManagement();
                    if (rankOfExisting != null && rankOfReplaced != null) {
                        logger.info("validating ranking of activities");
                        boolean phaseSettingsValidated = false;
                        if ((checkForStaff && checkForManagement) && (!phaseSettings.isStaffEligibleForUnderStaffing() && !phaseSettings.isManagementEligibleForUnderStaffing())) {
                            phaseSettingsValidated = true;
                        } else if (checkForStaff ? !phaseSettings.isStaffEligibleForUnderStaffing() : !phaseSettings.isManagementEligibleForUnderStaffing()) {
                            phaseSettingsValidated = true;
                        }
                        if (phaseSettingsValidated && (rankOfExisting.getRank() > rankOfReplaced.getRank() && UNDERSTAFFING.equals(staffingLevelForExistingActivity) && UNDERSTAFFING.equals(staffingLevelForReplacedActivity))
                                || (rankOfExisting.getRank() > rankOfReplaced.getRank() && BALANCED.equals(staffingLevelForReplacedActivity))
                                || (BALANCED.equals(staffingLevelForReplacedActivity) && BALANCED.equals(staffingLevelForExistingActivity))
                                || (staffingLevelForReplacedActivity == null && rankOfExisting.getRank() > rankOfReplaced.getRank())
                                ) {
                            logger.info("shift can be replaced");
                        } else {
                            throwException(SHIFT_CAN_NOT_MOVE, staffingLevelForReplacedActivity);
                        }

                    }
                }
            }
        }

    }


    private String getStaffingLevel(Activity activity, List<StaffingLevel> staffingLevels, List<Shift> shifts, boolean addShift) {
        String staffingLevelStatus = null;
        if (activity.getRulesActivityTab().isEligibleForStaffingLevel()) {
            for (StaffingLevel staffingLevel : staffingLevels) {
                List<StaffingLevelInterval> staffingLevelIntervals = (activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_DAY_CALCULATION) ||
                        activity.getTimeCalculationActivityTab().getMethodForCalculatingTime().equals(CommonConstants.FULL_WEEK)) ? staffingLevel.getAbsenceStaffingLevelInterval() : staffingLevel.getPresenceStaffingLevelInterval();
                for (StaffingLevelInterval staffingLevelInterval : staffingLevelIntervals) {
                    int shiftsCount = 0;
                    Optional<StaffingLevelActivity> staffingLevelActivity = staffingLevelInterval.getStaffingLevelActivities().stream().filter(sa -> sa.getActivityId().equals(activity.getId())).findFirst();
                    if (staffingLevelActivity.isPresent()) {
                        ZonedDateTime startDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getFrom());
                        ZonedDateTime endDate = ZonedDateTime.ofInstant(staffingLevel.getCurrentDate().toInstant(), ZoneId.systemDefault()).with(staffingLevelInterval.getStaffingLevelDuration().getTo());
                        DateTimeInterval interval = new DateTimeInterval(startDate, endDate);
                        boolean overlapped = false;
                        for (Shift shift : shifts) {
                            if (shift.getActivities().get(0).getActivityId().equals(activity.getId()) && interval.overlaps(shift.getInterval())) {
                                shiftsCount++;
                                overlapped = true;
                            }
                        }
                        if (overlapped) {
                            shiftsCount = addShift ? shiftsCount + 1 : shiftsCount - 1;
                            if (shiftsCount > staffingLevelActivity.get().getMaxNoOfStaff()) {
                                staffingLevelStatus = OVERSTAFFING;
                                break;
                            } else if (shiftsCount < staffingLevelActivity.get().getMinNoOfStaff()) {
                                staffingLevelStatus = UNDERSTAFFING;
                                break;
                            } else {
                                staffingLevelStatus = BALANCED;
                            }
                        }
                    } else {
                        exceptionService.actionNotPermittedException(MESSAGE_STAFFINGLEVEL_ACTIVITY, activity.getName());
                    }

                }
            }
        }
        return staffingLevelStatus;

    }

    @Override
    public List<String> isSatisfiedString(ShiftWithActivityDTO shiftWithActivityDTO) {
        return null;
    }
}
