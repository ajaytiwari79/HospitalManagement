package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.enums.FilterType.*;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class TimeBankOffKPIService implements KPIService{
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private AbsencePlanningKPIService absencePlanningKPIService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private TimeTypeService timeTypeService;

    public double getCountAndHoursAndPercentageOfTODOS(Long staffId,DateTimeInterval dateTimeInterval,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
           double todoStatusCount = getTodoStatusCountByTimeTypeAndByActivity(staffId,kpiCalculationRelatedInfo,dateTimeInterval);
           return todoStatusCount;
    }
    public Map<BigInteger,List<TodoDTO>> getBigIntegerTodoListMap(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS);
        Map<BigInteger,List<TodoDTO>> bigIntegerTodoListMap;
       if(isNotNull(staffId)){
           if(isActivityExist){
               bigIntegerTodoListMap=kpiCalculationRelatedInfo.getStaffIdAndActivityTodoListMap().get(staffId);
           }else {
               bigIntegerTodoListMap=kpiCalculationRelatedInfo.getStaffIdAndTimeTypeTodoListMap().get(staffId);
           }
       }else {
           if(isActivityExist){
               bigIntegerTodoListMap=kpiCalculationRelatedInfo.getActivityIdAndTodoListMap();
           }else {
               bigIntegerTodoListMap=kpiCalculationRelatedInfo.getTimeTypeTodoListMap();
           }

       }if(isNotNull(bigIntegerTodoListMap)) {
            return bigIntegerTodoListMap;
        }else {
           return new HashMap<>();
        }
    }

    private double getTodoStatusCountByTimeTypeAndByActivity(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval) {
        int totalTodos=0;
        double todoStatusCount=0;
        Map<BigInteger,List<TodoDTO>> idTodoListMap  = getBigIntegerTodoListMap(staffId,kpiCalculationRelatedInfo);
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : idTodoListMap.entrySet()){
            List<TodoDTO> todoDTOList = new ArrayList<>();
            if(isNull(staffId)) {
                todoDTOList = getTodoDTOListIfStaffIsNotExist(staffId, kpiCalculationRelatedInfo, dateTimeInterval, entry);
                }
                List<TodoDTO> todoDTOS =isNotNull(staffId)?entry.getValue():todoDTOList;
                totalTodos +=entry.getValue().size();
                todoStatusCount += getActivityStatusCount(todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
            }
        if(PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))&&totalTodos>0){
            return getValueWithDecimalFormat((double)(todoStatusCount * 100) / totalTodos);
        }else {
            return todoStatusCount;
        }
    }

    private List<TodoDTO> getTodoDTOListIfStaffIsNotExist(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Map.Entry<BigInteger, List<TodoDTO>> entry) {
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS);
        List<TodoDTO> activityTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        List<TodoDTO> timeTypeTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        return isActivityExist?activityTodoList:timeTypeTodoList;
    }


    public double getActivityStatusCount(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,XAxisConfig xAxisConfig) {
        if(XAxisConfig.HOURS.equals(xAxisConfig)){
            return getHoursOfTheTodos(todoDTOS);
        }else if(PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            return getStatusCountByPercentage(todoDTOS,kpiCalculationRelatedInfo);
        }
        else{
                return todoDTOS.size();
        }
    }

    public double getHoursOfTheTodos(List<TodoDTO> todoDTOS){
        double shiftHours =0.0d;
        List<BigInteger> shiftIds = new ArrayList<>();
        if(isCollectionNotEmpty(todoDTOS)) {
            shiftIds = todoDTOS.stream().map(TodoDTO::getEntityId).collect(Collectors.toList());
        }
        List<Shift> shiftList = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftIds);
        for(Shift shift :shiftList){
            shiftHours += DateUtils.getMinutesBetweenDate(shift.getStartDate(),shift.getEndDate());
        }
        return shiftHours;
    }

    public double getStatusCountByPercentage(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        double statusPercentage = 0;
        if(isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS))) {
            if (ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            } else if (ShiftStatus.DISAPPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            }
        }else{
            statusPercentage =todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())||TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
        }

        return statusPercentage;
    }


    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getCountAndHoursAndPercentageOfTODOS(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
    }
}
