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
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
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

    private static LocationActivityTab initializeLocationActivityTab(GlideTimeSettingsDTO glideTimeSettingsDTO){
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
        return new LocationActivityTab(activityGlideTimeDetailsForCheckIn, activityGlideTimeDetailsForCheckOut);
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
        //general tab
        GeneralActivityTab generalActivityTab = getGeneralActivityTab(timeCareActivity, activityCategory, activity);
        activity.setGeneralActivityTab(generalActivityTab);

        //balance setting tab
        BalanceSettingsActivityTab balanceSettingsActivityTab = getBalanceSettingsActivityTab(timeCareActivity, presenceTimeTypeId, absenceTimeTypeId, activity);
        activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);
        //rules activity tab
        RulesActivityTab rulesActivityTab = getRulesActivityTab(timeCareActivity, phases, activity);
        activity.setRulesActivityTab(rulesActivityTab);
        // location settings
        activity.setLocationActivityTab(ActivityUtil.initializeLocationActivityTab(glideTimeSettingsDTO));
        //Time calculation tab
        TimeCalculationActivityTab timeCalculationActivityTab = getTimeCalculationActivityTab(timeCareActivity, activity);
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);
        SkillActivityTab skillActivityTab = getSkillActivityTab(timeCareActivity, skills, activity);
        activity.setSkillActivityTab(skillActivityTab);
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

    private static GeneralActivityTab getGeneralActivityTab(TimeCareActivity timeCareActivity, ActivityCategory activityCategory, Activity activity) {
        GeneralActivityTab generalActivityTab = (Optional.ofNullable(activity.getGeneralActivityTab()).isPresent()) ? activity.getGeneralActivityTab() :
                new GeneralActivityTab();
        generalActivityTab.setName(activity.getName());
        generalActivityTab.setShortName(timeCareActivity.getShortName());
        generalActivityTab.setCategoryId(activityCategory.getId());
        return generalActivityTab;
    }

    private static SkillActivityTab getSkillActivityTab(TimeCareActivity timeCareActivity, List<Skill> skills, Activity activity) {
        SkillActivityTab skillActivityTab = new SkillActivityTab();
        if (!timeCareActivity.getArrayOfSkill().isEmpty()) {
            List<ActivitySkill> activitySkills = skills.stream().filter(kairosSkill -> timeCareActivity.getArrayOfSkill().stream().map(timeCareSkill -> timeCareSkill).
                    anyMatch(timeCareSkill -> timeCareSkill.equals(kairosSkill.getName()))).map(skill -> new ActivitySkill(skill.getName(), "2", skill.getId())).collect(Collectors.toList());
            skillActivityTab = Optional.ofNullable(activity.getSkillActivityTab()).isPresent() ? activity.getSkillActivityTab() : new SkillActivityTab();
            skillActivityTab.setActivitySkills(activitySkills);
            activity.setSkillActivityTab(skillActivityTab);
        }
        return skillActivityTab;
    }

    private static TimeCalculationActivityTab getTimeCalculationActivityTab(TimeCareActivity timeCareActivity, Activity activity) {
        TimeCalculationActivityTab timeCalculationActivityTab = Optional.ofNullable(activity.getTimeCalculationActivityTab()).isPresent() ?
                activity.getTimeCalculationActivityTab() : new TimeCalculationActivityTab();
        List<String> balanceTypes = new ArrayList<>();
        balanceTypes.add(timeCareActivity.getBalanceType().replace(" ", "_"));
        timeCalculationActivityTab.setMethodForCalculatingTime(durationCalculationMethod(timeCareActivity.getTimeMethod()));
        timeCalculationActivityTab.setBalanceType(balanceTypes);
        if (timeCalculationActivityTab.getMethodForCalculatingTime().equals(FIXED_TIME)) {
            timeCalculationActivityTab.setFixedTimeValue(0l);
        }
        timeCalculationActivityTab.setDefaultStartTime(LocalTime.of(7, 0));
        timeCalculationActivityTab.setMultiplyWithValue(1d);
        timeCalculationActivityTab.setMultiplyWith(true);
        if (!StringUtils.isBlank(timeCareActivity.getMultiplyTimeWith())) {
            timeCalculationActivityTab.setMultiplyWithValue(Double.parseDouble(timeCareActivity.getMultiplyTimeWith()));
            timeCalculationActivityTab.setMultiplyWith(true);
        }
        return timeCalculationActivityTab;
    }

    private static RulesActivityTab getRulesActivityTab(TimeCareActivity timeCareActivity, List<PhaseDTO> phases, Activity activity) {
        RulesActivityTab rulesActivityTab = Optional.ofNullable(activity.getRulesActivityTab()).isPresent() ? activity.getRulesActivityTab() :
                new RulesActivityTab();

        rulesActivityTab.setEligibleForStaffingLevel(timeCareActivity.getIsStaffing());
        return rulesActivityTab;
    }

    private static BalanceSettingsActivityTab getBalanceSettingsActivityTab(TimeCareActivity timeCareActivity, BigInteger presenceTimeTypeId, BigInteger absenceTimeTypeId, Activity activity) {
        BalanceSettingsActivityTab balanceSettingsActivityTab = Optional.ofNullable(activity.getBalanceSettingsActivityTab()).isPresent() ? activity.getBalanceSettingsActivityTab() :
                new BalanceSettingsActivityTab();
        balanceSettingsActivityTab.setTimeTypeId(timeCareActivity.getIsWork() && timeCareActivity.getIsPresence() ? presenceTimeTypeId : absenceTimeTypeId);
        balanceSettingsActivityTab.setNegativeDayBalancePresent(timeCareActivity.getNegativeDayBalance());
        return balanceSettingsActivityTab;
    }

    public  static void initializeActivityTabs(Activity activity,List<PhaseTemplateValue> phaseTemplateValues,GlideTimeSettingsDTO glideTimeSettingsDTO){

        RulesActivityTab rulesActivityTab = new RulesActivityTab();
        PQLSettings pqlSettings=new PQLSettings();
        rulesActivityTab.setPqlSettings(pqlSettings);
        rulesActivityTab.setCutOffBalances(CutOffIntervalUnit.CutOffBalances.EXPIRE);
        activity.setRulesActivityTab(rulesActivityTab);

        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab(ENTERED_TIMES, 0L, true, LocalTime.of(7, 0), 1d);
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

        IndividualPointsActivityTab individualPointsActivityTab = new IndividualPointsActivityTab("addHourValues", 0.0);
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);

        CommunicationActivityTab communicationActivityTab = new CommunicationActivityTab(false, false, false);
        activity.setCommunicationActivityTab(communicationActivityTab);

        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = new OptaPlannerSettingActivityTab(AppConstants.MAX_ONE_ACTIVITY_PER_SHIFT, 0, true);
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);

        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(false);
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);

        PhaseSettingsActivityTab phaseSettingsActivityTab=new PhaseSettingsActivityTab(phaseTemplateValues);
        activity.setPhaseSettingsActivityTab(phaseSettingsActivityTab);
        activity.setNotesActivityTab(new NotesActivityTab());

        SkillActivityTab skillActivityTab = new SkillActivityTab();
        activity.setSkillActivityTab(skillActivityTab);
        activity.setLocationActivityTab(ActivityUtil.initializeLocationActivityTab(glideTimeSettingsDTO));
        BalanceSettingsActivityTab balanceSettingsActivityTab = new BalanceSettingsActivityTab(false, false);

        activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);
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