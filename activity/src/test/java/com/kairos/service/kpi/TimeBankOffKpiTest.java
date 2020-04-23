package com.kairos.service.kpi;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.CalculationType;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoSubtype;
import com.kairos.enums.todo.TodoType;
import com.kairos.service.counter.KPIBuilderCalculationService;
import com.kairos.service.counter.PayLevelKPIService;
import com.kairos.service.counter.TimeBankOffKPIService;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.DateUtils.asLocalDate;
import static com.kairos.enums.kpi.CalculationType.STAFF_SKILLS_COUNT;
import static com.kairos.enums.kpi.CalculationType.TODO_STATUS;

@RunWith(MockitoJUnitRunner.class)
public class TimeBankOffKpiTest {

    @InjectMocks
    private TimeBankOffKPIService timeBankOffKPIService;
    @Mock
    private KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelated;

    @Mock
    private KPIBuilderCalculationService kpiBuilderCalculationService;

    private List<Long> staffIds;
    private List<TodoDTO> todoDTOS;
    private Long unitId;
    private Set<BigInteger> activityIds;
    private DateTimeInterval dateTimeInterval;
    private Map<Long,List<TodoDTO>> staffIdTodoListMap;
    private Map<BigInteger,List<TodoDTO>> activityIdTodoListMap;
    private Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdActivityIdTodoMap;
    private List<XAxisConfig> xAxisConfigs;
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
        staffIds.add(2536l);
        staffIds.add(2564l);
        staffIds.add(2465l);
        staffIds.add(2566l);
        unitId =2403l;
        activityIds = new HashSet<>();
        activityIds.add(new BigInteger("1004"));
        activityIds.add(new BigInteger("1005"));
        activityIds.add(new BigInteger("1006"));
        activityIds.add(new BigInteger("1007"));
        xAxisConfigs=ObjectUtils.newArrayList(XAxisConfig.COUNT);
        dateTimeInterval = new DateTimeInterval(asDate(asLocalDate("2020-04-01")),asDate(asLocalDate("2020-04-30")));
        filterType =FilterType.EMPLOYMENT_SUB_TYPE;
        todoDTOS =getTodoDTOS();
        staffIdTodoListMap=getstaffIdTodoListMap();
        activityIdTodoListMap=getActivityIdTodoMap();
        staffIdActivityIdTodoMap=getStaffIdActivityIdTodoMap();
        kpiCalculationRelatedInfo=getKpiCalculationRelatedInfo();

    }

    public Map<BigInteger,List<TodoDTO>> getActivityIdTodoMap(){
        Map<BigInteger,List<TodoDTO>> activityIdTodoListMap=todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
        return activityIdTodoListMap;
    }

    public Map<Long,List<TodoDTO>> getstaffIdTodoListMap(){
        Map<Long,List<TodoDTO>> activityIdTodoListMap=todoDTOS.stream().collect(Collectors.groupingBy(TodoDTO::getStaffId, Collectors.toList()));
        return activityIdTodoListMap;
    }

    public Map<Long,Map<BigInteger,List<TodoDTO>>> getStaffIdActivityIdTodoMap(){
        Map<Long,Map<BigInteger,List<TodoDTO>>> staffIdActivityIdTodoMap = new HashMap<>();
        for (Map.Entry<Long, List<TodoDTO>> entry : staffIdTodoListMap.entrySet()) {
            Map<BigInteger,List<TodoDTO>> bigIntegerListMap = entry.getValue().stream().collect(Collectors.groupingBy(TodoDTO::getSubEntityId, Collectors.toList()));
            staffIdActivityIdTodoMap.put(entry.getKey(),bigIntegerListMap);
        }
        return staffIdActivityIdTodoMap;
    }

    private KPIBuilderCalculationService.KPICalculationRelatedInfo getKpiCalculationRelatedInfo(){
        KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo = new KPIBuilderCalculationService().new KPICalculationRelatedInfo();
        kpiCalculationRelatedInfo.setCalculationTypes(Arrays.asList(STAFF_SKILLS_COUNT));
        kpiCalculationRelatedInfo.setUnitId(unitId);
        kpiCalculationRelatedInfo.setStaffIds(staffIds);
        kpiCalculationRelatedInfo.setActivityIds(activityIds);
        kpiCalculationRelatedInfo.setDateTimeIntervals(Arrays.asList(dateTimeInterval));
        kpiCalculationRelatedInfo.setFilterBasedCriteria(getFilterBasedCrieteia());
        kpiCalculationRelatedInfo.setActivityIdAndTodoListMap(activityIdTodoListMap);
        kpiCalculationRelatedInfo.setStaffIdAndTodoMap(staffIdTodoListMap);
        kpiCalculationRelatedInfo.setStaffIdAndActivityTodoListMap(staffIdActivityIdTodoMap);
        kpiCalculationRelatedInfo.setXAxisConfigs(xAxisConfigs);
        return kpiCalculationRelatedInfo;
    }


    private List<TodoDTO> getTodoDTOS(){
        List<TodoDTO> todoDTOS = new ArrayList<>();
        todoDTOS.add(new TodoDTO(new BigInteger("1"), TodoType.APPROVAL_REQUIRED, TodoSubtype.ABSENCE_WITH_TIME,new BigInteger("8401"),new BigInteger("1004"), TodoStatus.APPROVE, LocalDate.now().minusDays(3),null,null,null,2563l,null,2403l,null,null,null,null,null,null,null));
        todoDTOS.add(new TodoDTO(new BigInteger("2"), TodoType.APPROVAL_REQUIRED, TodoSubtype.ABSENCE_WITH_TIME,new BigInteger("8402"),new BigInteger("1005"), TodoStatus.DISAPPROVE, LocalDate.now().minusDays(7),null,null,null,2564l,null,2403l,null,null,null,null,null,null,null));
        todoDTOS.add(new TodoDTO(new BigInteger("3"), TodoType.APPROVAL_REQUIRED, TodoSubtype.ABSENCE_WITH_TIME,new BigInteger("8403"),new BigInteger("1006"), TodoStatus.APPROVE, LocalDate.now().minusDays(5),null,null,null,2565l,null,2403l,null,null,null,null,null,null,null));
        todoDTOS.add(new TodoDTO(new BigInteger("4"), TodoType.APPROVAL_REQUIRED, TodoSubtype.ABSENCE_WITH_TIME,new BigInteger("8404"),new BigInteger("1007"), TodoStatus.APPROVE, LocalDate.now().minusDays(20),null,null,null,2566l,null,2403l,null,null,null,null,null,null,null));
        return todoDTOS;
    }

    private Map<FilterType,List> getFilterBasedCrieteia(){
        Map<FilterType,List> filterTypeListMap = new HashMap<>();
        filterTypeListMap.put(FilterType.CALCULATION_TYPE,ObjectUtils.newArrayList(TODO_STATUS));
        filterTypeListMap.put(FilterType.CALCULATION_BASED_ON,ObjectUtils.newArrayList(YAxisConfig.ACTIVITY));
        filterTypeListMap.put(FilterType.CALCULATION_UNIT,ObjectUtils.newArrayList(XAxisConfig.COUNT));
        filterTypeListMap.put(FilterType.ACTIVITY_STATUS,ObjectUtils.newArrayList(TodoStatus.APPROVE));
        filterTypeListMap.put(FilterType.ACTIVITY_IDS,ObjectUtils.newArrayList(1005,1006));
        return filterTypeListMap;
    }



    @Test
    public void testTimeBankOffKPIService(){
        double count = timeBankOffKPIService.getCountAndHoursAndPercentageOfTODOSByActivityAndTimeType(staffIds.get(0),dateTimeInterval,kpiCalculationRelatedInfo);
        Assert.assertEquals(2.0,count,2.0d);
    }




}
