package com.kairos.shiftplanning.utils;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.activity.ActivityDTO;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.staffing_level.Duration;
import com.kairos.dto.activity.staffing_level.StaffingLevelActivity;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.presence.PresenceStaffingLevelDto;
import com.kairos.dto.activity.wta.basic_details.WTABaseRuleTemplateDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.planner.constarints.ConstraintDTO;
import com.kairos.dto.planner.shift_planning.ShiftPlanningProblemSubmitDTO;
import com.kairos.dto.planner.solverconfig.SolverConfigDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.staff.employment.EmploymentDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.constraint.ConstraintSubType;
import com.kairos.enums.constraint.ConstraintType;
import com.kairos.enums.constraint.ScoreLevel;
import com.kairos.enums.phase.PhaseType;
import com.kairos.persistence.model.staff.personal_details.StaffDTO;
import com.kairos.shiftplanning.constraints.Constraint;
import com.kairos.shiftplanning.constraints.activityconstraint.*;
import com.kairos.shiftplanning.constraints.unitconstraint.DislikeNightShiftsForNonNightWorkers;
import com.kairos.shiftplanning.constraints.unitconstraint.MaxLengthOfShiftInNightTimeSlot;
import com.kairos.shiftplanning.constraints.unitconstraint.PreferedEmployementType;
import com.kairos.shiftplanning.constraints.unitconstraint.ShiftOnWeekend;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.skill.Skill;
import com.kairos.shiftplanning.domain.staff.*;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLevelMatrix;
import com.kairos.shiftplanning.domain.tag.Tag;
import com.kairos.shiftplanning.domain.timetype.TimeType;
import com.kairos.shiftplanning.domain.unit.*;
import com.kairos.shiftplanning.domain.wta_ruletemplates.WTABaseRuleTemplate;
import com.kairos.shiftplanning.executioner.ShiftPlanningSolver;
import com.kairos.shiftplanning.solution.ShiftRequestPhasePlanningSolution;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.commons.utils.ObjectUtils.newHashSet;
import static com.kairos.enums.constraint.ConstraintSubType.ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH;
import static com.kairos.enums.constraint.ConstraintSubType.MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF;
import static com.kairos.enums.constraint.ScoreLevel.HARD;
import static com.kairos.enums.constraint.ScoreLevel.SOFT;
import static com.kairos.shiftplanning.executioner.ShiftPlanningGenerator.INTERVAL_MINS;

public class ShiftPlanningInitializer {

    private BigInteger id;

    public ShiftRequestPhasePlanningSolution initializeShiftPlanning(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Long unitId = shiftPlanningProblemSubmitDTO.getUnitId();
        List<Long> staffIds = shiftPlanningProblemSubmitDTO.getStaffIds();
        Unit unit = getUnit(unitId, shiftPlanningProblemSubmitDTO);
        List<Long> employmentIds = new LinkedList<>();
        for (StaffDTO staffDTO : shiftPlanningProblemSubmitDTO.getStaffs()) {
            employmentIds.addAll(staffDTO.getEmployments().stream().map(employmentDTO -> employmentDTO.getId()).collect(Collectors.toSet()));
        }
        ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution = new ShiftRequestPhasePlanningSolution();
        updateEmployees(shiftPlanningProblemSubmitDTO, shiftRequestPhasePlanningSolution);
        updateActivityRelatedDetails(shiftPlanningProblemSubmitDTO, shiftRequestPhasePlanningSolution);
        List<ShiftImp> shiftImp = getShiftRequestPhase(shiftPlanningProblemSubmitDTO, shiftRequestPhasePlanningSolution);
        int activitiesRank[] = shiftRequestPhasePlanningSolution.getActivities().stream().mapToInt(activity -> activity.getActivityPrioritySequence()).toArray();
        StaffingLevelMatrix staffingLevelMatrix = new StaffingLevelMatrix(ShiftPlanningUtility.createStaffingLevelMatrix(shiftRequestPhasePlanningSolution.getWeekDates(), shiftRequestPhasePlanningSolution.getActivityLineIntervals(), INTERVAL_MINS, shiftRequestPhasePlanningSolution.getActivities()), activitiesRank);
        shiftRequestPhasePlanningSolution.setUnit(unit);
        shiftRequestPhasePlanningSolution.setStaffingLevelMatrix(staffingLevelMatrix);
        shiftRequestPhasePlanningSolution.setShifts(shiftImp);
        shiftRequestPhasePlanningSolution.setSkillLineIntervals(new ArrayList<>());
        ShiftRequestPhasePlanningSolution planningSolution = new ShiftPlanningSolver(getSolverConfigDTO()).solveProblem(shiftRequestPhasePlanningSolution);
        return planningSolution;
    }

