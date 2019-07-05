package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.service.counter.PlannedHoursCalculationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static junit.framework.TestCase.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PlannedHoursCalculationServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlannedHoursCalculationServiceTest.class);

    @InjectMocks
    private PlannedHoursCalculationService plannedHoursCalculationService;

    private List<Shift> shifts;
    private List<Long> staffIds;
    List<DateTimeInterval> dateTimeIntervals;
    Map<DateTimeInterval, List<Shift>> dateTimeIntervalListMap;

    @Test
    public void getStaffPlannedHoursByRepresentPerStaff(){
        staffIds=new ArrayList<>();
        staffIds.add(920L);
        shifts = ObjectMapperUtils.JsonStringToList(getShift(),Shift.class);
        Map<Object, Double> shiDoubleMap=plannedHoursCalculationService.getStaffPlannedHoursByRepresentPerStaff(staffIds,shifts);
        assertTrue(shiDoubleMap.get(staffIds.get(0)).equals(6.0));
    }

    @Test
    public void getStaffPlannedHoursByRepresentPerStaffNegitive(){
        staffIds=new ArrayList<>();
        staffIds.add(120L);
        shifts = ObjectMapperUtils.JsonStringToList(getShift(),Shift.class);
        Map<Object, Double> shiDoubleMap=plannedHoursCalculationService.getStaffPlannedHoursByRepresentPerStaff(staffIds,shifts);
        assertTrue(shiDoubleMap.get(staffIds.get(0)).equals(0.0));
    }


    public String getShift(){
        return "[ {\n" +
                "  \"id\" : 1652,\n" +
                "  \"deleted\" : false,\n" +
                "  \"startDate\" : 1556780400000,\n" +
                "  \"endDate\" : 1556802000000,\n" +
                "  \"disabled\" : false,\n" +
                "  \"bid\" : 0,\n" +
                "  \"pId\" : 0,\n" +
                "  \"bonusTimeBank\" : 0,\n" +
                "  \"amount\" : 0,\n" +
                "  \"probability\" : 0,\n" +
                "  \"accumulatedTimeBankInMinutes\" : 0,\n" +
                "  \"remarks\" : null,\n" +
                "  \"staffId\" : 920,\n" +
                "  \"phaseId\" : 97,\n" +
                "  \"planningPeriodId\" : null,\n" +
                "  \"weekCount\" : null,\n" +
                "  \"unitId\" : 1172,\n" +
                "  \"scheduledMinutes\" : 360,\n" +
                "  \"durationMinutes\" : 360,\n" +
                "  \"activities\" : [ {\n" +
                "    \"activityId\" : 825,\n" +
                "    \"startDate\" : 1556780400000,\n" +
                "    \"endDate\" : 1556802000000,\n" +
                "    \"scheduledMinutes\" : 360,\n" +
                "    \"durationMinutes\" : 360,\n" +
                "    \"activityName\" : \"Kirsebærhuset\",\n" +
                "    \"bid\" : 0,\n" +
                "    \"pId\" : 0,\n" +
                "    \"reasonCodeId\" : null,\n" +
                "    \"absenceReasonCodeId\" : null,\n" +
                "    \"remarks\" : \"\",\n" +
                "    \"id\" : 1769,\n" +
                "    \"timeType\" : \"WORKING_TYPE\",\n" +
                "    \"backgroundColor\" : \"\",\n" +
                "    \"haltBreak\" : false,\n" +
                "    \"plannedTimeId\" : 2,\n" +
                "    \"breakShift\" : false,\n" +
                "    \"breakReplaced\" : true,\n" +
                "    \"timeBankCTADistributions\" : [ {\n" +
                "      \"ctaName\" : \"Night To Time bank\",\n" +
                "      \"minutes\" : 0,\n" +
                "      \"ctaRuleTemplateId\" : 1851,\n" +
                "      \"ctaDate\" : \"2019-05-02\"\n" +
                "    }, {\n" +
                "      \"ctaName\" : \"Self Paid to Time Bank\",\n" +
                "      \"minutes\" : 0,\n" +
                "      \"ctaRuleTemplateId\" : 1854,\n" +
                "      \"ctaDate\" : \"2019-05-02\"\n" +
                "    }, {\n" +
                "      \"ctaName\" : \"Evening To Time Bank\",\n" +
                "      \"minutes\" : 0,\n" +
                "      \"ctaRuleTemplateId\" : 1855,\n" +
                "      \"ctaDate\" : \"2019-05-02\"\n" +
                "    } ],\n" +
                "    \"payoutPerShiftCTADistributions\" : null,\n" +
                "    \"payoutCtaBonusMinutes\" : 0,\n" +
                "    \"allowedBreakDurationInMinute\" : null,\n" +
                "    \"timeBankCtaBonusMinutes\" : 0,\n" +
                "    \"startLocation\" : null,\n" +
                "    \"endLocation\" : null,\n" +
                "    \"plannedMinutesOfTimebank\" : 0,\n" +
                "    \"plannedMinutesOfPayout\" : 0,\n" +
                "    \"scheduledMinutesOfTimebank\" : 0,\n" +
                "    \"scheduledMinutesOfPayout\" : 0,\n" +
                "    \"status\" : [ ],\n" +
                "    \"interval\" : {\n" +
                "      \"start\" : 1556780400.000000000,\n" +
                "      \"end\" : 1556802000.000000000,\n" +
                "      \"startDate\" : 1556780400000,\n" +
                "      \"endDate\" : 1556802000000,\n" +
                "      \"startLocalDate\" : \"2019-05-02\",\n" +
                "      \"endLocalDate\" : \"2019-05-02\",\n" +
                "      \"milliSeconds\" : 21600000,\n" +
                "      \"startMillis\" : 1556780400000,\n" +
                "      \"endMillis\" : 1556802000000,\n" +
                "      \"startLocalTime\" : [ 7, 0 ],\n" +
                "      \"endLocalTime\" : [ 13, 0 ],\n" +
                "      \"startLocalDateTime\" : [ 2019, 5, 2, 7, 0 ],\n" +
                "      \"endLocalDateTime\" : [ 2019, 5, 2, 13, 0 ],\n" +
                "      \"seconds\" : 21600,\n" +
                "      \"days\" : 0,\n" +
                "      \"hours\" : 6,\n" +
                "      \"minutes\" : 360\n" +
                "    }\n" +
                "  } ],\n" +
                "  \"externalId\" : null,\n" +
                "  \"employmentId\" : 20276,\n" +
                "  \"parentOpenShiftId\" : null,\n" +
                "  \"copiedFromShiftId\" : null,\n" +
                "  \"sickShift\" : false,\n" +
                "  \"functionId\" : null,\n" +
                "  \"staffUserId\" : null,\n" +
                "  \"shiftType\" : null,\n" +
                "  \"timeBankCtaBonusMinutes\" : 0,\n" +
                "  \"plannedMinutesOfTimebank\" : 0,\n" +
                "  \"payoutCtaBonusMinutes\" : 0,\n" +
                "  \"plannedMinutesOfPayout\" : 0,\n" +
                "  \"scheduledMinutesOfTimebank\" : 0,\n" +
                "  \"scheduledMinutesOfPayout\" : 0,\n" +
                "  \"interval\" : {\n" +
                "    \"start\" : 1556780400.000000000,\n" +
                "    \"end\" : 1556802000.000000000,\n" +
                "    \"startDate\" : 1556780400000,\n" +
                "    \"endDate\" : 1556802000000,\n" +
                "    \"startLocalDate\" : \"2019-05-02\",\n" +
                "    \"endLocalDate\" : \"2019-05-02\",\n" +
                "    \"milliSeconds\" : 21600000,\n" +
                "    \"startMillis\" : 1556780400000,\n" +
                "    \"endMillis\" : 1556802000000,\n" +
                "    \"startLocalTime\" : [ 7, 0 ],\n" +
                "    \"endLocalTime\" : [ 13, 0 ],\n" +
                "    \"startLocalDateTime\" : [ 2019, 5, 2, 7, 0 ],\n" +
                "    \"endLocalDateTime\" : [ 2019, 5, 2, 13, 0 ],\n" +
                "    \"seconds\" : 21600,\n" +
                "    \"days\" : 0,\n" +
                "    \"hours\" : 6,\n" +
                "    \"minutes\" : 360\n" +
                "  },\n" +
                "  \"minutes\" : 360\n" +
                "}]";
    }
}
