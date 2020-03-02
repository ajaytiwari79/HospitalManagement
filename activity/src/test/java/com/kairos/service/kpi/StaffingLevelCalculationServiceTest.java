package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.staffing_level.StaffingLevelInterval;
import com.kairos.dto.activity.staffing_level.StaffingLevelSetting;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.staffing_level.StaffingLevel;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.StaffingLevelCalculationKPIService;
import com.kairos.service.staffing_level.StaffingLevelService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

import static com.kairos.commons.utils.DateUtils.*;
import static com.kairos.enums.kpi.CalculationType.PRESENCE_UNDER_STAFFING;
import static org.mockito.ArgumentMatchers.*;

/**
 * Created By G.P.Ranjan on 26/2/20
 **/
@RunWith(MockitoJUnitRunner.class)
public class StaffingLevelCalculationServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(StaffingLevelCalculationServiceTest.class);

    @Mock
    private StaffingLevelService staffingLevelService;

    @Mock
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    @Mock
    private KPIBuilderCalculationService.FilterShiftActivity filterShiftActivity;

    @InjectMocks
    private StaffingLevelCalculationKPIService staffingLevelCalculationKPIService;


    private KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo;
    private Long unitId;
    private DateTimeInterval dateTimeInterval;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void setUp(){
        this.unitId = 1172L;
        this.dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-02-27")),asDate(asLocalDate("2020-02-28")));
        this.kpiCalculationRelatedInfo = getKpiCalculationRelatedInfo();
    }

    @Test
    public void testPresenceStaffingLevelDataPerHour(){
        Mockito.when(staffingLevelService.findByUnitIdAndDates(anyLong(),any(Date.class),any(Date.class))).thenReturn(getStaffingLevelList());
        Mockito.when(kpiBuilderCalculationService.getShiftActivityCriteria(any(KPIBuilderCalculationService.KPICalculationRelatedInfo.class))).thenReturn(KPIBuilderCalculationService.ShiftActivityCriteria.builder().teamActivityIds(new HashSet<>()).build());
        Mockito.when(filterShiftActivity.invoke()).thenReturn(kpiBuilderCalculationService.new FilterShiftActivity(kpiCalculationRelatedInfo.getShifts(),null,false));
        Mockito.when(staffingLevelService.updatePresenceStaffingLevelAvailableStaffCount(any(), any(), anyMap())).thenReturn(null);
        Map<Integer, Long> staffingLevelData = staffingLevelCalculationKPIService.getPresenceStaffingLevelDataPerHour(dateTimeInterval, kpiCalculationRelatedInfo);
        Assert.assertEquals(180L,Long.parseLong(staffingLevelData.get(0).toString()));
    }

    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setShifts(ObjectMapperUtils.jsonStringToList(getShift(), ShiftWithActivityDTO.class));
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(PRESENCE_UNDER_STAFFING));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        return kpiCalculationRelatedInfo;
    }

    private List<StaffingLevel> getStaffingLevelList(){
        Calendar cl = Calendar. getInstance();
        cl.setTime(dateTimeInterval.getStartDate());
        StaffingLevelSetting staffingLevelSetting = new StaffingLevelSetting();
        return ObjectUtils.newArrayList(getStaffingLevel(dateTimeInterval.getStartDate(),cl.WEEK_OF_YEAR,unitId,null,staffingLevelSetting));
    }

    private StaffingLevel getStaffingLevel(Date currentDate, Integer weekCount, Long organizationId, BigInteger phaseId, StaffingLevelSetting staffingLevelSetting){
        StaffingLevel staffingLevel = new StaffingLevel(currentDate, weekCount, organizationId, phaseId, staffingLevelSetting);
        List<StaffingLevelInterval> presenceStaffingLevelInterval =new ArrayList<>(96);
        for(int i = 0; i < 96; i++){
            StaffingLevelInterval staffingLevelInterval = new StaffingLevelInterval(i, 5, 10, null);
            staffingLevelInterval.setAvailableNoOfStaff(2);
            presenceStaffingLevelInterval.add(staffingLevelInterval);
        }
        staffingLevel.setPresenceStaffingLevelInterval(presenceStaffingLevelInterval);
        return staffingLevel;
    }

    private String getShift(){
        return "[{\n" +
                "  \"id\" : 1,\n" +
                "  \"startDate\" : \"2020-02-27T00:00:00.000+0000\",\n" +
                "  \"endDate\" : \"2020-02-27T06:00:00.000+0000\",\n" +
                "  \"unitId\" : 2403,\n" +
                "  \"staffId\" : 2588,\n" +
                "  \"employmentId\" : 3057,\n" +
                "  \"activities\" : [ {\n" +
                "    \"status\" : [ \"PUBLISH\" ],\n" +
                "    \"success\" : false,\n" +
                "    \"activityId\" : 1005,\n" +
                "    \"startDate\" : \"2020-02-27T00:00:00.000+0000\",\n" +
                "    \"endDate\" : \"2020-02-27T06:00:00.000+0000\",\n" +
                "    \"scheduledMinutes\" : 300,\n" +
                "    \"durationMinutes\" : 300,\n" +
                "    \"activityName\" : \"Team D\",\n" +
                "    \"id\" : 2,\n" +
                "    \"timeType\" : \"WORKING_TYPE\",\n" +
                "    \"backgroundColor\" : \"#abe7fc\",\n" +
                "    \"haltBreak\" : false,\n" +
                "    \"breakShift\" : false,\n" +
                "    \"breakReplaced\" : true,\n" +
                "    \"timeBankCtaBonusMinutes\" : 50,\n" +
                "    \"timeBankCTADistributions\" : [ {\n" +
                "      \"ctaName\" : \"Night To Time bank\",\n" +
                "      \"ctaRuleTemplateId\" : 4069,\n" +
                "      \"ctaDate\" : \"2020-01-08\",\n" +
                "      \"minutes\" : 50\n" +
                "    }, {\n" +
                "      \"ctaName\" : \"Evening To Time Bank\",\n" +
                "      \"ctaRuleTemplateId\" : 4073,\n" +
                "      \"ctaDate\" : \"2020-01-08\",\n" +
                "      \"minutes\" : 0\n" +
                "    } ],\n" +
                "    \"plannedMinutesOfTimebank\" : 350,\n" +
                "    \"startLocation\" : \"\",\n" +
                "    \"endLocation\" : \"\",\n" +
                "    \"scheduledMinutesOfTimebank\" : 300,\n" +
                "    \"scheduledMinutesOfPayout\" : 0,\n" +
                "    \"plannedTimes\" : [ {\n" +
                "      \"plannedTimeId\" : 2,\n" +
                "      \"startDate\" : \"2020-02-27T00:00:00.000+0000\",\n" +
                "      \"endDate\" : \"2020-02-27T06:00:00.000+0000\"\n" +
                "    } ],\n" +
                "    \"plannedTimeId\" : null,\n" +
                "    \"plannedMinutesOfPayout\" : 0,\n" +
                "    \"payoutCtaBonusMinutes\" : 0,\n" +
                "    \"childActivities\" : [ ],\n" +
                "    \"breakNotHeld\" : false,\n" +
                "    \"breakInterrupt\" : false,\n" +
                "    \"totalPlannedMinutes\" : 350,\n" +
                "    \"totalCtaBonusMinutes\" : 50\n" +
                "  } ],\n" +
                "  \"scheduledMinutes\" : 300,\n" +
                "  \"durationMinutes\" : 300,\n" +
                "  \"editable\" : false,\n" +
                "  \"functionDeleted\" : false,\n" +
                "  \"shiftType\" : \"PRESENCE\",\n" +
                "  \"timeBankCtaBonusMinutes\" : 50,\n" +
                "  \"payoutCtaBonusMinutes\" : 0,\n" +
                "  \"plannedMinutesOfTimebank\" : 350,\n" +
                "  \"plannedMinutesOfPayout\" : 0,\n" +
                "  \"multipleActivity\" : false,\n" +
                "  \"planningPeriodId\" : 360,\n" +
                "  \"phaseId\" : 161,\n" +
                "  \"restingMinutes\" : 0,\n" +
                "  \"escalationFreeShiftIds\" : [ ],\n" +
                "  \"escalationResolved\" : false,\n" +
                "  \"deleted\" : false,\n" +
                "  \"draft\" : false,\n" +
                "  \"hasOriginalShift\" : false,\n" +
                "  \"presence\" : false,\n" +
                "  \"absence\" : false,\n" +
                "  \"minutes\" : 300\n" +
                "}]";
    }
}
