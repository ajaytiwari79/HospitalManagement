package com.kairos.service.kpi;


import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.PayLevelKPIService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;

@RunWith(MockitoJUnitRunner.class)
public class PayLevelKpiTest {

    @InjectMocks
    private PayLevelKPIService payLevelKPIService;
    @Mock
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    private List<Long> staffIds;
    private List<StaffKpiFilterDTO> staffKpiFilterDTOS;
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
        staffIds.add(2364l);
        unitId =1172l;
        dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-02-01")),asDate(asLocalDate("2020-02-29")));
        filterType =FilterType.EMPLOYMENT_SUB_TYPE;
        staffKpiFilterDTOS =getStaffKpiFilterDTOS();
        kpiCalculationRelatedInfo=getKpiCalculationRelatedInfo();

    }

    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(STAFF_SKILLS_COUNT));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setStaffIds(staffIds);
        kpiCalculationRelatedInfo.setStaffIdAndStaffKpiFilterMap(getStaffIdAndStaffKpiFilterMap());
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        kpiCalculationRelatedInfo.setFilterBasedCriteria(getFilterBasedCrieteia());
        return kpiCalculationRelatedInfo;
    }

    private List<StaffKpiFilterDTO> getStaffKpiFilterDTOS(){
        return ObjectUtils.newArrayList(getStaffKpiFilterDTO());
    }


    private StaffKpiFilterDTO  getStaffKpiFilterDTO(){
        StaffKpiFilterDTO staffKpiFilterDTO = new StaffKpiFilterDTO();
        staffKpiFilterDTO.setId(staffIds.get(0));
        staffKpiFilterDTO.setEmployment(ObjectMapperUtils.jsonStringToList(getEmployment(), EmploymentWithCtaDetailsDTO.class));
        return staffKpiFilterDTO;
    }

    private Map<FilterType,List> getFilterBasedCrieteia(){
        Map<FilterType,List> filterTypeListMap = new HashMap<>();
        List<String> valueList = new ArrayList<>();
        valueList.add(EmploymentSubType.MAIN.value);
        valueList.add(EmploymentSubType.SECONDARY.value);
        filterTypeListMap.put(filterType,valueList);
        return filterTypeListMap;
    }


    private String getEmployment() {
        return "[{\n" +
                " \"employmentLines\": [{\n" +
                " \"hourlyCost\": null, \n" +
                " \"totalWeeklyHours\": 37.0,\n" +
                " \"seniorityLevel\": null,\n" +
                " \"workingDaysInWeek\": 5,\n" +
                " \"endDate\": null,\n" +
                "\"employmentSubType\": null,\n"+
                "\"employmentStatus\": null,\n"+
                "\"id\": 2989,\n" +
                "\"totalWeeklyMinutes\": 2220,\n"+
                "\"employmentTypeId\": 14046,\n"+
                "\"fullTimeWeeklyMinutes\": 2220,\n"+
                "\"startDate\": \"2020-01-20\",\n"+
                "\"avgDailyWorkingHours\": 0.0,\n"+
                "\"payGradeLevel\": 22\n" +
                "}],\n" +
                "\"endDate\": null,\n"+
                "\"id\": 2988,\n"+
                "\"unitId\": null,\n"+
                "\"accumulatedTimebankMinutes\": 0,\n"+
                "\"employmentTypeId\": null,\n"+
                "\"startDate\": \"2020-01-20\",\n"+
                "\"accumulatedTimebankDate\": null\n" +
                "}]";
    }


    private Map<Long,StaffKpiFilterDTO> getStaffIdAndStaffKpiFilterMap(){
        Map<Long,StaffKpiFilterDTO> longStaffKpiFilterDTOMap = new HashMap<>();
        longStaffKpiFilterDTOMap.put(staffIds.get(0),getStaffKpiFilterDTO());
        return longStaffKpiFilterDTOMap;
    }

    @Test
    public void testPayLevelKpiService(){
        double totalWeeklyHours = payLevelKPIService.getPayLevelOfMainEmploymentOfStaff(staffIds.get(0),kpiCalculationRelatedInfo,dateTimeInterval.getStartLocalDate());
        Assert.assertEquals(22.0d,totalWeeklyHours,22.0d);
    }



}
