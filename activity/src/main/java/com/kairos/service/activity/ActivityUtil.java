package com.kairos.service.activity;
/*
 *Created By Pavan on 29/10/18
 *
 */

import com.kairos.constants.AppConstants;
import com.kairos.constants.CommonConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.activity.glide_time.GlideTimeSettingsDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.LocationEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.ActivityRulesSettings;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.PQLSettings;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ActivityUtil {

    private static ActivityLocationSettings initializeLocationActivitySettings(GlideTimeSettingsDTO glideTimeSettingsDTO){
        Set<ActivityGlideTimeDetails> activityGlideTimeDetailsForCheckIn =new HashSet<>();
        ActivityGlideTimeDetails activityGlideTimeDetailsForHome =new ActivityGlideTimeDetails(LocationEnum.HOME,glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckIn().getAfter());
        ActivityGlideTimeDetails activityGlideTimeDetailsForUnit =new ActivityGlideTimeDetails(LocationEnum.OFFICE,glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckIn().getAfter());
        ActivityGlideTimeDetails activityGlideTimeDetailsForDepot =new ActivityGlideTimeDetails(LocationEnum.DEPOT,glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckIn().getAfter());
        ActivityGlideTimeDetails activityGlideTimeDetailsForOther =new ActivityGlideTimeDetails(LocationEnum.OTHERS,glideTimeSettingsDTO.getGlideTimeForCheckIn().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckIn().getAfter());
        activityGlideTimeDetailsForCheckIn.add(activityGlideTimeDetailsForHome);
        activityGlideTimeDetailsForCheckIn.add(activityGlideTimeDetailsForUnit);
        activityGlideTimeDetailsForCheckIn.add(activityGlideTimeDetailsForDepot);
        activityGlideTimeDetailsForCheckIn.add(activityGlideTimeDetailsForOther);


        Set<ActivityGlideTimeDetails> activityGlideTimeDetailsForCheckOut =new HashSet<>();
        ActivityGlideTimeDetails activityCheckOutGlideTimeDetailsForHome =new ActivityGlideTimeDetails(LocationEnum.HOME,glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter());
        ActivityGlideTimeDetails activityCheckOutGlideTimeDetailsForUnit =new ActivityGlideTimeDetails(LocationEnum.OFFICE,glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter());
        ActivityGlideTimeDetails activityCheckOutGlideTimeDetailsForDepot =new ActivityGlideTimeDetails(LocationEnum.DEPOT,glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter());
        ActivityGlideTimeDetails activityCheckOutGlideTimeDetailsForOther =new ActivityGlideTimeDetails(LocationEnum.OTHERS,glideTimeSettingsDTO.getGlideTimeForCheckOut().getBefore(),glideTimeSettingsDTO.getGlideTimeForCheckOut().getAfter());
        activityGlideTimeDetailsForCheckOut.add(activityCheckOutGlideTimeDetailsForHome);
        activityGlideTimeDetailsForCheckOut.add(activityCheckOutGlideTimeDetailsForUnit);
        activityGlideTimeDetailsForCheckOut.add(activityCheckOutGlideTimeDetailsForDepot);
        activityGlideTimeDetailsForCheckOut.add(activityCheckOutGlideTimeDetailsForOther);
        return new ActivityLocationSettings(activityGlideTimeDetailsForCheckIn, activityGlideTimeDetailsForCheckOut);
    }

    public static List<CutOffInterval> getCutoffInterval(LocalDate dateFrom, CutOffIntervalUnit cutOffIntervalUnit, Integer dayValue) {
        LocalDate startDate = dateFrom;
        LocalDate endDate = startDate.plusYears(1);
        List<CutOffInterval> cutOffIntervals = new ArrayList<>();
        updateCutOff(cutOffIntervalUnit, dayValue, startDate, endDate, cutOffIntervals);
        return cutOffIntervals;
    }

    private static void updateCutOff(CutOffIntervalUnit cutOffIntervalUnit, Integer dayValue, LocalDate startDate, LocalDate endDate, List<CutOffInterval> cutOffIntervals) {
        while (startDate.isBefore(endDate)) {
            LocalDate nextEndDate = startDate;
            switch (cutOffIntervalUnit) {
                case DAYS:
                    nextEndDate = startDate.plusDays(dayValue - 1);
                    break;
                case HALF_YEARLY:
                    nextEndDate = startDate.plusMonths(6).minusDays(1);
                    break;
                case WEEKS:
                    nextEndDate = startDate.plusWeeks(1).minusDays(1);
                    break;
                case MONTHS:
                    nextEndDate = startDate.plusMonths(1).minusDays(1);
                    break;
                case QUARTERS:
                    nextEndDate = startDate.plusMonths(3).minusDays(1);
                    break;
                case YEARS:
                    nextEndDate = startDate.plusYears(1).minusDays(1);
                    break;
                default:
                    break;
            }
            cutOffIntervals.add(new CutOffInterval(startDate, nextEndDate));
            startDate = nextEndDate.plusDays(1);
        }
    }

    public static String durationCalculationMethod(String method) {
        String calculationType = null;
        switch (method) {
            case FIXED_TIME_FOR_TIMECARE:
                calculationType = FIXED_TIME;
                break;
            case WEEKLY_WORK_TIME:
                calculationType = CommonConstants.FULL_DAY_CALCULATION;
                break;
            case FULL_TIME_HOUR:
                calculationType = WEEKLY_HOURS;
                break;
            case CALCULATED_TIME:
                calculationType = ENTERED_TIMES;
                break;
            default:
                break;
        }
        return calculationType;
    }

    public static Activity buildActivity(ActivityDTO activityDTO) {
        List<BigInteger> tags = new ArrayList<>(activityDTO.getTags());
        return new Activity(activityDTO.getName(), activityDTO.getDescription(), tags);
    }

    public static List<PhaseTemplateValue> getPhaseForRulesActivity(List<PhaseDTO> phases) {
        List<PhaseTemplateValue> phaseTemplateValues = new ArrayList<>();
        for (PhaseDTO phaseDTO : phases) {
            PhaseTemplateValue phaseTemplateValue = new PhaseTemplateValue();
            phaseTemplateValue.setPhaseId(phaseDTO.getId());
            phaseTemplateValue.setName(phaseDTO.getName());
            phaseTemplateValue.setDescription(phaseDTO.getDescription());
            phaseTemplateValue.setEligibleForManagement(false);
            phaseTemplateValue.setEligibleEmploymentTypes(new ArrayList<>());
            phaseTemplateValue.setAllowedSettings(new AllowedSettings());
            phaseTemplateValues.add(phaseTemplateValue);
        }
        return phaseTemplateValues;
    }

    public static Activity initializeTimeCareActivities(TimeCareActivity timeCareActivity,Long orgType,List<Long> orgSubTypes,Long countryId,GlideTimeSettingsDTO glideTimeSettingsDTO,List<PhaseDTO> phases,List<Activity> activitiesByExternalIds
            ,ActivityCategory activityCategory,List<Skill> skills, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId){
        Optional<Activity> result = activitiesByExternalIds.stream().filter(activityByExternalId -> timeCareActivity.getId().equals(activityByExternalId.getExternalId())).findFirst();
        Activity activity = getActivity(timeCareActivity, orgType, orgSubTypes, countryId, result);
        //general settings
        ActivityGeneralSettings activityGeneralSettings = getGeneralActivitySettings(timeCareActivity, activityCategory, activity);
        activity.setActivityGeneralSettings(activityGeneralSettings);

        //balance setting settings
        ActivityBalanceSettings activityBalanceSettings = getBalanceSettingsActivitySettings(timeCareActivity, presenceTimeTypeId, absenceTimeTypeId, activity);
        activity.setActivityBalanceSettings(activityBalanceSettings);
        //rules activity settings
        ActivityRulesSettings activityRulesSettings = getRulesActivitySettings(timeCareActivity, phases, activity);
        activity.setActivityRulesSettings(activityRulesSettings);
        // location settings
        activity.setActivityLocationSettings(ActivityUtil.initializeLocationActivitySettings(glideTimeSettingsDTO));
        //Time calculation settings
        ActivityTimeCalculationSettings activityTimeCalculationSettings = getTimeCalculationActivitySettings(timeCareActivity, activity);
        activity.setActivityTimeCalculationSettings(activityTimeCalculationSettings);
        ActivitySkillSettings activitySkillSettings = getSkillActivitySettings(timeCareActivity, skills, activity);
        activity.setActivitySkillSettings(activitySkillSettings);
        return activity;
    }

    private static Activity getActivity(TimeCareActivity timeCareActivity, Long orgType, List<Long> orgSubTypes, Long countryId, Optional<Activity> result) {
        Activity activity = result.orElseGet(Activity::new);
        activity.setCountryId(countryId);
        activity.setParentActivity(true);
        activity.setState(ActivityStateEnum.LIVE);
        activity.setName(timeCareActivity.getName());
        activity.setOrganizationTypes(Collections.singletonList(orgType));
        activity.setOrganizationSubTypes(orgSubTypes);
        activity.setExternalId(timeCareActivity.getId());
        return activity;
    }

    private static ActivityGeneralSettings getGeneralActivitySettings(TimeCareActivity timeCareActivity, ActivityCategory activityCategory, Activity activity) {
        ActivityGeneralSettings activityGeneralSettings = (Optional.ofNullable(activity.getActivityGeneralSettings()).isPresent()) ? activity.getActivityGeneralSettings() :
                new ActivityGeneralSettings();
        activityGeneralSettings.setName(activity.getName());
        activityGeneralSettings.setShortName(timeCareActivity.getShortName());
        activityGeneralSettings.setCategoryId(activityCategory.getId());
        return activityGeneralSettings;
    }

    private static ActivitySkillSettings getSkillActivitySettings(TimeCareActivity timeCareActivity, List<Skill> skills, Activity activity) {
        ActivitySkillSettings activitySkillSettings = new ActivitySkillSettings();
        if (!timeCareActivity.getArrayOfSkill().isEmpty()) {
            List<ActivitySkill> activitySkills = skills.stream().filter(kairosSkill -> timeCareActivity.getArrayOfSkill().stream().map(timeCareSkill -> timeCareSkill).
                    anyMatch(timeCareSkill -> timeCareSkill.equals(kairosSkill.getName()))).map(skill -> new ActivitySkill(skill.getName(), "2", skill.getId())).collect(Collectors.toList());
            activitySkillSettings = Optional.ofNullable(activity.getActivitySkillSettings()).isPresent() ? activity.getActivitySkillSettings() : new ActivitySkillSettings();
            activitySkillSettings.setActivitySkills(activitySkills);
            activity.setActivitySkillSettings(activitySkillSettings);
        }
        return activitySkillSettings;
    }

    private static ActivityTimeCalculationSettings getTimeCalculationActivitySettings(TimeCareActivity timeCareActivity, Activity activity) {
        ActivityTimeCalculationSettings activityTimeCalculationSettings = Optional.ofNullable(activity.getActivityTimeCalculationSettings()).isPresent() ?
                activity.getActivityTimeCalculationSettings() : new ActivityTimeCalculationSettings();
        List<String> balanceTypes = new ArrayList<>();
        balanceTypes.add(timeCareActivity.getBalanceType().replace(" ", "_"));
        activityTimeCalculationSettings.setMethodForCalculatingTime(durationCalculationMethod(timeCareActivity.getTimeMethod()));
        activityTimeCalculationSettings.setBalanceType(balanceTypes);
        if (activityTimeCalculationSettings.getMethodForCalculatingTime().equals(FIXED_TIME)) {
            activityTimeCalculationSettings.setFixedTimeValue(0l);
        }
        activityTimeCalculationSettings.setDefaultStartTime(LocalTime.of(7, 0));
        activityTimeCalculationSettings.setMultiplyWithValue(1d);
        activityTimeCalculationSettings.setMultiplyWith(true);
        if (!StringUtils.isBlank(timeCareActivity.getMultiplyTimeWith())) {
            activityTimeCalculationSettings.setMultiplyWithValue(Double.parseDouble(timeCareActivity.getMultiplyTimeWith()));
            activityTimeCalculationSettings.setMultiplyWith(true);
        }
        return activityTimeCalculationSettings;
    }

    private static ActivityRulesSettings getRulesActivitySettings(TimeCareActivity timeCareActivity, List<PhaseDTO> phases, Activity activity) {
        ActivityRulesSettings activityRulesSettings = Optional.ofNullable(activity.getActivityRulesSettings()).isPresent() ? activity.getActivityRulesSettings() :
                new ActivityRulesSettings();

        activityRulesSettings.setEligibleForStaffingLevel(timeCareActivity.getIsStaffing());
        return activityRulesSettings;
    }

    private static ActivityBalanceSettings getBalanceSettingsActivitySettings(TimeCareActivity timeCareActivity, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId, Activity activity) {
        ActivityBalanceSettings activityBalanceSettings = Optional.ofNullable(activity.getActivityBalanceSettings()).isPresent() ? activity.getActivityBalanceSettings() :
                new ActivityBalanceSettings();
        activityBalanceSettings.setTimeTypeId(timeCareActivity.getIsWork() && timeCareActivity.getIsPresence() ? presenceTimeTypeId : absenceTimeTypeId);
        activityBalanceSettings.setNegativeDayBalancePresent(timeCareActivity.getNegativeDayBalance());
        return activityBalanceSettings;
    }

    public  static void initializeActivitySettings(Activity activity, List<PhaseTemplateValue> phaseTemplateValues, GlideTimeSettingsDTO glideTimeSettingsDTO){

        ActivityRulesSettings activityRulesSettings = new ActivityRulesSettings();
        activityRulesSettings.setPqlSettings(new PQLSettings());
        activityRulesSettings.setCutOffBalances(CutOffIntervalUnit.CutOffBalances.EXPIRE);
        activity.setActivityRulesSettings(activityRulesSettings);

        ActivityTimeCalculationSettings activityTimeCalculationSettings = new ActivityTimeCalculationSettings(ENTERED_TIMES, 0L, true, LocalTime.of(7, 0), 1d);
        activity.setActivityTimeCalculationSettings(activityTimeCalculationSettings);

        ActivityIndividualPointsSettings activityIndividualPointsSettings = new ActivityIndividualPointsSettings("addHourValues", 0.0);
        activity.setActivityIndividualPointsSettings(activityIndividualPointsSettings);

        ActivityCommunicationSettings activityCommunicationSettings = new ActivityCommunicationSettings(false, false, false);
        activity.setActivityCommunicationSettings(activityCommunicationSettings);

        ActivityOptaPlannerSetting activityOptaPlannerSetting = new ActivityOptaPlannerSetting(AppConstants.MAX_ONE_ACTIVITY_PER_SHIFT, 0, true);
        activity.setActivityOptaPlannerSetting(activityOptaPlannerSetting);

        ActivityCTAAndWTASettings activityCTAAndWTASettings = new ActivityCTAAndWTASettings(false);
        activity.setActivityCTAAndWTASettings(activityCTAAndWTASettings);

        ActivityPhaseSettings activityPhaseSettings =new ActivityPhaseSettings(phaseTemplateValues);
        activity.setActivityPhaseSettings(activityPhaseSettings);
        activity.setActivityNotesSettings(new ActivityNotesSettings());

        ActivitySkillSettings activitySkillSettings = new ActivitySkillSettings();
        activity.setActivitySkillSettings(activitySkillSettings);
        activity.setActivityLocationSettings(ActivityUtil.initializeLocationActivitySettings(glideTimeSettingsDTO));
        ActivityBalanceSettings activityBalanceSettings = new ActivityBalanceSettings(false, false);

        activity.setActivityBalanceSettings(activityBalanceSettings);
    }

    public static Set<BigInteger> getAllActivities(ShiftWithActivityDTO shift) {
        Set<BigInteger> allActivities=new HashSet<>();
        for (ShiftActivityDTO shiftActivityDTO:shift.getActivities()) {
            allActivities.add(shiftActivityDTO.getActivityId());
            for (ShiftActivityDTO child:shiftActivityDTO.getChildActivities()) {
                allActivities.add(child.getActivityId());
            }
        }
        return allActivities;
    }


}