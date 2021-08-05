package com.kairos.service;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.utils.KPIUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TimeBankOffKPIService implements KPIService{
    public double getCountAndHoursAndPercentageOfTODOSByActivityAndTimeType(Long staffId,DateTimeInterval dateTimeInterval,KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        int totalTodos=0;
        double todoStatusCount=0;
        Map<BigInteger,List<TodoDTO>> idTodoListMap  = getBigIntegerTodoListMap(staffId,kpiCalculationRelatedInfo);
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : idTodoListMap.entrySet()){
            List<TodoDTO> todoDTOList = new ArrayList<>();
            if(ObjectUtils.isNull(staffId)) {
                todoDTOList = getTodoDTOListIfStaffIsNotExist(kpiCalculationRelatedInfo, dateTimeInterval, entry);
            }
            List<TodoDTO> todoDTOS = ObjectUtils.isNotNull(staffId)?entry.getValue():todoDTOList;
            totalTodos +=todoDTOS.size();
            todoStatusCount += getActivityStatusCount(staffId,todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0),dateTimeInterval);
        }
        if(XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))&&totalTodos>0){
            return KPIUtils.getValueWithDecimalFormat((todoStatusCount * 100) / totalTodos);
        }else if(XAxisConfig.HOURS.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
          return getHoursOfTheTodos(staffId,kpiCalculationRelatedInfo,dateTimeInterval);

        }
        else {
            return todoStatusCount;
        }

    }
    public Map<BigInteger,List<TodoDTO>> getBigIntegerTodoListMap(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ACTIVITY_IDS);
        Map<BigInteger,List<TodoDTO>> bigIntegerTodoListMap = getBigIntegerListMap(staffId, kpiCalculationRelatedInfo, isActivityExist);
        if(ObjectUtils.isNotNull(bigIntegerTodoListMap)) {
            return bigIntegerTodoListMap;
        }else {
           return new HashMap<>();
        }
    }

    private Map<BigInteger, List<TodoDTO>> getBigIntegerListMap(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo, boolean isActivityExist) {
        Map<BigInteger, List<TodoDTO>> bigIntegerTodoListMap;
        if(ObjectUtils.isNotNull(staffId)){
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

        }
        return bigIntegerTodoListMap;
    }


    private List<TodoDTO> getTodoDTOListIfStaffIsNotExist(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Map.Entry<BigInteger, List<TodoDTO>> entry) {
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ACTIVITY_IDS);
        List<TodoDTO> activityTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        List<TodoDTO> timeTypeTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        return isActivityExist?activityTodoList:timeTypeTodoList;
    }

    private List<ShiftDTO> getShiftListIfStaffIsNotExist(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Map.Entry<BigInteger, List<ShiftDTO>> entry) {
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ACTIVITY_IDS)&&YAxisConfig.ACTIVITY.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().getOrDefault(FilterType.CALCULATION_BASED_ON,new ArrayList()).get(0));
        List<ShiftDTO> activityShiftList =kpiCalculationRelatedInfo.getShiftsByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndShiftListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        List<ShiftDTO> timeTypeShiftList =kpiCalculationRelatedInfo.getShiftsByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeIdAndShiftListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        return isActivityExist?activityShiftList:timeTypeShiftList;
    }


    public double getActivityStatusCount(Long staffId,List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo,XAxisConfig xAxisConfig,DateTimeInterval dateTimeInterval) {
        if(XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            return getStatusCountByPercentage(todoDTOS,kpiCalculationRelatedInfo);
        }
        else{
            return todoDTOS.size();
        }
    }

    public double getHoursOfTheTodos(Long staffId,KPICalculationRelatedInfo kpiCalculationRelatedInfo,DateTimeInterval dateTimeInterval){
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(FilterType.ACTIVITY_IDS)&& YAxisConfig.ACTIVITY.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.CALCULATION_BASED_ON).get(0));
        Map<BigInteger,List<ShiftDTO>> bigIntegerAndShiftMap =isActivityExist?kpiCalculationRelatedInfo.getStaffIdAndActivityIdAndShiftMap().get(staffId):kpiCalculationRelatedInfo.getStaffIdAndTimeTypeIdAndShiftMap().get(staffId);
        Map<BigInteger,List<ShiftDTO>> bigIntegerAndShiftListMap =isActivityExist?kpiCalculationRelatedInfo.getActivityIdAndShiftListMap():kpiCalculationRelatedInfo.getTimeTypeIdAndShiftListMap();
        Map<BigInteger,List<ShiftDTO>> filterIdAndShiftListMap = ObjectUtils.isNotNull(staffId)?bigIntegerAndShiftMap:bigIntegerAndShiftListMap;
        double shiftHours =0.0d;
        if(ObjectUtils.isNotNull(filterIdAndShiftListMap)) {
            for (Map.Entry<BigInteger, List<ShiftDTO>> entry : filterIdAndShiftListMap.entrySet()) {

                List<ShiftDTO> shiftList = ObjectUtils.isNotNull(staffId) ? entry.getValue() : getShiftListIfStaffIsNotExist(kpiCalculationRelatedInfo, dateTimeInterval, entry);
                for (ShiftDTO shift : shiftList) {
                    shiftHours += DateUtils.getMinutesBetweenDate(shift.getStartDate(), shift.getEndDate());
                }
            }
        }

        return shiftHours;
    }

    public double getStatusCountByPercentage(List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        double statusPercentage = 0;
        if(ObjectUtils.isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS))) {
            if (ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            } else if (ShiftStatus.DISAPPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            }else if(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(FilterType.ACTIVITY_STATUS).size() >1){
                statusPercentage =todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())||TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            }
        }else{
            statusPercentage =todoDTOS.size();
        }

        return statusPercentage;
    }

    @Override
    public <T> double get(Long staffId, DateTimeInterval dateTimeInterval, KPICalculationRelatedInfo kpiCalculationRelatedInfo, T t) {
        return getCountAndHoursAndPercentageOfTODOSByActivityAndTimeType(staffId,dateTimeInterval,kpiCalculationRelatedInfo);
    }
}
