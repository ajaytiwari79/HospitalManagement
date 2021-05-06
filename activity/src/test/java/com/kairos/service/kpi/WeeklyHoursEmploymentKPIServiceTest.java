package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.KPICalculationRelatedInfo;
import com.kairos.service.counter.WeeklyEmploymentHoursKPIService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;

@RunWith(MockitoJUnitRunner.class)
public class WeeklyHoursEmploymentKPIServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlannedHoursCalculationServiceTest.class);

    @Mock
    private UserIntegrationService userIntegrationService;

    @InjectMocks
    private WeeklyEmploymentHoursKPIService weeklyEmploymentHoursKPIService;

    @Mock
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    private List<Long> staffIds;
    private Long unitId;
    private DateTimeInterval dateTimeInterval;
    private KPICalculationRelatedInfo kpiCalculationRelatedInfo;
    private List<StaffKpiFilterDTO> staffKpiFilterDTOS;
    private FilterType filterType;

    static {
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
        System.setProperty("user.timezone", "UTC");
        DateTimeZone.setDefault(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    }

    @Before
    public void setUp(){
        this.staffIds=new ArrayList<>();
        this.staffIds.add(2364l);
        this.unitId =1172l;
        this.dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-02-01")),asDate(asLocalDate("2020-02-29")));
        this.kpiCalculationRelatedInfo = getKpiCalculationRelatedInfo();
        this.staffKpiFilterDTOS = getStaffKpiFilterDTOS();
        this.filterType = FilterType.EMPLOYMENT_SUB_TYPE;
    }


    private KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPICalculationRelatedInfo();
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
        staffKpiFilterDTO.setEmployment(ObjectMapperUtils.jsonStringToList(getEmployment(),EmploymentWithCtaDetailsDTO.class));
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
    public void testWeeklyHoursEmploymentKpiService(){
        double totalWeeklyHours = weeklyEmploymentHoursKPIService.getWeeklyHoursOfEmployment(staffIds.get(0),kpiCalculationRelatedInfo,dateTimeInterval.getStartLocalDate(),dateTimeInterval.getEndLocalDate());
        Assert.assertEquals(37.0d,totalWeeklyHours,37.0d);
    }



}
