package com.kairos.shiftplanningNewVersion.generator;

import com.kairos.commons.custom_exception.ActionNotPermittedException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.time_type.TimeTypeDTO;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.time_slot.TimeSlot;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.shiftplanning.constraints.ConstraintHandler;
import com.kairos.shiftplanning.constraints.activityconstraint.*;
import com.kairos.shiftplanning.constraints.unitconstraint.DislikeNightShiftsForNonNightWorkers;
import com.kairos.shiftplanning.constraints.unitconstraint.MaxLengthOfShiftInNightTimeSlot;
import com.kairos.shiftplanning.constraints.unitconstraint.PreferedEmployementType;
import com.kairos.shiftplanning.constraints.unitconstraint.ShiftOnWeekend;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.staff.*;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.unit.*;
import com.kairos.shiftplanning.domain.wta_ruletemplates.WTABaseRuleTemplate;
import com.kairos.shiftplanningNewVersion.entity.ALI;
import com.kairos.shiftplanningNewVersion.entity.Shift;
import com.kairos.shiftplanningNewVersion.entity.Staff;
import com.kairos.shiftplanningNewVersion.solution.StaffingLevelSolution;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.constraint.ScoreLevel.HARD;
import static com.kairos.enums.constraint.ScoreLevel.SOFT;

public class StaffingLevelGenerator {

    private BigInteger id = BigInteger.valueOf(1);
    private Set<Integer> breakTime = newHashSet(15,30,45);

    public StaffingLevelSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {

        StaffingLevelSolution staffingLevelSolution = new StaffingLevelSolution();
        updateUnit(shiftPlanningProblemSubmitDTO.getUnitId(), shiftPlanningProblemSubmitDTO, staffingLevelSolution);
        updateStaffs(shiftPlanningProblemSubmitDTO, staffingLevelSolution);
        Map<BigInteger, Activity> activityMap = updateActivityRelatedDetails(shiftPlanningProblemSubmitDTO, staffingLevelSolution);
        List<Shift> shiftImp = getShiftRequestPhase(shiftPlanningProblemSubmitDTO, staffingLevelSolution,activityMap);
        staffingLevelSolution.setShifts(shiftImp);
        //staffingLevelSolution.setScore(HardMediumSoftLongScore.of(0,0,0));
        return staffingLevelSolution;
    }

