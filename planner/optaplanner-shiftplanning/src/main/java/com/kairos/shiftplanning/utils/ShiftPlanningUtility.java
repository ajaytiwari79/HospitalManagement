package com.kairos.shiftplanning.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.TimeInterval;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.wta.templates.PhaseTemplateValue;
import com.kairos.dto.user.country.agreement.cta.cta_response.DayTypeDTO;
import com.kairos.dto.user.country.time_slot.TimeSlotWrapper;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.Day;
import com.kairos.enums.wta.IntervalUnit;
import com.kairos.enums.wta.MinMaxSetting;
import com.kairos.enums.wta.PartOfDay;
import com.kairos.enums.wta.ShiftLengthAndAverageSetting;
import com.kairos.shiftplanning.constraints.activityconstraint.CountryHolidayCalender;
import com.kairos.shiftplanning.constraints.activityconstraint.DayType;
import com.kairos.shiftplanning.domain.activity.Activity;
import com.kairos.shiftplanning.domain.activity.ActivityLineInterval;
import com.kairos.shiftplanning.domain.activity.ShiftActivity;
import com.kairos.shiftplanning.domain.shift.Shift;
import com.kairos.shiftplanning.domain.shift.ShiftBreak;
import com.kairos.shiftplanning.domain.shift.ShiftImp;
import com.kairos.shiftplanning.domain.staff.Employee;
import com.kairos.shiftplanning.domain.staff.IndirectActivity;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLevelActivityType;
import com.kairos.shiftplanning.domain.staffing_level.StaffingLevelPlannerEntity;
import com.kairos.shiftplanning.domain.unit.Unit;
import com.kairos.shiftplanning.domain.wta.WTABaseRuleTemplate;
import com.kairos.shiftplanning.dto.ShiftDTO;
import com.kairos.shiftplanning.move.helper.ActivityLineIntervalWrapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kairos.commons.utils.CommonsExceptionUtil.throwException;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.constants.CommonConstants.CAMELCASE_DAYS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShiftPlanningUtility {
    public static final int FIRST_BREAK_START_MINUTES = 210;
    public static final int FIRST_BREAK_END_MINUTES = 240;
    public static final int SECOND_BREAK_START_MINUTES = 390;
    public static final int SECOND_BREAK_END_MINUTES = 435;
    public static final int THIRD_BREAK_START_MINUTES = 570;
    public static final int THIRD_BREAK_END_MINUTES = 615;
    private static Logger log = LoggerFactory.getLogger(ShiftPlanningUtility.class);
    public static final String ERROR = "Error {}";

    public static Integer getStaffingLevelSatisfaction(StaffingLevelPlannerEntity staffingLevel) {
        int[] invalidShiftIntervals = new int[1];
        staffingLevel.getIntervals().forEach(slInterval -> {
            int availableEmployees = 0;
            invalidShiftIntervals[0] += availableEmployees;
        });
        return invalidShiftIntervals[0];
    }

    public static Set<DayOfWeek> getValidDays(Map<Long, DayTypeDTO> dayTypeMap, List<Long> dayTypeIds) {
        Set<DayOfWeek> dayOfWeeks = new HashSet<>();
        List<Day> days = dayTypeIds.stream().filter(s -> dayTypeMap.containsKey(s)).flatMap(dayTypeId -> dayTypeMap.get(dayTypeId).getValidDays().stream()).collect(Collectors.toList());
        days.forEach(day -> {
            if (!day.equals(Day.EVERYDAY)) {
                dayOfWeeks.add(DayOfWeek.valueOf(day.name()));
            } else if (day.equals(Day.EVERYDAY)) {
                dayOfWeeks.addAll(Arrays.asList(DayOfWeek.values()));
            }
        });

        return new HashSet<>(dayOfWeeks);
    }

    public static void brakeRuleTemplateAndUpdateViolationDetails(RuleTemplateSpecificInfo infoWrapper,
                                                                  Integer counterCount, boolean isValid,
                                                                  WTABaseRuleTemplate wtaBaseRuleTemplate,
                                                                  Integer totalCounter, String unitType,
                                                                  String unitValue) {
        if (!isValid) {
        }
    }

    public static Integer[] getValueByPhaseAndCounter(Unit unit, List<PhaseTemplateValue> phaseTemplateValues, WTABaseRuleTemplate ruleTemplate) {
        Integer[] limitAndCounter = new Integer[3];
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (unit.getPhase().getId().equals(phaseTemplateValue.getPhaseId())) {
                limitAndCounter[0] = (int) (UserContext.getUserDetails().isStaff() ? phaseTemplateValue.getStaffValue() : phaseTemplateValue.getManagementValue());
                Integer[] counterValue = getCounterValue(unit, phaseTemplateValue, ruleTemplate);
                limitAndCounter[1] = counterValue[0];
                limitAndCounter[2] = counterValue[1];
                break;
            }
        }
        return limitAndCounter;
    }

    public static Integer[] getCounterValue(Unit unit, PhaseTemplateValue phaseTemplateValue, WTABaseRuleTemplate ruleTemplate) {
        Integer totalCounterValue = null;
        if (UserContext.getUserDetails().isStaff() && phaseTemplateValue.isStaffCanIgnore()) {
            totalCounterValue = ruleTemplate.getStaffCanIgnoreCounter();
            if (totalCounterValue == null) {
                throwException(MESSAGE_RULETEMPLATE_COUNTER_VALUE_NOTNULL, ruleTemplate.getName());
            }
        } else if (UserContext.getUserDetails().isManagement() && phaseTemplateValue.isManagementCanIgnore()) {
            totalCounterValue = ruleTemplate.getManagementCanIgnoreCounter();
            if (totalCounterValue == null) {
                throwException(MESSAGE_RULETEMPLATE_COUNTER_VALUE_NOTNULL, ruleTemplate.getName());
            }
        }
        Integer availableCounter = 0;
        return new Integer[]{availableCounter,totalCounterValue};
    }

    public static List<ShiftImp> getShiftsByInterval(DateTimeInterval dateTimeInterval, List<ShiftImp> shifts, TimeInterval timeInterval) {
        List<ShiftImp> updatedShifts = new ArrayList<>();
        shifts.forEach(s -> {
            if (dateTimeInterval.containsAndEqualsEndDate(s.getStart().toDate()) && (timeInterval == null || timeInterval.contains(s.getStart().getMinuteOfDay()))) {
                updatedShifts.add(s);
            }
        });
        return updatedShifts;
    }

    public static DateTimeInterval getIntervalByRuleTemplate(ShiftWithActivityDTO shift, String intervalUnit, long intervalValue) {
        DateTimeInterval interval = null;
        if (intervalValue == 0 || StringUtils.isEmpty(intervalUnit)) {
            throwException(MESSAGE_RULETEMPLATE_INTERVAL_NOTNULL);
        }
        switch (intervalUnit) {
            case CAMELCASE_DAYS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS).plusDays(1), DateUtils.asZoneDateTime(shift.getEndDate()).plusDays((int) intervalValue).truncatedTo(ChronoUnit.DAYS).minusDays(1).plusDays(1));
                break;
            case WEEKS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS).plusDays(1), DateUtils.asZoneDateTime(shift.getEndDate()).plusWeeks((int) intervalValue).truncatedTo(ChronoUnit.DAYS).minusDays(1).plusDays(1));
                break;
            case MONTHS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS).plusDays(1), DateUtils.asZoneDateTime(shift.getEndDate()).plusMonths((int) intervalValue).truncatedTo(ChronoUnit.DAYS).minusDays(1).plusDays(1));
                break;
            case YEARS:
                interval = new DateTimeInterval(DateUtils.asZoneDateTime(shift.getStartDate()).minusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS).plusDays(1), DateUtils.asZoneDateTime(shift.getEndDate()).plusYears((int) intervalValue).truncatedTo(ChronoUnit.DAYS).minusDays(1).plusDays(1));
                break;
            default:
                break;
        }
        return interval;
    }

    public static List<ShiftImp> filterShiftsByPlannedTypeAndTimeTypeIds(List<ShiftImp> shifts, Set<BigInteger> timeTypeIds, Set<BigInteger> plannedTimeIds) {
        List<ShiftImp> shiftImps = new ArrayList<>();
        shifts.forEach(shift -> {
            boolean isValidShift = (CollectionUtils.isNotEmpty(timeTypeIds) && CollectionUtils.containsAny(timeTypeIds, shift.getActivitiesTimeTypeIds())) && (CollectionUtils.isNotEmpty(plannedTimeIds) && CollectionUtils.containsAny(plannedTimeIds, shift.getActivitiesPlannedTimeIds()));
            if (isValidShift) {
                shiftImps.add(shift);
            }

        });
        return shiftImps;
    }

    public static boolean isValidForDay(List<Long> dayTypeIds, Unit unit,DateTime shiftDate) {
        DayOfWeek shiftDay = asLocalDate(shiftDate).getDayOfWeek();
        return getValidDays(unit.getDayTypeMap(), dayTypeIds).stream().anyMatch(day -> day.equals(shiftDay));
    }

    public static int isValid(MinMaxSetting minMaxSetting, int limitValue, int calculatedValue) {
        return minMaxSetting.equals(MinMaxSetting.MINIMUM) ? limitValue - calculatedValue  : calculatedValue - limitValue;
    }

    public static int getValueAccordingShiftLengthAndAverageSetting(ShiftLengthAndAverageSetting shiftLengthAndAverageSetting, ShiftImp shift){
        int returnValue;
        switch (shiftLengthAndAverageSetting){
            case DURATION:returnValue = shift.getDurationMinutes();
                break;
            case SCHEDULED_HOURS:returnValue = shift.getScheduledMinutes();
                break;
            case PLANNED_HOURS:returnValue = shift.getPlannedMinutesOfTimebank();
                break;
            default:returnValue = shift.getMinutes();
        }
        return returnValue;
    }

    public static TimeInterval getTimeSlotByPartOfDay(List<PartOfDay> partOfDays, Map<String, TimeSlotWrapper> timeSlotWrapperMap, ShiftImp shift) {
        TimeInterval timeInterval = null;
        for (PartOfDay partOfDay : partOfDays) {
            if (timeSlotWrapperMap.containsKey(partOfDay.getValue())) {
                TimeSlotWrapper timeSlotWrapper = timeSlotWrapperMap.get(partOfDay.getValue());
                if (partOfDay.getValue().equals(timeSlotWrapper.getName())) {
                    int endMinutesOfInterval = (timeSlotWrapper.getEndHour() * 60) + timeSlotWrapper.getEndMinute();
                    int startMinutesOfInterval = (timeSlotWrapper.getStartHour() * 60) + timeSlotWrapper.getStartMinute();
                    TimeInterval interval = new TimeInterval(startMinutesOfInterval, endMinutesOfInterval);
                    int minuteOfTheDay = shift.getStart().getMinuteOfDay();
                    if (minuteOfTheDay == (int) interval.getStartFrom() || interval.contains(minuteOfTheDay)) {
                        timeInterval = interval;
                        break;
                    }
                }
            }
        }
        return timeInterval;
    }

    public static boolean isValidForPhase(BigInteger phaseId, List<PhaseTemplateValue> phaseTemplateValues) {
        for (PhaseTemplateValue phaseTemplateValue : phaseTemplateValues) {
            if (phaseId.equals(phaseTemplateValue.getPhaseId())) {
                return !phaseTemplateValue.isDisabled();
            }
        }
        return false;
    }

    public static Integer getStaffingLevelSatisfaction(StaffingLevelPlannerEntity staffingLevel, Shift shift) {
        int[] invalidShiftIntervals = new int[1];
        if (shift.getInterval() != null) {
            staffingLevel.getIntervals().forEach(slInterval -> {
                if (shift.getInterval().contains(slInterval.getInterval()) && (!shift.availableThisInterval(slInterval.getInterval()) ||
                        !canEmployeeWorkForActivityType(slInterval.getActivityTypeLevels(), shift.getEmployee())))
                        invalidShiftIntervals[0]++;

            });
        }
        return invalidShiftIntervals[0];
    }

    public static boolean canEmployeeWorkForActivityType(List<StaffingLevelActivityType> staffingLevelActivityTypes, Employee employee) {
        boolean canWork = false;
        if (staffingLevelActivityTypes == null || staffingLevelActivityTypes.isEmpty())
            //TODO return true later and modify
            return canWork;
        else {
            for (StaffingLevelActivityType staffingLevelActivityType : staffingLevelActivityTypes) {
                if (employee.getSkillSet().containsAll(staffingLevelActivityType.getSkillSet()) && staffingLevelActivityType.getMinimumStaffRequired() > 0) {
                    canWork = true;
                    break;
                }
            }
        }

        return canWork;
    }

    public static Comparator getShiftStartTimeComparator() {
        return new Comparator<Shift>() {
            @Override
            public int compare(Shift shift1, Shift shift2) {
                if (shift1.getStart() != null && shift2.getStart() != null && shift1.getEmployee().getId().equals(shift2.getEmployee().getId())) {

                    return shift1.getStart().compareTo(shift2.getStart());
                } else {
                    return -1;
                }
            }
        };
    }

    public static List<ShiftBreak> generateBreaksForShift() {
        return new ArrayList<>();
    }

    public static String getIntervalAsString(Interval interval) {
        return interval.getStart().toString("dd[HH:mm") + "-" + interval.getEnd().toString("HH:mm]");
    }

    public static List<LocalDate> getSortedAndUniqueDates(List<Shift> shifts) {
        List<LocalDate> dates = new ArrayList<>(shifts.stream().map(s -> s.getStart().toLocalDate()).collect(Collectors.toSet()));
        Collections.sort(dates);
        return dates;
    }

    public static List<Shift> sortShifts(List<Shift> shifts) {
        shifts.sort(Comparator.comparing(Shift::getStart));
        return shifts;
    }

    public static List<Interval> getSortedIntervals(List<Shift> shifts) {
        List<Interval> intervals = new ArrayList<>();
        for (Shift s : sortShifts(shifts)) {
            intervals.add(s.getInterval());
        }
        return intervals;
    }

    public static Interval createInterval(LocalDate weekStart, int intervalLength, IntervalUnit intervalUnit) {
        Interval interval = null;
        if (IntervalUnit.WEEKS == intervalUnit) {
            interval = new Interval(weekStart.minusWeeks(intervalLength - 1).toDateTimeAtStartOfDay(), weekStart.plusWeeks(1).toDateTimeAtStartOfDay());
        }
        return interval;
    }

    public static void sortActivityLineIntervals(List<ActivityLineInterval> intervals) {
        intervals.sort(Comparator.comparing(ActivityLineInterval::getStart));
    }

    /**
     * @param activityLineIntervals
     * @param ali
     * @return not null
     */
    public static List<ActivityLineInterval> getOverlappingActivityLineIntervals(List<ActivityLineInterval> activityLineIntervals, ActivityLineInterval ali) {
        List<ActivityLineInterval> overlappingAlis = new ArrayList<>();
        if (activityLineIntervals != null) {
            for (ActivityLineInterval ex : activityLineIntervals) {
                if (ex.getInterval().overlaps(ali.getInterval())) {
                    overlappingAlis.add(ex);
                }
            }
        }
        return overlappingAlis;
    }

    public static List<ActivityLineInterval> getOverlappingActivityLineIntervalsWithInterval(ShiftImp shift, Interval interval) {
        List<ActivityLineInterval> alis = shift.getActivityLineIntervals();
        List<ActivityLineInterval> overlappingAlis = new ArrayList<>();
        for (ActivityLineInterval ali : alis) {
            if (interval.contains(ali.getStart())) {
                overlappingAlis.add(ali);
            }
        }
        return overlappingAlis;
    }
    public static boolean intervalOverlapsBreak(List<ShiftBreak> shiftBreaks, Interval interval) {
        boolean overlaps = false;
        if (shiftBreaks == null) return overlaps;
        for (ShiftBreak shiftBreak : shiftBreaks) {
            if (shiftBreak.getInterval() != null && shiftBreak.getInterval().overlaps(interval)) {
                overlaps = true;
                break;
            }
        }
        return overlaps;
    }

    public static List<ActivityLineIntervalWrapper> toActivityWrapper(List<ActivityLineInterval> alis, ShiftImp shiftImp) {
        List<ActivityLineIntervalWrapper> aliw = new ArrayList<>();
        if (CollectionUtils.isEmpty(alis)) return aliw;
        for (ActivityLineInterval ali : alis) {
            if (Objects.equals(ali.getShift(), shiftImp))
                continue;
            aliw.add(new ActivityLineIntervalWrapper(ali, shiftImp));
        }
        return aliw;
    }

    /*
    Receives length of array as bound so it -1 first thing to keep up with index of array
     */
    public static int[] getRandomRange(int bound, Random random) {
        try {
            if (bound == 1) {
                return new int[]{0, 1};
            }
            bound--;//
            int lower = random.nextInt(bound);
            int diff = random.nextInt(bound) + 1;//+1 in case diff returns 0
            int upper = lower + diff > bound ? bound : lower + diff;
            return new int[]{lower, upper};
        } catch (Exception e) {
            log.error("Bad bound:" + bound, e);
        }
        return new int[]{};
    }

    public static String solvedShiftPlanningProblem(List<ShiftDTO> shiftDTOS, Long unitId) {
        final String baseUrl = "http://192.168.6.211:5555/kairos/activity/api/v1/organization/71/unit/" + unitId + "/sub-shifts";

        HttpClient client = HttpClientBuilder.create().build();
        Map<String, String> header = new HashedMap<>();
        header.put("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ1bHJpa0BrYWlyb3MuY29tIiwic2NvcGUiOlsid2ViY2xpZW50Il0sImRldGFpbHMiOnsiaWQiOjY3LCJ1c2VyTmFtZSI6InVscmlrQGthaXJvcy5jb20iLCJuaWNrTmFtZSI6IlVscmlrIiwiZmlyc3ROYW1lIjoiVWxyaWsiLCJsYXN0TmFtZSI6IlJhc211c3NlbiIsImVtYWlsIjoidWxyaWtAa2Fpcm9zLmNvbSIsInBhc3N3b3JkVXBkYXRlZCI6dHJ1ZSwiYWdlIjo2NiwiY291bnRyeUlkIjpudWxsLCJodWJNZW1iZXIiOmZhbHNlfSwiZXhwIjoxNTIwNDA1NDk3LCJhdXRob3JpdGllcyI6WyI3MV90YWJfNjciLCI3MV90YWJfNjgiLCI3MV90YWJfNjkiLCI3MV90YWJfNjMiLCI3MV90YWJfNjQiLCI3MV90YWJfNjUiLCI3MV90YWJfNjYiLCI3MV90YWJfNjAiLCI3MV90YWJfNjEiLCI3MV90YWJfNjIiLCI3MV90YWJfNTYiLCI3MV90YWJfNTciLCI3MV90YWJfNTgiLCI3MV90YWJfNTkiLCI3MV90YWJfMTA4IiwiNzFfdGFiXzEwOSIsIjcxX3RhYl8xMDYiLCI3MV90YWJfMTA3IiwiNzFfdGFiXzUyIiwiNzFfdGFiXzEwMCIsIjcxX3RhYl8xMDEiLCI3MV90YWJfNTMiLCI3MV90YWJfNTQiLCI3MV90YWJfNTUiLCI3MV90YWJfMTA0IiwiNzFfdGFiXzEwNSIsIjcxX3RhYl81MCIsIjcxX3RhYl8xMDIiLCI3MV90YWJfNTEiLCI3MV90YWJfMTAzIiwiNzFfdGFiXzg5IiwiNzFfdGFiXzgwIiwiNzFfdGFiXzg1IiwiNzFfdGFiXzg2IiwiNzFfdGFiXzg3IiwiNzFfdGFiXzg4IiwiNzFfdGFiXzgxIiwiNzFfdGFiXzgyIiwiNzFfdGFiXzgzIiwiNzFfdGFiXzg0IiwiNzFfdGFiXzc4IiwiNzFfdGFiXzc5IiwiNzFfbW9kdWxlXzEiLCI3MV90YWJfOSIsIjcxX21vZHVsZV80IiwiNzFfbW9kdWxlXzUiLCI3MV90YWJfNyIsIjcxX21vZHVsZV8yIiwiNzFfbW9kdWxlXzMiLCI3MV90YWJfOCIsIjcxX3RhYl81IiwiNzFfbW9kdWxlXzgiLCI3MV90YWJfNzQiLCI3MV90YWJfNzUiLCI3MV90YWJfNiIsIjcxX3RhYl83NiIsIjcxX3RhYl8zIiwiNzFfbW9kdWxlXzYiLCI3MV90YWJfNCIsIjcxX3RhYl83NyIsIjcxX21vZHVsZV83IiwiNzFfdGFiXzEiLCI3MV90YWJfNzAiLCI3MV90YWJfMiIsIjcxX3RhYl83MSIsIjcxX3RhYl83MiIsIjcxX3RhYl83MyIsIjcxX3RhYl8yNyIsIjcxX3RhYl8yOCIsIjcxX3RhYl8yOSIsIjcxX3RhYl8yMyIsIjcxX3RhYl8yNCIsIjcxX3RhYl8yNSIsIjcxX3RhYl8yNiIsIjcxX3RhYl8yMCIsIjcxX3RhYl8yMSIsIjcxX3RhYl8yMiIsIjcxX3RhYl8xNiIsIjcxX3RhYl8xNyIsIjcxX3RhYl8xOCIsIjcxX3RhYl8xOSIsIjcxX3RhYl8xMiIsIjcxX3RhYl8xMyIsIjcxX3RhYl8xNCIsIjcxX3RhYl8xNSIsIjcxX3RhYl85MCIsIjcxX3RhYl85MSIsIjcxX3RhYl85NiIsIjcxX3RhYl85NyIsIjcxX3RhYl8xMCIsIjcxX3RhYl85OCIsIjcxX3RhYl85OSIsIjcxX3RhYl8xMSIsIjcxX3RhYl85MiIsIjcxX3RhYl85MyIsIjcxX3RhYl85NCIsIjcxX3RhYl85NSIsIjcxX3RhYl80NSIsIjcxX3RhYl80NiIsIjcxX3RhYl80NyIsIjcxX3RhYl80OCIsIjcxX3RhYl8xMTEiLCI3MV90YWJfNDEiLCI3MV90YWJfNDIiLCI3MV90YWJfMTEyIiwiNzFfdGFiXzQzIiwiNzFfdGFiXzQ0IiwiNzFfdGFiXzExMCIsIjcxX3RhYl8xMTMiLCI3MV90YWJfNDAiLCI3MV90YWJfMzgiLCI3MV90YWJfMzkiLCI3MV90YWJfMzQiLCI3MV90YWJfMzUiLCI3MV90YWJfMzYiLCI3MV90YWJfMzciLCI3MV90YWJfMzAiLCI3MV90YWJfMzEiLCI3MV90YWJfMzIiLCI3MV90YWJfMzMiXSwianRpIjoiMzcyZTgzMjQtOGRhMS00YTYxLWE4YTctZjA4Mjg5ZjE0MGM1IiwiY2xpZW50X2lkIjoia2Fpcm9zIn0.PrgK1fGis9iyk5mfhY_f_tEb6o_Qkdp4VV86Pr38l4Q");
        HttpUriRequest request = getPutRequest(shiftDTOS, null, null, baseUrl);
        StringBuilder result = new StringBuilder();
        try {
            HttpResponse response = client.execute(request);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            log.info("status {}" , response.getStatusLine());
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            log.error(ERROR,e.getMessage());
        }
        return "";
    }

    public static HttpUriRequest getPutRequest(List<ShiftDTO> shiftDTOS, Map<String, Object> urlParameters, Map<String, String> headers, String url) {
        if (headers == null) headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        HttpPut putRequest = (HttpPut) setHeaders(headers, url);
        if (urlParameters != null) {
            List<BasicNameValuePair> parametersList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : urlParameters.entrySet()) {
                parametersList.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parametersList);
                putRequest.setEntity(entity);
            } catch (UnsupportedEncodingException e) {
                log.error(ERROR,e.getMessage());
            }
        }
        if (shiftDTOS != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String body = mapper.writeValueAsString(shiftDTOS);
                ByteArrayEntity entity = new ByteArrayEntity(body.getBytes());
                putRequest.setEntity(entity);
            } catch (JsonProcessingException e) {
                log.error(ERROR,e.getMessage());
            }

        }
        return putRequest;
    }

    public static HttpUriRequest setHeaders(Map<String, String> headers, String uri) {
        HttpPut request = new HttpPut(uri);
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }

    //TODO n2 complex need to improve
    public static List<ActivityLineIntervalWrapper> buildNullAssignWrappersForExIntervals(List<ActivityLineIntervalWrapper> activityLineIntervalWrappers) {
        List<ActivityLineIntervalWrapper> overlappingAlisWrappers = new ArrayList<>();
        if (activityLineIntervalWrappers.get(0).getShiftImp() == null) return overlappingAlisWrappers;
        List<ActivityLineInterval> exAlisThisShift = activityLineIntervalWrappers.get(0).getShiftImp().getActivityLineIntervals();
        if (exAlisThisShift != null) {
            for (ActivityLineInterval ex : exAlisThisShift) {
                for (ActivityLineIntervalWrapper newAli : activityLineIntervalWrappers) {
                    if (ex.getInterval().overlaps(newAli.getActivityLineInterval().getInterval())) {
                        overlappingAlisWrappers.add(new ActivityLineIntervalWrapper(ex, null));
                        break;
                    }
                }
            }
        }
        return overlappingAlisWrappers;
    }

    public static List<Interval> getMergedIntervals(List<ActivityLineInterval> intervals, boolean ignoreActivities) {
        if (intervals.isEmpty()) {
            return new ArrayList<>();
        }
        intervals.sort(Comparator.comparing(ActivityLineInterval::getStart));
        Interval mergedInterval = intervals.get(0).getInterval();
        BigInteger id = intervals.get(0).getActivity().getId();
        List<Interval> mergedIntervals = new ArrayList<>();
        for (ActivityLineInterval ali : intervals) {
            if (mergedInterval.getEnd().equals(ali.getStart()) && (ignoreActivities || id.equals(ali.getActivity().getId()))) {
                mergedInterval = mergedInterval.withEnd(ali.getEnd());
            } else if ((mergedInterval.getEnd().equals(ali.getStart()) && (ignoreActivities || !id.equals(ali.getActivity().getId()))) || mergedInterval.getEnd().isBefore(ali.getStart())) {
                mergedIntervals.add(mergedInterval);
                mergedInterval = ali.getInterval();
                id = ali.getActivity().getId();
            }
        }
        //to add last one
        mergedIntervals.add(mergedInterval);
        return mergedIntervals;
    }

    public static Object[] getMergedShiftActivitys(List<ActivityLineInterval> intervals) {
        Set<BigInteger> activityIds = new HashSet<>();
        Set<BigInteger> timeTypeIds = new HashSet<>();
        Set<BigInteger> plannedTimeTypeIds = new HashSet<>();
        if (intervals.isEmpty()) {
            return new Object[]{new ArrayList<>(),activityIds,plannedTimeTypeIds,timeTypeIds};
        }
        List<ShiftActivity> shiftActivities = new ArrayList<>();
        intervals.sort(Comparator.comparing(ActivityLineInterval::getStart));
        ShiftActivity shiftActivity = intervals.get(0).getShiftActivity();
        BigInteger id = intervals.get(0).getActivity().getId();
        for (ActivityLineInterval ali : intervals) {
            if (shiftActivity.getInterval().getEnd().equals(ali.getStart()) && id.equals(ali.getActivity().getId())) {
                shiftActivity.setEndTime(ali.getEnd());
            } else if (shiftActivity.getEndTime().equals(ali.getStart()) && !id.equals(ali.getActivity().getId()) || shiftActivity.getEndTime().isBefore(ali.getStart())) {
                activityIds.add(shiftActivity.getActivity().getId());
                timeTypeIds.add(shiftActivity.getActivity().getTimeType().getId());
                plannedTimeTypeIds.addAll(shiftActivity.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
                shiftActivities.add(shiftActivity);
                shiftActivity = ali.getShiftActivity();
                id = ali.getActivity().getId();
            }
        }
        //to add last one
        activityIds.add(shiftActivity.getActivity().getId());
        timeTypeIds.add(shiftActivity.getActivity().getTimeType().getId());
        plannedTimeTypeIds.addAll(shiftActivity.getPlannedTimes().stream().map(plannedTime -> plannedTime.getPlannedTimeId()).collect(Collectors.toSet()));
        shiftActivities.add(shiftActivity);
        shiftActivities.sort(Comparator.comparing(ShiftActivity::getStartTime));
        return new Object[]{shiftActivities,activityIds,plannedTimeTypeIds,timeTypeIds};
    }

    public static int getMinutes(DateTime start, DateTime end) {
        return new Interval(start, end).toDuration().toStandardMinutes().getMinutes();

    }

    public static Interval getPossibleBreakStartInterval(ShiftBreak shiftBreak, ShiftImp shift) {
        switch (shiftBreak.getOrder()) {
            case 1:
                return new Interval(shift.getStart().plusMinutes(FIRST_BREAK_START_MINUTES), shift.getStart().plusMinutes(FIRST_BREAK_END_MINUTES));
            case 2:
                return new Interval(shift.getStart().plusMinutes(SECOND_BREAK_START_MINUTES), shift.getStart().plusMinutes(SECOND_BREAK_END_MINUTES));
            case 3:
                return new Interval(shift.getStart().plusMinutes(THIRD_BREAK_START_MINUTES), shift.getStart().plusMinutes(THIRD_BREAK_END_MINUTES));
            default:
                break;
        }
        return null;
    }

    /**
     * This creates matrix as date:Act1:[1,1]. this means 1 required and 1 optional making min/max as 1:2.
     *
     * @param dates
     * @param alis
     * @param granularity
     * @param activities
     * @return
     */
    public static Map<LocalDate, Object[]> createStaffingLevelMatrix(List<LocalDate> dates, List<ActivityLineInterval> alis, int granularity, List<Activity> activities) {
        Map<LocalDate, Object[]> slMatrix = new HashMap<>();
        for (LocalDate localDate : dates) {
            slMatrix.put(localDate, new int[1440 / granularity][activities.size() * 2]);
        }
        for (ActivityLineInterval ali : alis) {
            if (ali.getActivity().isBlankActivity()) continue;
            if (ali.getActivity().isTypeAbsence()) {
                IntStream.rangeClosed(0, 1440 / granularity - 1).forEach(i -> ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[i][getActivityIndex(ali)]++);

            } else {
                ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[getTimeIndex(ali.getStart(), granularity)][getActivityIndex(ali)]++;
            }
        }
        printStaffingLevelMatrix(slMatrix, null);
        return slMatrix;
    }

    public static void printStaffingLevelMatrix(Map<LocalDate, Object[]> slMatrix, Map<LocalDate, Object[]> originalMatrix) {
        slMatrix.forEach((k, v) -> {
            log.info("date: {}", k);
            int[] idx = {0};
            Arrays.stream(v).forEach(i -> log.info("{} {} {}",k.toDateTimeAtStartOfDay().plusMinutes((idx[0]++) * 15).toString("HH:mm")
                    , (originalMatrix != null ? Arrays.toString((int[]) originalMatrix.get(k)[idx[0] - 1]) : "")
                    , Arrays.toString((int[]) i)));
        });
    }

    public static Map<LocalDate, Object[]> reduceStaffingLevelMatrix(Map<LocalDate, Object[]> slMatrixOriginal, List<ShiftImp> shifts,
                                                                     List<ShiftBreak> shiftBreaks, List<IndirectActivity> indirectActivities, int granularity) {
        long start = System.currentTimeMillis();
        Map<LocalDate, Object[]> slMatrix = deepCopyMatrix(slMatrixOriginal);
        if (log.isDebugEnabled())
            log.debug("1 reduceStaffingLevelMatrix() took {}", (System.currentTimeMillis() - start) / 1000.0);

        Map<BigInteger, List<ShiftBreak>> breaksPerShift = getBreakMap(shiftBreaks);
        for (ShiftImp shift : shifts) {
            if (shift.getInterval() == null) {
                continue;
            }
            if (shift.isAbsenceActivityApplied()) {
                reduceAbsenceStaffingLevel(granularity, slMatrix, shift);
            } else {
                reducePresenceStaffingLevel(shiftBreaks, indirectActivities, granularity, slMatrix, breaksPerShift, shift);
            }
        }
        if (log.isDebugEnabled())
            log.debug("2 reduceStaffingLevelMatrix() took {}", (System.currentTimeMillis() - start) / 1000.0);

        return slMatrix;
    }

    private static void reduceAbsenceStaffingLevel(int granularity, Map<LocalDate, Object[]> slMatrix, ShiftImp shift) {
        for (ActivityLineInterval ali : shift.getActivityLineIntervals()) {//TODO
            if (!ali.isRequired()) continue;
            IntStream.rangeClosed(0, 1440 / granularity - 1).forEach(i -> {
                int[] perIntervalStaffingLevel = ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[i];
                if (perIntervalStaffingLevel[getActivityMinIndex(ali)] > 0) {
                    perIntervalStaffingLevel[getActivityMinIndex(ali)]--;
                } else {
                    perIntervalStaffingLevel[getActivityMaxIndex(ali)]--;
                }
            });
        }
    }

    private static Map<BigInteger, List<ShiftBreak>> getBreakMap(List<ShiftBreak> shiftBreaks) {
        Map<BigInteger, List<ShiftBreak>> breaksPerShift = new HashMap<>();
        if (shiftBreaks != null) {
            shiftBreaks.forEach(sb -> {
                if (!breaksPerShift.containsKey(sb.getShift().getId())) {
                    breaksPerShift.put(sb.getShift().getId(), new ArrayList<>());
                }
                breaksPerShift.get(sb.getShift().getId()).add(sb);
            });
        }
        return breaksPerShift;
    }

    private static void reducePresenceStaffingLevel(List<ShiftBreak> shiftBreaks, List<IndirectActivity> indirectActivities, int granularity, Map<LocalDate, Object[]> slMatrix, Map<BigInteger, List<ShiftBreak>> breaksPerShift, ShiftImp shift) {
        for (ActivityLineInterval ali : shift.getActivityLineIntervals()) {
            int[] perIntervalStaffingLevel = ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[getTimeIndex(ali.getStart(), granularity)];
            if (shiftBreaks != null && intervalOverlapsBreak(breaksPerShift.get(ali.getShift().getId()), ali.getInterval())) {
                continue;
            }
            if (indirectActivities != null && intervalOverlapsIndirectActivities(indirectActivities, ali.getInterval(), shift.getEmployee())) {
                continue;
            }
            if (perIntervalStaffingLevel[getActivityMinIndex(ali)] > 0) {
                perIntervalStaffingLevel[getActivityMinIndex(ali)]--;
            } else {
                perIntervalStaffingLevel[getActivityMaxIndex(ali)]--;
            }
        }
    }

    private static boolean intervalOverlapsIndirectActivities(List<IndirectActivity> indirectActivities, Interval interval, Employee employee) {
        for (IndirectActivity ic : indirectActivities) {
            if (ic.getInterval() != null && ic.getInterval().overlaps(interval) && ic.getEmployees().contains(employee))
                return true;
        }
        return false;
    }

    public static Map<LocalDate, Object[]> reduceALIsFromStaffingLevelMatrix(Map<LocalDate, Object[]> slMatrixOriginal, List<ActivityLineInterval> alis, int granularity) {
        long start = System.currentTimeMillis();
        Map<LocalDate, Object[]> slMatrix = deepCopyMatrix(slMatrixOriginal);
        if (log.isDebugEnabled())
            log.debug("1 reduceStaffingLevelMatrix() took {}",(System.currentTimeMillis() - start) / 1000.0);
        for (ActivityLineInterval ali : alis) {
            if (ali.getActivity().isTypeAbsence()) {
                IntStream.rangeClosed(0, 1440 / granularity - 1).forEach(i -> {
                    int[] perIntervalStaffingLevel = ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[i];
                    if (perIntervalStaffingLevel[getActivityMinIndex(ali)] > 0) {
                        perIntervalStaffingLevel[getActivityMinIndex(ali)]--;
                    } else {
                        perIntervalStaffingLevel[getActivityMaxIndex(ali)]--;
                    }
                });
            } else {
                //first get index of twice-2 and then twice-1
                int[] perIntervalStaffingLevel = ((int[][]) slMatrix.get(ali.getStart().toLocalDate()))[getTimeIndex(ali.getStart(), granularity)];
                if (perIntervalStaffingLevel[getActivityMinIndex(ali)] > 0) {
                    perIntervalStaffingLevel[getActivityMinIndex(ali)]--;
                } else {
                    perIntervalStaffingLevel[getActivityMaxIndex(ali)]--;
                }
            }
        }
        if (log.isDebugEnabled())
            log.debug("2 reduceStaffingLevelMatrix() took {}",(System.currentTimeMillis() - start) / 1000.0);
        return slMatrix;
    }

    private static Map<LocalDate, Object[]> deepCopyMatrix(Map<LocalDate, Object[]> slMatrixOriginal) {
        Map<LocalDate, Object[]> copy = new HashMap<>();
        for (Map.Entry<LocalDate,Object[]> localDateEntry : slMatrixOriginal.entrySet()) {
            int[][] array = (int[][]) localDateEntry.getValue();
            final int[][] result = new int[array.length][];
            for (int i = 0; i < array.length; i++) {
                result[i] = Arrays.copyOf(array[i], array[i].length);
            }
            copy.put(localDateEntry.getKey(), result);
        }
        return copy;
    }

    public static int[] getTotalMissingMinAndMaxStaffingLevels(Map<LocalDate, Object[]> slMatrix, int[] activitiesRank) {
        long start = System.currentTimeMillis();
        int[] missingMinAndMax = new int[2];
        slMatrix.forEach((date, m) -> {
            int[][] matrix = (int[][]) m;
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    //missingMinAndMax[j%2==0?0:1]+=matrix[i][j];//0,2,4 are for mins
                    missingMinAndMax[j % 2 == 0 ? 0 : 1] += Math.abs(matrix[i][j] * activitiesRank[j / 2]);//0,2,4 are for mins
                }
            }
        });
        if (log.isDebugEnabled())
            log.debug("getTotalMissingMinAndMaxStaffingLevels() took {}",(System.currentTimeMillis() - start) / 1000.0);
        return missingMinAndMax;
    }

    public static int getTimeIndex(DateTime dateTime, int granularity) {
        return dateTime.getMinuteOfDay() / granularity;
    }

    //activity with order 1 can return 0 if min and 1 if max
    public static int getActivityIndex(ActivityLineInterval activityLineInterval) {
        return activityLineInterval.getActivity().getOrder() * 2 - (activityLineInterval.isRequired() ? 2 : 1);
    }

    public static int getActivityMinIndex(ActivityLineInterval activityLineInterval) {
        return activityLineInterval.getActivity().getOrder() * 2 - 2;
    }

    public static int getActivityMaxIndex(ActivityLineInterval activityLineInterval) {
        return activityLineInterval.getActivity().getOrder() * 2 - 1;
    }

    public static boolean intervalConstainsTimeIncludingEnd(Interval interval, DateTime dateTime) {
        return interval.contains(dateTime) || interval.getEnd().isEqual(dateTime);
    }

    public static boolean isValidForDayType(Shift shift,List<DayType> dayTypes){
        boolean valid = false;
        DateTimeInterval shiftInterval = new DateTimeInterval(shift.getStart().getMillis(),shift.getEnd().getMillis());
        for (DayType dayType : dayTypes) {
            if (dayType.isHolidayType()) {
                valid = isHolidayTypeValid(valid, shiftInterval, dayType);
            } else {
                valid = dayType.getValidDays() != null && dayType.getValidDays().contains(Day.valueOf(shiftInterval.getStartLocalDate().getDayOfWeek().toString()));
            }
            if (valid) {
                break;
            }
        }
        return valid;
    }

    private static boolean isHolidayTypeValid(boolean valid, DateTimeInterval shiftInterval, DayType dayType) {
        for (CountryHolidayCalender countryHolidayCalender : dayType.getCountryHolidayCalenders()) {
            DateTimeInterval dateTimeInterval;
            if (dayType.isAllowTimeSettings()) {
                java.time.LocalTime holidayEndTime = countryHolidayCalender.getEndTime().get(ChronoField.MINUTE_OF_DAY)==0 ? java.time.LocalTime.MAX: countryHolidayCalender.getEndTime();
                dateTimeInterval = new DateTimeInterval(DateUtils.asDate(countryHolidayCalender.getHolidayDate(), countryHolidayCalender.getStartTime()), DateUtils.asDate(countryHolidayCalender.getHolidayDate(), holidayEndTime));
            }else {
                dateTimeInterval = new DateTimeInterval(DateUtils.asDate(countryHolidayCalender.getHolidayDate()), DateUtils.asDate(countryHolidayCalender.getHolidayDate().plusDays(1)));
            }
            valid = dateTimeInterval.overlaps(shiftInterval);
            if (valid) {
                break;
            }
        }
        return valid;
    }

}