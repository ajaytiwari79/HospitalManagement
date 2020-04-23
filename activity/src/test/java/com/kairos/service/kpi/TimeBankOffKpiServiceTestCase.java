package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.PayLevelKPIService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;
import static com.kairos.enums.kpi.CalculationType.TODO_STATUS;

@RunWith(MockitoJUnitRunner.class)
public class TimeBankOffKpiServiceTestCase {
    @InjectMocks
    private PayLevelKPIService payLevelKPIService;
    @Mock
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    private List<Long> staffIds;
    private Long unitId;
    private DateTimeInterval dateTimeInterval;
    private KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo;
    private FilterType filterType;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void setUp(){
        staffIds = new ArrayList<>();
        staffIds.add(1475l);
        unitId =1172l;
        dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-04-01")),asDate(asLocalDate("2020-04-29")));
        filterType =FilterType.EMPLOYMENT_SUB_TYPE;
        kpiCalculationRelatedInfo=getKpiCalculationRelatedInfo();

    }

    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(TODO_STATUS));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setStaffIds(staffIds);
        kpiCalculationRelatedInfo.setStaffIdAndStaffKpiFilterMap(getStaffIdAndStaffKpiFilterMap());
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        kpiCalculationRelatedInfo.setFilterBasedCriteria(getFilterBasedCrieteia());
        return kpiCalculationRelatedInfo;
    }


    private StaffKpiFilterDTO  getStaffKpiFilterDTO(){
        StaffKpiFilterDTO staffKpiFilterDTO = new StaffKpiFilterDTO();
        staffKpiFilterDTO.setId(staffIds.get(0));
        staffKpiFilterDTO.setEmployment(ObjectMapperUtils.jsonStringToList(getTodo(), EmploymentWithCtaDetailsDTO.class));
        return staffKpiFilterDTO;
    }

    private Map<FilterType,List> getFilterBasedCrieteia(){
        Map<FilterType,List> filterTypeListMap = new HashMap<>();
        List<String> valueList = new ArrayList<>();
        valueList.add(ShiftStatus.APPROVE.name());
        valueList.add(EmploymentSubType.SECONDARY.value);
        filterTypeListMap.put(filterType,valueList);
        return filterTypeListMap;
    }


    private String getTodo(){
       return  "[{\n" +
                "\"_id\": 1,\n" +
                "\"type\" : \"APPROVAL_REQUIRED\",\n" +
                "\"subtype\" : \"ABSENCE_WITH_TIME\",\n" +
                "\"entityId\" : 3271,\n" +
                "\"subEntityId\" : 495,\n"+
                "\"status\" : \"APPROVE\",\n"+
                "\"shiftDate\" : \"2020-04-06T13:17:27.039Z\",\n"+
                "\"description\" : \"An activity <span class='activity-details'>SYRENHUSET</span> has been requested for <s pan class='activity-details'>Sep 11,2019</span>\",\n" +
                 "\"staffId\" : 1475,\n" +
                "\"employmentId\" : 1426,\n" +
                "\"unitId\" : 1387,\n" +
                "\"activityName\" : \"SYRENHUSET\",\n" +
                "\"approvedOn\" : \"2020-04-06T13:17:27.039Z\",\n" +
                "\"createdAt\" : \"2020-04-06T13:17:27.039Z\",\n" +
                "\"updatedAt\" : \"2020-04-06T13:17:27.039Z\",\n" +
                "\"deleted\" : false,\n" +
                "\"_class\" : \"com.kairos.persistence.model.todo.Todo\",n" +
                "\"requestedOn\" : \"2020-04-06T13:17:27.039Z\",\n" +
                "}]";
    }


    @Test
    public void testPayLevelKpiService(){
        double totalWeeklyHours = payLevelKPIService.getPayLevelOfMainEmploymentOfStaff(staffIds.get(0),kpiCalculationRelatedInfo,dateTimeInterval.getStartLocalDate());
        Assert.assertEquals(22.0d,totalWeeklyHours,22.0d);
    }



}