    private void updateUnit(Long unitId, ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, StaffingLevelSolution staffingLevelSolution) {
        Phase phase = new Phase(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getCurrentPhaseId(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getPhaseEnum(), PhaseType.PLANNING);
        PlanningPeriod planningPeriod = new PlanningPeriod(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getId(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getStartDate(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getEndDate());
        planningPeriod.setPhase(phase);
        Map<String, TimeSlot> timeSlotMap = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO, Unit.class).getTimeSlotMap();
        Map<Long, DayType> dayTypeMap = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO, Unit.class).getDayTypeMap();
        PresencePlannedTime presencePlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getPresencePlannedTime(), PresencePlannedTime.class);
        AbsencePlannedTime absencePlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getAbsencePlannedTime(), AbsencePlannedTime.class);
        NonWorkingPlannedTime nonWorkingPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getNonWorkingPlannedTime(), NonWorkingPlannedTime.class);
        Unit unit = Unit.builder().planningPeriod(planningPeriod).id(unitId).dayTypeMap(dayTypeMap).timeSlotMap(timeSlotMap).accessGroupRole(AccessGroupRole.MANAGEMENT).absencePlannedTime(absencePlannedTime).nonWorkingPlannedTime(nonWorkingPlannedTime).presencePlannedTime(presencePlannedTime).build();
        staffingLevelSolution.setUnit(unit);
    }


    public void updateStaffs(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, StaffingLevelSolution staffingLevelSolution) {
        List<Staff> staffList = new ArrayList<>();
        Map[] agreementMap = getCostTimeAgreementAndWTAMap(shiftPlanningProblemSubmitDTO);
        Map<Long, Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>>> employmentIdAndDateWiseWTARuleTemplateMap = agreementMap[0];
        Map<Long, Map<LocalDate, List<CTARuleTemplate>>> employmentIdAndDateWiseCtaRuleTemplateMap = agreementMap[1];
        for (StaffDTO staffDTO : shiftPlanningProblemSubmitDTO.getStaffs()) {
            for (EmploymentDTO employmentDTO : staffDTO.getEmployments()) {
                if(employmentIdAndDateWiseCtaRuleTemplateMap.containsKey(employmentDTO.getId()) && employmentIdAndDateWiseWTARuleTemplateMap.containsKey(employmentDTO.getId())){
                    EmploymentType employmentType = getEmploymentType(employmentDTO);
                    Map<LocalDate, Function> dateWiseFunctionMap = ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getDateWiseFunctionMap(),Map.class);
                    Expertise expertise = ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getExpertise(),Expertise.class);
                    Employment employment = getEmployment(employmentDTO, employmentType, dateWiseFunctionMap, expertise);
                    Staff employee = Staff.builder()
                            .id(staffDTO.getId())
                            .name(staffDTO.getFirstName())
                            .skillSet((Set<Skill>) ObjectMapperUtils.copyCollectionPropertiesByMapper(new HashSet(isNullOrElse(staffDTO.getSkills(),new ArrayList<>())), Skill.class))
                            .employment(employment)
                            .nightWorker(staffDTO.isNightWorker())
                            .localDateCTARuletemplateMap(employmentIdAndDateWiseCtaRuleTemplateMap.get(employment.getId()))
                            .functionalBonus(new HashMap<>())
                            .staffChildDetails(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffDTO.getStaffChildDetails(),StaffChildDetail.class))
                            .seniorAndChildCareDays(ObjectMapperUtils.copyPropertiesByMapper(staffDTO.getSeniorAndChildCareDays(),SeniorAndChildCareDays.class))
                            .tags(new HashSet(ObjectMapperUtils.copyCollectionPropertiesByMapper(isNullOrElse(staffDTO.getTags(),new ArrayList<>()), Tag.class)))
                            .teams(new HashSet(ObjectMapperUtils.copyCollectionPropertiesByMapper(isNullOrElse(staffDTO.getTeams(),new ArrayList<>()),Team.class)))
                            .unit(staffingLevelSolution.getUnit())
                            .wtaRuleTemplateMap(employmentIdAndDateWiseWTARuleTemplateMap.get(employment.getId()))
                            .expertiseNightWorkerSetting(ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getExpertiseNightWorkerSetting(),ExpertiseNightWorkerSetting.class))
                            .breakSettings(getBreakSettings(employmentDTO))
                            .build();
                    staffList.add(employee);
                }
            }
        }
        staffingLevelSolution.setStaffs(staffList);
    }

    private BreakSettings getBreakSettings(EmploymentDTO employmentDTO) {
        return ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getBreakSettings(), BreakSettings.class);
    }

    private EmploymentType getEmploymentType(EmploymentDTO employmentDTO) {
        return EmploymentType.builder().id(employmentDTO.getEmploymentTypeId()).employmentCategories(newHashSet(employmentDTO.getEmploymentTypeCategory())).build();
    }

    private Employment getEmployment(EmploymentDTO employmentDTO, EmploymentType employmentType, Map<LocalDate, Function> dateWiseFunctionMap, Expertise expertise) {
        return Employment.builder()
                .id(employmentDTO.getId())
                .employmentType(employmentType)
                .dateWiseFunctionMap(dateWiseFunctionMap)
                .expertise(expertise).startDate(employmentDTO.getStartDate())
                .endDate(employmentDTO.getEndDate())
                .employmentSubType(employmentDTO.getEmploymentSubType())
                .employmentLines(ObjectMapperUtils.copyCollectionPropertiesByMapper(employmentDTO.getEmploymentLines(),EmploymentLine.class))
                .build();
    }

    private Map[] getCostTimeAgreementAndWTAMap(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Map<Long, Map<LocalDate, List<CTARuleTemplate>>> employmentIdAndDateWiseCtaRuleTemplateMap = new HashMap<>();
        Map<Long, Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>>> employmentIdAndDateWiseWTARuleTemplateMap = new HashMap<>();
        for (Map.Entry<Long, List<CTAResponseDTO>> employmentIdAndCtaResponse : shiftPlanningProblemSubmitDTO.getEmploymentIdAndCTAResponseMap().entrySet()) {
            ZonedDateTime startDate = asZonedDateTime(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getStartDate(), LocalTime.MIDNIGHT);
            ZonedDateTime endDate = asZonedDateTime(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getEndDate(), LocalTime.MIDNIGHT);
            List<WTAResponseDTO> wtaResponseDTOS = shiftPlanningProblemSubmitDTO.getEmploymentIdAndWTAResponseMap().get(employmentIdAndCtaResponse.getKey());
            Map<LocalDate, List<CTARuleTemplate>> ctaRuleTemplatesMap = new HashMap<>();
            Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>> localDateWTARuletemplateMap = new HashMap<>();
            while (!startDate.isAfter(endDate)){
                AtomicReference<ZonedDateTime> zonedDateTimeAtomicReference = new AtomicReference<>(startDate);
                WTAResponseDTO wtaResponseDTO = wtaResponseDTOS.stream().filter(wtaResponse -> wtaResponse.isValidWorkTimeAgreement(zonedDateTimeAtomicReference.get().toLocalDate())).findFirst().orElse(null);//.orElseThrow(()->new DataNotFoundByIdException("WTA Not Found for employmentId "+employmentIdAndCtaResponse.getKey()+" Date "+zonedDateTimeAtomicReference.get()));
                if(isNotNull(wtaResponseDTO)){
                    localDateWTARuletemplateMap.put(startDate.toLocalDate(),getWTARuletemplateMap(wtaResponseDTO));
                }
                CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponse.getValue().stream().filter(ctaResponse -> ctaResponse.isValidCostTimeAgreement(zonedDateTimeAtomicReference.get().toLocalDate())).findFirst().orElse(null);//.orElseThrow(()->new DataNotFoundByIdException("CTA not Found for employmentId "+employmentIdAndCtaResponse.getKey()+" Date "+zonedDateTimeAtomicReference.get()));
                if(isNotNull(ctaResponseDTO)){
                    ctaRuleTemplatesMap.put(startDate.toLocalDate(),ObjectMapperUtils.copyCollectionPropertiesByMapper(ctaResponseDTO.getRuleTemplates(),CTARuleTemplate.class));
                }
                startDate = startDate.plusDays(1);
            }
            employmentIdAndDateWiseWTARuleTemplateMap.put(employmentIdAndCtaResponse.getKey(),localDateWTARuletemplateMap);
            employmentIdAndDateWiseCtaRuleTemplateMap.put(employmentIdAndCtaResponse.getKey(),ctaRuleTemplatesMap);
        }
        return new Map[]{employmentIdAndDateWiseWTARuleTemplateMap,employmentIdAndDateWiseCtaRuleTemplateMap};
    }

    private Map<ConstraintSubType, WTABaseRuleTemplate> getWTARuletemplateMap(WTAResponseDTO wtaResponseDTO) {
        Map<ConstraintSubType, WTABaseRuleTemplate> wtaBaseRuleTemplateMap = new HashMap<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : wtaResponseDTO.getRuleTemplates()) {
            wtaBaseRuleTemplateMap.put(ConstraintSubType.valueOf(ruleTemplate.getWtaTemplateType().toString()), ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate, WTABaseRuleTemplate.class));
        }
        return wtaBaseRuleTemplateMap;
    }


    public Map<BigInteger, Activity> updateActivityRelatedDetails(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, StaffingLevelSolution staffingLevelSolution) {
        Map<LocalDate, List<StaffingLevelInterval>> localDateStaffingLevelTimeSlotMap = new HashMap<LocalDate, List<StaffingLevelInterval>>(){{
            put(asLocalDate(shiftPlanningProblemSubmitDTO.getStaffingLevels().get(0).getCurrentDate()),shiftPlanningProblemSubmitDTO.getStaffingLevels().get(0).getPresenceStaffingLevelInterval());
        }};
        List<ALI> activityLineIntervalList = new ArrayList<>();
        Map<LocalDate, Set<Activity>> dateActivityMap = new HashMap<>();
        Set<LocalDate> localDates = new HashSet<>();
        Map<BigInteger,Activity> activityMap = shiftPlanningProblemSubmitDTO.getActivities().stream().collect(Collectors.toMap(k->k.getId(),v->getActivity(v,shiftPlanningProblemSubmitDTO.getActivityOrderMap(),shiftPlanningProblemSubmitDTO.getTimeTypeMap())));
        Map<LocalDate,List<Activity>> activitiesPerDay = new HashMap<>();
        Map<LocalDate,Set<BigInteger>> activitiesIdsPerDay = new HashMap<>();
        Map<LocalDate,Map<BigInteger,List<ALI>>> aliMapByActivityAndDate = new HashMap<>();
        for (Map.Entry<LocalDate, List<StaffingLevelInterval>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            localDates.add(localDateListEntry.getKey());
            Map<BigInteger,List<ALI>> aliByActivity = aliMapByActivityAndDate.getOrDefault(localDateListEntry.getKey(),new HashMap<>());
            for (StaffingLevelInterval staffingLevelInterval : localDateListEntry.getValue()) {
                for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                    if (activityMap.containsKey(staffingLevelActivity.getActivityId()) && staffingLevelActivity.getMinNoOfStaff()>0) {
                        Activity activity = activityMap.get(staffingLevelActivity.getActivityId());
                        Set<BigInteger> activitiesIdsPerDayOrDefault = activitiesIdsPerDay.getOrDefault(localDateListEntry.getKey(), new HashSet<>());
                        if(!activitiesIdsPerDayOrDefault.contains(activity.getId())){
                            List<Activity> activities = activitiesPerDay.getOrDefault(localDateListEntry.getKey(),new ArrayList<>());
                            activities.add(activity);
                            activitiesPerDay.put(localDateListEntry.getKey(),activities);
                            activitiesIdsPerDayOrDefault.add(activity.getId());
                            activitiesIdsPerDay.put(localDateListEntry.getKey(),activitiesIdsPerDayOrDefault);
                        }
                        Set<Activity> activityList = dateActivityMap.getOrDefault(localDateListEntry.getKey(), new HashSet<>());
                        activityList.add(activity);
                        dateActivityMap.put(localDateListEntry.getKey(), activityList);
                        ZonedDateTime zonedDateTime = asZonedDateTime(localDateListEntry.getKey(), staffingLevelInterval.getStaffingLevelDuration().getFrom());
                        //Prepare DateWise Required/Demanding activities for optaplanner
                        List<ALI> activityLineIntervals = getInterval(activity, zonedDateTime, staffingLevelInterval.getStaffingLevelDuration(), staffingLevelActivity);
                        List<ALI> aliTreeSet = aliByActivity.getOrDefault(staffingLevelActivity.getActivityId(),new ArrayList<>());
                        aliTreeSet.addAll(activityLineIntervals);
                        aliByActivity.put(staffingLevelActivity.getActivityId(),aliTreeSet);
                        activityLineIntervalList.addAll(activityLineIntervals);
                    }
                }
            }
            aliMapByActivityAndDate.put(localDateListEntry.getKey(),aliByActivity);
        }
        Object[] objects = getMergedALI(aliMapByActivityAndDate);
        List<ALI> alis = (List<ALI>)objects[0];
        List<List<ALI>> aliPerDayByActivity = (List<List<ALI>>)objects[1];
        staffingLevelSolution.setActivityLineIntervals(alis);
        List<Activity> activityList = dateActivityMap.values().stream().flatMap(activities -> activities.stream()).distinct().sorted(Comparator.comparing(Activity::getRanking)).collect(Collectors.toList());
        staffingLevelSolution.setActivities(activityList);
        staffingLevelSolution.setWeekDates(new ArrayList<>(localDates));
        staffingLevelSolution.setActivitiesPerDay(activitiesPerDay);
        /*Map<BigInteger,List<ALI>> bigIntegerListMap = activityLineIntervalList.stream().collect(Collectors.groupingBy(ali -> ali.getActivity().getId()));
        bigIntegerListMap.values().forEach(listEntry -> staffingLevelSolution.getAliPerActivities().add(listEntry));*/
        staffingLevelSolution.setAliPerActivities(aliPerDayByActivity);
        return activityMap;
    }

    public Object[] getMergedALI(Map<LocalDate, Map<BigInteger, List<ALI>>> aliMapByActivityAndDate) {
        List<ALI> alis = new ArrayList<>();
        List<List<ALI>> aliPerDayByActivity = new ArrayList<>();
        for (Map.Entry<LocalDate, Map<BigInteger, List<ALI>>> localDateMapEntry : aliMapByActivityAndDate.entrySet()) {
            for (Map.Entry<BigInteger, List<ALI>> activityAndALIs : localDateMapEntry.getValue().entrySet()) {
                List<ALI> alIsValue = activityAndALIs.getValue();
                Map<String,List<ALI>> integerListMap = alIsValue.stream().collect(Collectors.groupingBy(ali -> ali.getStaffNo()+"_"+ali.isRequired()));
                for (Map.Entry<String, List<ALI>> integerListEntry : integerListMap.entrySet()) {
                    alIsValue = integerListEntry.getValue();
                    Collections.sort(alIsValue);
                    ALI needToAdd = alIsValue.get(0);
                    for (int i = 1; i < alIsValue.size(); i++) {
                        ALI previousAli = alIsValue.get(i-1);
                        ALI nextAli = alIsValue.get(i);
                        if(previousAli.getEnd().equals(nextAli.getStart())){
                            needToAdd.setDuration(needToAdd.getDuration()+nextAli.getDuration());
                        }else {
                            List<ALI> aliList = breakALI(needToAdd);
                            alis.addAll(aliList);
                            needToAdd = nextAli;
                        }
                    }
                    if(alis.isEmpty() || !alis.get(alis.size()-1).getStart().equals(needToAdd.getStart()) && !alis.get(alis.size()-1).getEnd().equals(needToAdd.getEnd())){
                        List<ALI> aliList = breakALI(needToAdd);
                        alis.addAll(aliList);
                    }

                }
            }
            Map<BigInteger,List<ALI>> bigIntegerListMap = alis.stream().collect(Collectors.groupingBy(ali -> ali.getActivity().getId()));
            bigIntegerListMap.values().forEach(listEntry -> aliPerDayByActivity.add(listEntry));
        }
        return new Object[]{alis,aliPerDayByActivity};
    }

    private List<ALI> breakALI(ALI needToAdd) {
        List<ALI> alis = new ArrayList<>();
        int minuteOfHours = needToAdd.getStart().get(ChronoField.MINUTE_OF_HOUR);
        if(breakTime.contains(minuteOfHours) && needToAdd.getDuration()>=60){
            ALI ali = ObjectMapperUtils.copyPropertiesByMapper(needToAdd, ALI.class);
            ali.setDuration(60 - minuteOfHours);
            ali.setId(idGenerator());
            alis.add(ali);
            needToAdd.setDuration(needToAdd.getDuration()-ali.getDuration());
            needToAdd.setStart(needToAdd.getStart().plusMinutes(ali.getDuration()));
        }
        if(needToAdd.getDuration()<=60){
            switch (needToAdd.getDuration()){
                case 15:
                case 30:
                case 60:
                    alis.add(needToAdd);
                    break;
                case 45:
                    ALI ali = ObjectMapperUtils.copyPropertiesByMapper(needToAdd, ALI.class);
                    ali.setId(idGenerator());
                    ali.setDuration(30);
                    ali = ObjectMapperUtils.copyPropertiesByMapper(needToAdd, ALI.class);
                    ali.setId(idGenerator());
                    ali.setStart(ali.getStart().plusMinutes(30));
                    ali.setDuration(15);
                    alis.add(ali);
                break;
                default:
                    break;
            }
        }else {
            ALI ali = ObjectMapperUtils.copyPropertiesByMapper(needToAdd, ALI.class);
            ali.setDuration(60);
            ali.setId(idGenerator());
            alis.add(ali);
            needToAdd.setDuration(needToAdd.getDuration()-60);
            needToAdd.setStart(needToAdd.getStart().plusMinutes(60));
            alis.addAll(breakALI(needToAdd));
        }
        return alis;
    }

    private Activity getActivity(ActivityDTO activityDTO, Map<BigInteger,Integer> activityOrderMap, Map<BigInteger, TimeTypeDTO> timeTypeMap) {
        TimeType timeType = getTimeType(activityDTO, timeTypeMap);
        return Activity.builder()
                .breakAllowed(activityDTO.getActivityRulesSettings().isBreakAllowed())
                .cutOffdayValue(activityDTO.getActivityRulesSettings().getCutOffdayValue())
                .cutOffIntervalUnit(activityDTO.getActivityRulesSettings().getCutOffIntervalUnit())
                .cutOffStartFrom(activityDTO.getActivityRulesSettings().getCutOffStartFrom())
                .expertises(activityDTO.getExpertises())
                .fixedTimeValue(activityDTO.getActivityTimeCalculationSettings().getFixedTimeValue())
                .id(activityDTO.getId())
                .fullDayCalculationType(activityDTO.getActivityTimeCalculationSettings().getFullDayCalculationType())
                .fullWeekCalculationType(activityDTO.getActivityTimeCalculationSettings().getFullWeekCalculationType())
                .methodForCalculatingTime(activityDTO.getActivityTimeCalculationSettings().getMethodForCalculatingTime())
                .multiplyWithValue(activityDTO.getActivityTimeCalculationSettings().getMultiplyWithValue())
                .name(activityDTO.getName())
                .validDayTypeIds(isNull(activityDTO.getActivityRulesSettings().getDayTypes()) ? new HashSet<>() : new HashSet<>(activityDTO.getActivityRulesSettings().getDayTypes()))
                .skills(ObjectMapperUtils.copyCollectionPropertiesByMapper(activityDTO.getActivitySkillSettings().getActivitySkills(), Skill.class))
                //    .tags(ObjectMapperUtils.copyCollectionPropertiesByMapper(activityDTO.getTags(), Tag.class))
                .timeType(timeType).teamId(activityDTO.getTeamId()).constraints(getActivityConstrainsts(activityDTO)).order(activityOrderMap.get(activityDTO.getId())).ranking(activityDTO.getActivitySequence()).build();
    }

    private TimeType getTimeType(ActivityDTO activityDTO, Map<BigInteger, TimeTypeDTO> timeTypeMap) {
        TimeTypeDTO timeTypeDTO = timeTypeMap.get(activityDTO.getActivityBalanceSettings().getTimeTypeId());
        return TimeType.builder()
                .timeTypeEnum(activityDTO.getActivityBalanceSettings().getTimeType())
                .timeTypes(activityDTO.getActivityBalanceSettings().getTimeTypes())
                .breakNotHeldValid(timeTypeDTO.isBreakNotHeldValid())
                .id(timeTypeDTO.getId())
                .name(timeTypeDTO.getLabel())
                .build();
    }

    private Map<ConstraintSubType, ConstraintHandler> getActivityConstrainsts(ActivityDTO activityDTO) {
        validateActivityTimeRules(activityDTO);
        Map<ConstraintSubType, ConstraintHandler> constraintMap = new HashMap<>();
        LongestDuration longestDuration = new LongestDuration(activityDTO.getActivityRulesSettings().getLongestTime(), SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(activityDTO.getActivityRulesSettings().getShortestTime(), HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(activityDTO.getActivityRulesSettings().getRecurrenceTimes(), SOFT,-1);//3
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3, SOFT,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(activityDTO.getActivityRulesSettings().getShortestTime(), SOFT,-1);//5
        ActivityDayType activityDayType = new ActivityDayType(SOFT,5);
        ActivityRequiredTag activityRequiredTag = new ActivityRequiredTag(HARD,1);
        constraintMap.put(ConstraintSubType.ACTIVITY_LONGEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,longestDuration);
        constraintMap.put(ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH,shortestDuration);
        constraintMap.put(ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF,maxAllocationPerShift);
        constraintMap.put(ConstraintSubType.ACTIVITY_MUST_CONTINUOUS_NUMBER_OF_HOURS,maxDiffrentActivity);
        constraintMap.put(ConstraintSubType.MINIMUM_LENGTH_OF_ACTIVITY,minimumLengthofActivity);
        constraintMap.put(ConstraintSubType.ACTIVITY_VALID_DAYTYPE,activityDayType);
        constraintMap.put(ConstraintSubType.ACTIVITY_REQUIRED_TAG,activityRequiredTag);
        constraintMap.put(ConstraintSubType.PRESENCE_AND_ABSENCE_SAME_TIME,new PresenceAndAbsenceAtSameTime(SOFT,-6));
        constraintMap.put(ConstraintSubType.MAX_SHIFT_OF_STAFF,new MaxShiftOfStaff(1, SOFT,-6));
        constraintMap.put(ConstraintSubType.PREFER_PERMANENT_EMPLOYEE,new PreferedEmployementType(newHashSet(123l), SOFT,-4));
        constraintMap.put(ConstraintSubType.MINIMIZE_SHIFT_ON_WEEKENDS,new ShiftOnWeekend(SOFT,-4,newHashSet(DayOfWeek.SATURDAY,DayOfWeek.SUNDAY)));
        constraintMap.put(ConstraintSubType.MAX_LENGTH_OF_SHIFT_IN_NIGHT_TIMESLOT,new MaxLengthOfShiftInNightTimeSlot(SOFT,-4,null,5));
        constraintMap.put(ConstraintSubType.DISLIKE_NIGHT_SHIFS_FOR_NON_NIGHT_WORKERS,new DislikeNightShiftsForNonNightWorkers(SOFT,-4,null));
        return constraintMap;
    }

    public void validateActivityTimeRules(ActivityDTO activityDTO) {
        if(isNull(activityDTO.getActivityRulesSettings().getShortestTime())){
            throw new ActionNotPermittedException("Shortest Time configuration is missing in "+activityDTO.getName());
        }
        if(isNull(activityDTO.getActivityRulesSettings().getRecurrenceTimes())){
            throw new ActionNotPermittedException("Recurrence Times configuration is missing in "+activityDTO.getName());
        }
        if(isNull(activityDTO.getActivityRulesSettings().getLongestTime())){
            throw new ActionNotPermittedException("Longest Time configuration is missing in "+activityDTO.getName());
        }
        if (activityDTO.getActivityRulesSettings().getShortestTime() > activityDTO.getActivityRulesSettings().getLongestTime()) {
            throw new ActionNotPermittedException("Shortest Time can't be Greater than longest time"+activityDTO.getName());
        }
    }


    private List<ALI> getInterval(Activity activity, ZonedDateTime zonedDateTime, Duration duration, StaffingLevelActivity staffingLevelActivity) {
        List<ALI> perDayALIList = new ArrayList<>();
        for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
            //Create same ALI till - Max demand for particular [Interval/TimeSlot]
            ALI activityLineInterval = ALI.builder().id(idGenerator()).activity(activity).duration(Math.abs(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY))).required(i < staffingLevelActivity.getMinNoOfStaff()).start(zonedDateTime).staffNo(i).build();
            perDayALIList.add(activityLineInterval);
        }
        return perDayALIList;

    }

    public List<Shift> getShiftRequestPhase(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, StaffingLevelSolution staffingLevelSolution, Map<BigInteger, Activity> activityMap) {
        List<Shift> shiftImpList = new ArrayList<>();
        Map<Long, Staff> employeeMap = staffingLevelSolution.getStaffs().stream().collect(Collectors.toMap(k -> k.getEmployment().getId(), v -> v));
        Map<LocalDate,List<ALI>> activityLineIntervalMap = staffingLevelSolution.getActivityLineIntervals().stream().collect(Collectors.groupingBy(activityLineInterval -> activityLineInterval.getStart().toLocalDate()));
        for (ShiftDTO shiftDTO : shiftPlanningProblemSubmitDTO.getShifts()) {
            if (employeeMap.containsKey(shiftDTO.getEmploymentId())) {
                List<ShiftActivity> actualShiftActivities = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftDTO.getActivities(), ShiftActivity.class);
                actualShiftActivities.forEach(shiftActivity -> {
                    if(activityMap.containsKey(shiftActivity.getActivityId())){
                        shiftActivity.setActivity(activityMap.get(shiftActivity.getActivityId()));
                    }
                });
                LocalDate startDate = asLocalDate(shiftDTO.getStartDate());
                Staff employee = employeeMap.get(shiftDTO.getEmploymentId());
                if(employee.getBreakSettings()!=null){
                    employee.getBreakSettings().setActivity(activityMap.get(employee.getBreakSettings().getActivityId()));
                }
                Shift shiftImp = Shift.builder()
                        .startDate(startDate)
                        .startTime(asLocalTime(shiftDTO.getStartDate()))
                        .endDate(asLocalDate(shiftDTO.getEndDate()))
                        .endTime(asLocalTime(shiftDTO.getEndDate()))
                        .actualShiftActivities(actualShiftActivities)
                        .id(shiftDTO.getId())
                        .isCreatedByStaff(true)
                        .plannedMinutesOfTimebank(shiftDTO.getPlannedMinutesOfTimebank())
                        .restingMinutes(0)
                        .scheduledMinutes(shiftDTO.getScheduledMinutes())
                        .isLocked(shiftPlanningProblemSubmitDTO.getLockedShiftIds().contains(shiftDTO.getId()))
                        .breakActivities(new ArrayList<>())
                        .durationMinutes(shiftDTO.getDurationMinutes())
                        .staff(employeeMap.get(shiftDTO.getEmploymentId()))
                        .build();
                updateActivityLineInterval(shiftDTO,activityLineIntervalMap,shiftImp);
                shiftImpList.add(shiftImp);
            }
        }
        return shiftImpList;
    }

    private List<ALI> updateActivityLineInterval(ShiftDTO shiftDTO, Map<LocalDate, List<ALI>> activityLineIntervalMap, Shift shift) {
        LocalDate startDate = asLocalDate(shiftDTO.getStartDate());
        LocalDate endDate = asLocalDate(shiftDTO.getEndDate());
        List<ALI> activityLineIntervals = activityLineIntervalMap.getOrDefault(startDate,new ArrayList<>());
        if(!startDate.equals(endDate) && activityLineIntervalMap.keySet().size()>1){   // It works in case of multi days planning
            activityLineIntervals.addAll(activityLineIntervalMap.getOrDefault(endDate,new ArrayList<>()));
        }
        List<ALI> overallActivityLineIntervals = new ArrayList<>();
        for (ALI activityLineInterval : activityLineIntervals) {
            if(shiftDTO.getInterval().overlaps(activityLineInterval.getInterval()) && !shift.getActivityLineIntervals().stream().anyMatch(ali -> ali.getInterval().overlaps(activityLineInterval.getInterval()))){
                activityLineInterval.setActualShiftId(shift.getId());
                activityLineInterval.setShift(shift);
                shift.getActivityLineIntervals().add(activityLineInterval);
                activityLineInterval.setShift(shift);
            }
        }
        activityLineIntervals.removeAll(shift.getActivityLineIntervals());
        Object[] objects = getMergedShiftActivitys(shift);
        shift.setShiftActivities((List<ShiftActivity>)objects[0]);
        if(isCollectionNotEmpty(shift.getShiftActivities())){
            shift.getShiftActivities().sort(Comparator.comparing(shiftActivity -> shiftActivity.getStartDate()));
            shift.setStartDate(startDate);
            shift.setStartTime(asLocalTime(shiftDTO.getStartDate()));
            shift.setEndDate(asLocalDate(shiftDTO.getEndDate()));
            shift.setEndTime(asLocalTime(shiftDTO.getEndDate()));
        }
        shift.setActivityIds((Set<BigInteger>)objects[1]);
        shift.setActivitiesTimeTypeIds((Set<BigInteger>)objects[3]);
        shift.setActivitiesPlannedTimeIds((Set<BigInteger>)objects[2]);
        return overallActivityLineIntervals;
    }

    public static Object[] getMergedShiftActivitys(Shift shiftImp) {
        Set<BigInteger> activityIds = new HashSet<>();
        Set<BigInteger> timeTypeIds = new HashSet<>();
        Set<BigInteger> plannedTimeTypeIds = new HashSet<>();
        if (shiftImp.getActivityLineIntervals().isEmpty()) {
            return new Object[]{new ArrayList<>(),activityIds,plannedTimeTypeIds,timeTypeIds};
        }
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        shiftImp.getActivityLineIntervals().sort(Comparator.comparing(ALI::getStart));
        ShiftActivity shiftActivity = shiftImp.getActivityLineIntervals().get(0).getShiftActivity();
        BigInteger id = shiftImp.getActivityLineIntervals().get(0).getActivity().getId();
        for (ALI ali : shiftImp.getActivityLineIntervals()) {
            if (shiftActivity.getInterval().getEnd().equals(ali.getStart()) && id.equals(ali.getActivity().getId())) {
                shiftActivity.setEndDate(ali.getEnd());
            } else if (shiftActivity.getEndDate().equals(ali.getStart()) && !id.equals(ali.getActivity().getId()) || shiftActivity.getEndDate().isBefore(ali.getStart())) {
                activityIds.add(shiftActivity.getActivity().getId());
                timeTypeIds.add(shiftActivity.getActivity().getTimeType().getId());
                plannedTimeTypeIds.addAll(shiftActivity.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
                shiftActivities.add(shiftActivity);
                shiftActivity = ali.getShiftActivity();
                id = ali.getActivity().getId();
            }
            //calculateScheduledAndDurationInMinutes(shiftActivity,shiftImp.getEmployee().getEmployment().getEmploymentLinesByDate(shiftActivity.getStartDate().toLocalDate()));
        }
        //to add last one
        activityIds.add(shiftActivity.getActivity().getId());
        timeTypeIds.add(shiftActivity.getActivity().getTimeType().getId());
        plannedTimeTypeIds.addAll(shiftActivity.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
        shiftActivities.add(shiftActivity);
        shiftActivities.sort(Comparator.comparing(ShiftActivity::getStartDate));
        return new Object[]{shiftActivities,activityIds,plannedTimeTypeIds,timeTypeIds};
    }

    private BigInteger idGenerator() {
        id = id.add(BigInteger.valueOf(1));
        return id;
    }
}
