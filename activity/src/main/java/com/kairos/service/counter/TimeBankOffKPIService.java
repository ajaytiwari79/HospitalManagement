package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.kpi.YAxisConfig;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.shift.Shift;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.HOURS;
import static com.kairos.dto.activity.counter.enums.XAxisConfig.PERCENTAGE;
import static com.kairos.enums.FilterType.*;
import static com.kairos.utils.counter.KPIUtils.getValueWithDecimalFormat;

@Service
public class TimeBankOffKPIService implements KPIService{
    public double getCountAndHoursAndPercentageOfTODOSByActivityAndTimeType(Long staffId,DateTimeInterval dateTimeInterval,KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        int totalTodos=0;
        double todoStatusCount=0;
        Map<BigInteger,List<TodoDTO>> idTodoListMap  = getBigIntegerTodoListMap(staffId,kpiCalculationRelatedInfo);
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : idTodoListMap.entrySet()){
            List<TodoDTO> todoDTOList = new ArrayList<>();
            if(isNull(staffId)) {
                todoDTOList = getTodoDTOListIfStaffIsNotExist(kpiCalculationRelatedInfo, dateTimeInterval, entry);
            }
            List<TodoDTO> todoDTOS =isNotNull(staffId)?entry.getValue():todoDTOList;
            totalTodos +=todoDTOS.size();
            todoStatusCount += getActivityStatusCount(staffId,todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0),dateTimeInterval);
        }
        if(PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))&&totalTodos>0){
            return getValueWithDecimalFormat((todoStatusCount * 100) / totalTodos);
        }else if(HOURS.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
          return getHoursOfTheTodos(staffId,kpiCalculationRelatedInfo,dateTimeInterval);

        }
        else {
            return todoStatusCount;
        }

    }
    public Map<BigInteger,List<TodoDTO>> getBigIntegerTodoListMap(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS);
        Map<BigInteger,List<TodoDTO>> bigIntegerTodoListMap = getBigIntegerListMap(staffId, kpiCalculationRelatedInfo, isActivityExist);
        if(isNotNull(bigIntegerTodoListMap)) {
            return bigIntegerTodoListMap;
        }else {
           return new HashMap<>();
        }
    }

    private Map<BigInteger, List<TodoDTO>> getBigIntegerListMap(Long staffId, KPICalculationRelatedInfo kpiCalculationRelatedInfo, boolean isActivityExist) {
        Map<BigInteger, List<TodoDTO>> bigIntegerTodoListMap;
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

        }
        return bigIntegerTodoListMap;
    }


    private List<TodoDTO> getTodoDTOListIfStaffIsNotExist(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Map.Entry<BigInteger, List<TodoDTO>> entry) {
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS);
        List<TodoDTO> activityTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        List<TodoDTO> timeTypeTodoList =kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeTodoListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        return isActivityExist?activityTodoList:timeTypeTodoList;
    }

    private List<Shift> getShiftListIfStaffIsNotExist(KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Map.Entry<BigInteger, List<Shift>> entry) {
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS)&&YAxisConfig.ACTIVITY.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().getOrDefault(CALCULATION_BASED_ON,new ArrayList()).get(0));
        List<Shift> activityShiftList =kpiCalculationRelatedInfo.getShiftsByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndShiftListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        List<Shift> timeTypeShiftList =kpiCalculationRelatedInfo.getShiftsByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeIdAndShiftListMap().getOrDefault(entry.getKey(),new ArrayList<>()));
        return isActivityExist?activityShiftList:timeTypeShiftList;
    }


    public double getActivityStatusCount(Long staffId,List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo,XAxisConfig xAxisConfig,DateTimeInterval dateTimeInterval) {
        if(PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            return getStatusCountByPercentage(todoDTOS,kpiCalculationRelatedInfo);
        }
        else{
            return todoDTOS.size();
        }
    }

    public double getHoursOfTheTodos(Long staffId,KPICalculationRelatedInfo kpiCalculationRelatedInfo,DateTimeInterval dateTimeInterval){
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS)&& YAxisConfig.ACTIVITY.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(CALCULATION_BASED_ON).get(0));
        Map<BigInteger,List<Shift>> bigIntegerAndShiftMap =isActivityExist?kpiCalculationRelatedInfo.getStaffIdAndActivityIdAndShiftMap().get(staffId):kpiCalculationRelatedInfo.getStaffIdAndTimeTypeIdAndShiftMap().get(staffId);
        Map<BigInteger,List<Shift>> bigIntegerAndShiftListMap =isActivityExist?kpiCalculationRelatedInfo.getActivityIdAndShiftListMap():kpiCalculationRelatedInfo.getTimeTypeIdAndShiftListMap();
        Map<BigInteger,List<Shift>> filterIdAndShiftListMap =isNotNull(staffId)?bigIntegerAndShiftMap:bigIntegerAndShiftListMap;
        double shiftHours =0.0d;
        if(isNotNull(filterIdAndShiftListMap)) {
            for (Map.Entry<BigInteger, List<Shift>> entry : filterIdAndShiftListMap.entrySet()) {

                List<Shift> shiftList = isNotNull(staffId) ? entry.getValue() : getShiftListIfStaffIsNotExist(kpiCalculationRelatedInfo, dateTimeInterval, entry);
                for (Shift shift : shiftList) {
                    shiftHours += DateUtils.getMinutesBetweenDate(shift.getStartDate(), shift.getEndDate());
                }
            }
        }

        return shiftHours;
    }

    public double getStatusCountByPercentage(List<TodoDTO> todoDTOS, KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        double statusPercentage = 0;
        if(isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS))) {
            if (ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            } else if (ShiftStatus.DISAPPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
            }else if(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() >1){
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
