package com.kairos.service.activity;/*
 *Created By Pavan on 29/10/18
 *
 */

import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.activity.activity_tabs.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.activity.glide_time.GlideTimeSettingsDTO;
import com.kairos.dto.activity.phase.PhaseDTO;
import com.kairos.dto.user.organization.skill.Skill;
import com.kairos.enums.ActivityStateEnum;
import com.kairos.enums.LocationEnum;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.*;
import com.kairos.persistence.model.activity.tabs.rules_activity_tab.RulesActivityTab;
import com.kairos.utils.external_plateform_shift.TimeCareActivity;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstants.*;

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
            }
            cutOffIntervals.add(new CutOffInterval(startDate, nextEndDate));
            startDate = nextEndDate.plusDays(1);
        }
        return cutOffIntervals;
    }

    public static String durationCalculationMethod(String method) {
        String calculationType = null;
        switch (method) {
            case FIXED_TIME_FOR_TIMECARE:
                calculationType = FIXED_TIME;
                break;
            case WEEKLY_WORK_TIME:
                calculationType = FULL_DAY_CALCULATION;
                break;
            case FULL_TIME_HOUR:
                calculationType = WEEKLY_HOURS;
                break;
            case CALCULATED_TIME:
                calculationType = ENTERED_TIMES;
                break;
       /*     case "":
                break;*/

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
        Activity activity = result.orElseGet(Activity::new);
        activity.setCountryId(countryId);
        activity.setParentActivity(true);
        activity.setState(ActivityStateEnum.LIVE);
        activity.setName(timeCareActivity.getName());
        activity.setOrganizationTypes(Collections.singletonList(orgType));
        activity.setOrganizationSubTypes(orgSubTypes);
        activity.setExternalId(timeCareActivity.getId());
        //general tab
        GeneralActivityTab generalActivityTab = (Optional.ofNullable(activity.getGeneralActivityTab()).isPresent()) ? activity.getGeneralActivityTab() :
                new GeneralActivityTab();
        generalActivityTab.setName(activity.getName());
        generalActivityTab.setShortName(timeCareActivity.getShortName());
        generalActivityTab.setCategoryId(activityCategory.getId());
        activity.setGeneralActivityTab(generalActivityTab);

        //balance setting tab
        BalanceSettingsActivityTab balanceSettingsActivityTab = Optional.ofNullable(activity.getBalanceSettingsActivityTab()).isPresent() ? activity.getBalanceSettingsActivityTab() :
                new BalanceSettingsActivityTab();
        balanceSettingsActivityTab.setTimeTypeId(timeCareActivity.getIsWork() && timeCareActivity.getIsPresence() ? presenceTimeTypeId : absenceTimeTypeId);
        balanceSettingsActivityTab.setNegativeDayBalancePresent(timeCareActivity.getNegativeDayBalance());
        activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);

        //rules activity tab
        RulesActivityTab rulesActivityTab = Optional.ofNullable(activity.getRulesActivityTab()).isPresent() ? activity.getRulesActivityTab() :
                new RulesActivityTab();

        rulesActivityTab.setEligibleForStaffingLevel(timeCareActivity.getIsStaffing());
        List<PhaseTemplateValue> phaseTemplateValues = getPhaseForRulesActivity(phases);
        activity.setRulesActivityTab(rulesActivityTab);

        // location settings
        activity.setLocationActivityTab(ActivityUtil.initializeLocationActivityTab(glideTimeSettingsDTO));

        //Time calculation tab
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
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

        if (!timeCareActivity.getArrayOfSkill().isEmpty()) {
            List<ActivitySkill> activitySkills = skills.stream().filter(kairosSkill -> timeCareActivity.getArrayOfSkill().stream().map(timeCareSkill -> timeCareSkill).
                    anyMatch(timeCareSkill -> timeCareSkill.equals(kairosSkill.getName()))).map(skill -> new ActivitySkill(skill.getName(), "2", skill.getId())).collect(Collectors.toList());
            SkillActivityTab skillActivityTab = Optional.ofNullable(activity.getSkillActivityTab()).isPresent() ? activity.getSkillActivityTab() : new SkillActivityTab();
            skillActivityTab.setActivitySkills(activitySkills);
            activity.setSkillActivityTab(skillActivityTab);
        } else {
            SkillActivityTab skillActivityTab = new SkillActivityTab();
            activity.setSkillActivityTab(skillActivityTab);
        }
        return activity;
    }
    public  static void initializeActivityTabs(Activity activity,List<PhaseTemplateValue> phaseTemplateValues,GlideTimeSettingsDTO glideTimeSettingsDTO){

        RulesActivityTab rulesActivityTab = new RulesActivityTab();
        PQLSettings pqlSettings=new PQLSettings();
        rulesActivityTab.setPqlSettings(pqlSettings);
        activity.setRulesActivityTab(rulesActivityTab);

        TimeCalculationActivityTab timeCalculationActivityTab = new TimeCalculationActivityTab(ENTERED_TIMES, 0L, true, LocalTime.of(7, 0), 1d);
        activity.setTimeCalculationActivityTab(timeCalculationActivityTab);

        IndividualPointsActivityTab individualPointsActivityTab = new IndividualPointsActivityTab("addHourValues", 0.0);
        activity.setIndividualPointsActivityTab(individualPointsActivityTab);

        CommunicationActivityTab communicationActivityTab = new CommunicationActivityTab(false, false);
        activity.setCommunicationActivityTab(communicationActivityTab);

        OptaPlannerSettingActivityTab optaPlannerSettingActivityTab = new OptaPlannerSettingActivityTab(AppConstants.MAX_ONE_ACTIVITY_PER_SHIFT, 0, true);
        activity.setOptaPlannerSettingActivityTab(optaPlannerSettingActivityTab);

        CTAAndWTASettingsActivityTab ctaAndWtaSettingsActivityTab = new CTAAndWTASettingsActivityTab(false);
        activity.setCtaAndWtaSettingsActivityTab(ctaAndWtaSettingsActivityTab);

        PhaseSettingsActivityTab phaseSettingsActivityTab=new PhaseSettingsActivityTab(phaseTemplateValues);
        activity.setPhaseSettingsActivityTab(phaseSettingsActivityTab);

        activity.setPermissionsActivityTab(new PermissionsActivityTab());

        activity.setNotesActivityTab(new NotesActivityTab());

        SkillActivityTab skillActivityTab = new SkillActivityTab();
        activity.setSkillActivityTab(skillActivityTab);
        activity.setLocationActivityTab(ActivityUtil.initializeLocationActivityTab(glideTimeSettingsDTO));
        BalanceSettingsActivityTab balanceSettingsActivityTab = new BalanceSettingsActivityTab(false, false);

        activity.setBalanceSettingsActivityTab(balanceSettingsActivityTab);



    }

    public static List<String> verifyCompositeActivities(boolean breakAllowed, List<Activity> activities){
        List<String> invalidActivities=new ArrayList<>();
        activities.forEach(activity -> {
            if(activity.getRulesActivityTab().isBreakAllowed()!=breakAllowed
                    && TimeTypeEnum.PAID_BREAK.equals(activity.getBalanceSettingsActivityTab().getTimeType())
                    && TimeTypeEnum.UNPAID_BREAK.equals(activity.getBalanceSettingsActivityTab().getTimeType())){
                invalidActivities.add(activity.getName());
            }
        });
        return invalidActivities;
    }
}