    private Unit getUnit(Long unitId, ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Phase phase = new Phase(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getCurrentPhaseId(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getPhaseEnum(), PhaseType.PLANNING);
        PlanningPeriod planningPeriod = new PlanningPeriod(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getId(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getStartDate(), shiftPlanningProblemSubmitDTO.getPlanningPeriod().getEndDate());
        planningPeriod.setPhase(phase);
        Map<String, TimeSlot> timeSlotMap = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getTimeSlotMap(), Map.class);
        Map<Long, DayType> dayTypeMap = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getDayTypeMap(), Map.class);
        PresencePlannedTime presencePlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getPresencePlannedTime(), PresencePlannedTime.class);
        AbsencePlannedTime absencePlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getAbsencePlannedTime(), AbsencePlannedTime.class);
        NonWorkingPlannedTime nonWorkingPlannedTime = ObjectMapperUtils.copyPropertiesByMapper(shiftPlanningProblemSubmitDTO.getActivityConfiguration().getNonWorkingPlannedTime(), NonWorkingPlannedTime.class);
        Unit unit = Unit.builder().planningPeriod(planningPeriod).id(unitId).dayTypeMap(dayTypeMap).timeSlotMap(timeSlotMap).accessGroupRole(AccessGroupRole.MANAGEMENT).absencePlannedTime(absencePlannedTime).nonWorkingPlannedTime(nonWorkingPlannedTime).presencePlannedTime(presencePlannedTime).build();
        return unit;
    }


    public SolverConfigDTO getSolverConfigDTO() {
        List<ConstraintDTO> constraintDTOS = new ArrayList<>();
        constraintDTOS.add(new ConstraintDTO("Shortest duration for this activity, relative to shift length", "Shortest duration for this activity, relative to shift length", ConstraintType.ACTIVITY, ACTIVITY_SHORTEST_DURATION_RELATIVE_TO_SHIFT_LENGTH, ScoreLevel.HARD, 5, 5l));
        constraintDTOS.add(new ConstraintDTO("Max number of allocations pr. shift for this activity per staff", "Max number of allocations pr. shift for this activity per staff", ConstraintType.ACTIVITY, MAXIMUM_ALLOCATIONS_PER_SHIFT_FOR_THIS_ACTIVITY_PER_STAFF, ScoreLevel.HARD, 5, 5l));
        return new SolverConfigDTO(constraintDTOS);
    }

    public void updateEmployees(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution) {
        List<Employee> employeeList = new ArrayList<>();
        Map[] agreementMap = getCostTimeAgreementMap(shiftPlanningProblemSubmitDTO);
        Map<Long, Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>>> employmentIdAndDateWiseWTARuleTemplateMap = agreementMap[0];
        Map<Long, Map<LocalDate, List<CTARuleTemplate>>> employmentIdAndDateWiseCtaRuleTemplateMap = agreementMap[1];
        for (StaffDTO staffDTO : shiftPlanningProblemSubmitDTO.getStaffs()) {
            for (EmploymentDTO employmentDTO : staffDTO.getEmployments()) {
                EmploymentType employmentType = getEmploymentType(employmentDTO);
                Map<LocalDate, Function> dateWiseFunctionMap = ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getDateWiseFunctionMap(),Map.class);
                Expertise expertise = ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getExpertise(),Expertise.class);
                Employment employment = getEmployment(employmentDTO, employmentType, dateWiseFunctionMap, expertise);
                Employee employee = Employee.builder()
                        .id(staffDTO.getId())
                        .name(staffDTO.getFirstName())
                        .skillSet((Set<Skill>) ObjectMapperUtils.copyCollectionPropertiesByMapper(new HashSet(staffDTO.getSkills()), Skill.class))
                        .employment(employment)
                        .nightWorker(staffDTO.isNightWorker())
                        .localDateCTARuletemplateMap(employmentIdAndDateWiseCtaRuleTemplateMap.get(employment.getId()))
                        .functionalBonus(new HashMap<>())
                        .staffChildDetails(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffDTO.getStaffChildDetails(),StaffChildDetail.class))
                        .seniorAndChildCareDays(ObjectMapperUtils.copyPropertiesByMapper(staffDTO.getSeniorAndChildCareDays(),SeniorAndChildCareDays.class))
                        .tags(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffDTO.getTags(),Tag.class))
                        .teams(ObjectMapperUtils.copyCollectionPropertiesByMapper(staffDTO.getTeams(),Team.class))
                        .unit(shiftRequestPhasePlanningSolution.getUnit())
                        .wtaRuleTemplateMap(employmentIdAndDateWiseWTARuleTemplateMap.get(employment.getId()))
                        .expertiseNightWorkerSetting(ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getExpertiseNightWorkerSetting(),ExpertiseNightWorkerSetting.class))
                        .breakSettings(ObjectMapperUtils.copyPropertiesByMapper(employmentDTO.getBreakSettings(),BreakSettings.class))
                        .build();
                employeeList.add(employee);
            }
        }
        shiftRequestPhasePlanningSolution.setEmployees(employeeList);
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

    private Map[] getCostTimeAgreementMap(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO) {
        Map<Long, Map<LocalDate, List<CTARuleTemplate>>> employmentIdAndDateWiseCtaRuleTemplateMap = new HashMap<>();
        Map<Long, Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>>> employmentIdAndDateWiseWTARuleTemplateMap = new HashMap<>();
        ZonedDateTime startDate = asZonedDateTime(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getStartDate(), LocalTime.MIDNIGHT);
        ZonedDateTime endDate = asZonedDateTime(shiftPlanningProblemSubmitDTO.getPlanningPeriod().getStartDate(), LocalTime.MIDNIGHT);
        for (Map.Entry<Long, List<CTAResponseDTO>> employmentIdAndCtaResponse : shiftPlanningProblemSubmitDTO.getEmploymentIdAndCTAResponseMap().entrySet()) {
            List<WTAResponseDTO> wtaResponseDTOS = shiftPlanningProblemSubmitDTO.getEmploymentIdAndWTAResponseMap().get(employmentIdAndCtaResponse.getKey());
            Map<LocalDate, List<CTARuleTemplate>> ctaRuleTemplatesMap = new HashMap<>();
            Map<LocalDate, Map<ConstraintSubType, WTABaseRuleTemplate>> localDateWTARuletemplateMap = new HashMap<>();
            while (!startDate.isAfter(endDate)){
                AtomicReference<ZonedDateTime> zonedDateTimeAtomicReference = new AtomicReference<>(startDate);
                WTAResponseDTO wtaResponseDTO = wtaResponseDTOS.stream().filter(wtaResponse -> wtaResponse.isValidWorkTimeAgreement(zonedDateTimeAtomicReference.get().toLocalDate())).findFirst().orElseThrow(()->new DataNotFoundByIdException("WTA Not Found for "+employmentIdAndCtaResponse.getKey()));
                localDateWTARuletemplateMap.put(startDate.toLocalDate(),getWTARuletemplateMap(wtaResponseDTO));
                CTAResponseDTO ctaResponseDTO = employmentIdAndCtaResponse.getValue().stream().filter(ctaResponse -> ctaResponse.isValidCostTimeAgreement(zonedDateTimeAtomicReference.get().toLocalDate())).findFirst().orElseThrow(()->new DataNotFoundByIdException("CTA not Found for "+employmentIdAndCtaResponse.getKey()));
                ctaRuleTemplatesMap.put(startDate.toLocalDate(),ObjectMapperUtils.copyCollectionPropertiesByMapper(ctaResponseDTO.getRuleTemplates(),CTARuleTemplate.class));
                startDate = startDate.plusDays(1);
            }
            employmentIdAndDateWiseCtaRuleTemplateMap.put(employmentIdAndCtaResponse.getKey(),ctaRuleTemplatesMap);
        }
        return new Map[]{employmentIdAndDateWiseWTARuleTemplateMap,employmentIdAndDateWiseCtaRuleTemplateMap};
    }

    private Map<ConstraintSubType, WTABaseRuleTemplate> getWTARuletemplateMap(WTAResponseDTO wtaResponseDTO) {
        Map<ConstraintSubType, WTABaseRuleTemplate> wtaBaseRuleTemplateMap = new HashMap<>();
        for (WTABaseRuleTemplateDTO ruleTemplate : wtaResponseDTO.getRuleTemplates()) {
            wtaBaseRuleTemplateMap.put(ConstraintSubType.valueOf(ruleTemplate.getWtaTemplateType().toString()),ObjectMapperUtils.copyPropertiesByMapper(ruleTemplate,WTABaseRuleTemplate.class));
        }
        return wtaBaseRuleTemplateMap;
    }


    public void updateActivityRelatedDetails(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution) {
        Map<String, PresenceStaffingLevelDto> presenceStaffingLevel = shiftPlanningProblemSubmitDTO.getStaffingLevel().getPresenceStaffingLevel();
        Map<LocalDate, List<StaffingLevelInterval>> localDateStaffingLevelTimeSlotMap = presenceStaffingLevel.entrySet().stream().collect(Collectors.toMap(k -> asLocalDate(k.getKey()), v -> v.getValue().getPresenceStaffingLevelInterval()));
        List<ActivityLineInterval> activityLineIntervalList = new ArrayList<>();
        Map<BigInteger, Activity> activityMap = new HashMap<>();
        Map<LocalDate, List<Activity>> dateActivityMap = new HashMap<>();
        Set<LocalDate> localDates = new HashSet<>();
        Map<BigInteger,ActivityDTO> activityDTOMap = shiftPlanningProblemSubmitDTO.getActivities().stream().collect(Collectors.toMap(k->k.getId(),v-v));
        Map<String, List<ActivityLineInterval>> activityLineIntervalMap = new HashMap<>();
        for (Map.Entry<LocalDate, List<StaffingLevelInterval>> localDateListEntry : localDateStaffingLevelTimeSlotMap.entrySet()) {
            localDates.add(localDateListEntry.getKey());
            for (StaffingLevelInterval staffingLevelInterval : localDateListEntry.getValue()) {
                for (StaffingLevelActivity staffingLevelActivity : staffingLevelInterval.getStaffingLevelActivities()) {
                    if (activityDTOMap.containsKey(staffingLevelActivity.getActivityId())) {
                        Activity activity = activityMap.getOrDefault(staffingLevelActivity.getActivityId(), getActivity(activityDTOMap.get(staffingLevelActivity.getActivityId()),shiftPlanningProblemSubmitDTO.getActivityOrderMap()));
                        List<Activity> activityList = dateActivityMap.getOrDefault(localDateListEntry.getKey(), new ArrayList<>());
                        activityList.add(activity);
                        dateActivityMap.put(localDateListEntry.getKey(), activityList);
                        ZonedDateTime zonedDateTime = asZonedDateTime(localDateListEntry.getKey(), staffingLevelInterval.getStaffingLevelDuration().getFrom());
                        //Prepare DateWise Required/Demanding activities for optaplanner
                        List<ActivityLineInterval> activityLineIntervals = getInterval(activity, zonedDateTime, staffingLevelInterval.getStaffingLevelDuration(), staffingLevelActivity, activityLineIntervalMap);
                        activityLineIntervalList.addAll(activityLineIntervals);
                    }
                }
            }

        }
        shiftRequestPhasePlanningSolution.setActivities(dateActivityMap.values().stream().flatMap(activities -> activities.stream()).distinct().sorted(Comparator.comparing(Activity::getActivityPrioritySequence)).collect(Collectors.toList()));
        shiftRequestPhasePlanningSolution.setWeekDates(new ArrayList<>(localDates));
        shiftRequestPhasePlanningSolution.setActivitiesIntervalsGroupedPerDay(activityLineIntervalMap);
        shiftRequestPhasePlanningSolution.setActivitiesPerDay(dateActivityMap);
    }

    private Activity getActivity(ActivityDTO activityDTO,Map<BigInteger,Integer> activityOrderMap) {
        TimeType timeType = TimeType.builder().timeTypeEnum(activityDTO.getBalanceSettingsActivityTab().getTimeType()).timeTypes(TimeTypes.valueOf(activityDTO.getTimeType().getTimeTypes())).breakNotHeldValid(activityDTO.getTimeType().isBreakNotHeldValid()).id(activityDTO.getTimeType().getId()).name(activityDTO.getTimeType().getLabel()).build();
        return Activity.builder()
                .breakAllowed(activityDTO.getRulesActivityTab().isBreakAllowed())
                .cutOffdayValue(activityDTO.getRulesActivityTab().getCutOffdayValue())
                .cutOffIntervalUnit(activityDTO.getRulesActivityTab().getCutOffIntervalUnit())
                .cutOffStartFrom(activityDTO.getRulesActivityTab().getCutOffStartFrom())
                .expertises(activityDTO.getExpertises())
                .fixedTimeValue(activityDTO.getTimeCalculationActivityTab().getFixedTimeValue())
                .id(activityDTO.getId())
                .fullDayCalculationType(activityDTO.getTimeCalculationActivityTab().getFullDayCalculationType())
                .fullWeekCalculationType(activityDTO.getTimeCalculationActivityTab().getFullWeekCalculationType())
                .methodForCalculatingTime(activityDTO.getTimeCalculationActivityTab().getMethodForCalculatingTime())
                .multiplyWithValue(activityDTO.getTimeCalculationActivityTab().getMultiplyWithValue())
                .name(activityDTO.getName())
                .skills(ObjectMapperUtils.copyCollectionPropertiesByMapper(activityDTO.getSkills(), Skill.class))
                .tags(ObjectMapperUtils.copyCollectionPropertiesByMapper(activityDTO.getTags(), Tag.class))
                .timeType(timeType).teamId(activityDTO.getTeamId()).constraints(getActivityConstrainsts(activityDTO)).order(activityOrderMap.get(activityDTO.getId())).activityPrioritySequence(activityDTO.getActivitySequence()).build();
    }

    private Map<ConstraintSubType, Constraint> getActivityConstrainsts(ActivityDTO activityDTO) {
        LongestDuration longestDuration = new LongestDuration(activityDTO.getRulesActivityTab().getLongestTime(), SOFT,-5);
        ShortestDuration shortestDuration = new ShortestDuration(activityDTO.getRulesActivityTab().getShortestTime(), HARD,-2);
        MaxAllocationPerShift maxAllocationPerShift = new MaxAllocationPerShift(activityDTO.getRulesActivityTab().getRecurrenceTimes(), SOFT,-1);//3
        MaxDiffrentActivity maxDiffrentActivity = new MaxDiffrentActivity(3, SOFT,-1);//4
        MinimumLengthofActivity minimumLengthofActivity = new MinimumLengthofActivity(activityDTO.getRulesActivityTab().getShortestTime(), SOFT,-1);//5
        ActivityDayType activityDayType = new ActivityDayType(SOFT,5);
        ActivityRequiredTag activityRequiredTag = new ActivityRequiredTag(HARD,1);
        Map<ConstraintSubType, Constraint> constraintMap = new HashMap<>();
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


    private List<ActivityLineInterval> getInterval(Activity activity, ZonedDateTime zonedDateTime, Duration duration, StaffingLevelActivity staffingLevelActivity, Map<String, List<ActivityLineInterval>> activityLineIntervalMap) {
        List<ActivityLineInterval> perDayALIList = new ArrayList<>();
        for (int i = 0; i < staffingLevelActivity.getMaxNoOfStaff(); i++) {
            //Create same ALI till - Max demand for particular [Interval/TimeSlot]
            String key = zonedDateTime.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "_" + activity.getId() + "_" + i;
            ActivityLineInterval activityLineInterval = ActivityLineInterval.builder().id(idGenerator()).activity(activity).duration(Math.abs(duration.getFrom().get(ChronoField.MINUTE_OF_DAY) - duration.getTo().get(ChronoField.MINUTE_OF_DAY))).required(i < staffingLevelActivity.getMinNoOfStaff()).start(zonedDateTime).staffNo(i).build();
            List<ActivityLineInterval> activityLines = activityLineIntervalMap.getOrDefault(key, new ArrayList<>());
            activityLines.add(activityLineInterval);
            activityLineIntervalMap.put(key, activityLines);
            perDayALIList.add(activityLineInterval);
        }
        return perDayALIList;

    }

    public List<ShiftImp> getShiftRequestPhase(ShiftPlanningProblemSubmitDTO shiftPlanningProblemSubmitDTO, ShiftRequestPhasePlanningSolution shiftRequestPhasePlanningSolution) {
        List<ShiftImp> shiftImpList = new ArrayList<>();
        Map<Long, Employee> employeeMap = shiftRequestPhasePlanningSolution.getEmployees().stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
        Map<LocalDate,List<ActivityLineInterval>> activityLineIntervalMap = shiftRequestPhasePlanningSolution.getActivityLineIntervals().stream().collect(Collectors.groupingBy(activityLineInterval -> activityLineInterval.getStart().toLocalDate()));
        for (ShiftDTO shiftDTO : shiftPlanningProblemSubmitDTO.getShifts()) {
            if (employeeMap.containsKey(shiftDTO.getEmploymentId())) {
                List<ShiftActivity> actualShiftActivities = ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftDTO.getActivities(), ShiftActivity.class);
                LocalDate startDate = asLocalDate(shiftDTO.getStartDate());
                ShiftImp shiftImp = ShiftImp.builder()
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
                        .employee(employeeMap.get(shiftDTO.getEmploymentId()))
                        .build();
                updateActivityLineInterval(shiftDTO,activityLineIntervalMap,shiftImp);
                shiftImpList.add(shiftImp);
            }
        }
        return shiftImpList;
    }

    private List<ActivityLineInterval> updateActivityLineInterval(ShiftDTO shiftDTO, Map<LocalDate, List<ActivityLineInterval>> activityLineIntervalMap, ShiftImp shiftImp) {
        LocalDate startDate = asLocalDate(shiftDTO.getStartDate());
        LocalDate endDate = asLocalDate(shiftDTO.getEndDate());
        List<ActivityLineInterval> activityLineIntervals = activityLineIntervalMap.get(startDate);
        if(!startDate.equals(endDate)){
            activityLineIntervals.addAll(activityLineIntervalMap.get(endDate));
        }
        List<ActivityLineInterval> overallActivityLineIntervals = new ArrayList<>();
        for (ActivityLineInterval activityLineInterval : activityLineIntervals) {
            if(shiftDTO.getInterval().overlaps(activityLineInterval.getInterval())){
                activityLineInterval.setActualShiftId(shiftImp.getId());
                activityLineInterval.setShift(shiftImp);
                shiftImp.getActivityLineIntervals().add(activityLineInterval);
            }
        }
        Object[] objects = ShiftPlanningUtility.getMergedShiftActivitys(shiftImp);
        shiftImp.setShiftActivities((List<ShiftActivity>)objects[0]);
        shiftImp.setActivityIds((Set<BigInteger>)objects[1]);
        shiftImp.setActivitiesTimeTypeIds((Set<BigInteger>)objects[3]);
        shiftImp.setActivitiesPlannedTimeIds((Set<BigInteger>)objects[2]);
        return overallActivityLineIntervals;
    }

    private BigInteger idGenerator() {
        id = id.add(BigInteger.valueOf(1));
        return id;
    }
}
