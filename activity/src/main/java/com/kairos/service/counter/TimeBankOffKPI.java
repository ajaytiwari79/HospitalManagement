package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.shift.ShiftService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.FilterType.*;

@Service
public class TimeBankOffKPI {
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private AbsencePlanningKPIService absencePlanningKPIService;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;

    public double getCountAndHoursAndPercentageOfTODOS(Long staffId,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        double todoStatusCount =0.0d;
        List<StaffKpiFilterDTO> staffKpiFilterDTOS = isNotNull(staffId) ? Arrays.asList(kpiCalculationRelatedInfo.getStaffIdAndStaffKpiFilterMap().getOrDefault(staffId, new StaffKpiFilterDTO())) : kpiCalculationRelatedInfo.getStaffKpiFilterDTOS();
        for(StaffKpiFilterDTO staffKpiFilterDTO :staffKpiFilterDTOS) {
           if (isNotNull(staffKpiFilterDTO.getId())) {
               kpiCalculationRelatedInfo.updateTodoDtosByStaffId(staffId);
           }
            if (isNotNull(staffKpiFilterDTO.getId())) {
                kpiCalculationRelatedInfo.getTimeTypeTodoList(staffId);
            }
           todoStatusCount += getTodoStatus(kpiCalculationRelatedInfo, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap(), kpiCalculationRelatedInfo.getTimeTypeTodoListMap());

       }
       return todoStatusCount;
    }

    public double getTodoStatus (KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,Map<BigInteger,List<TodoDTO>> activityIdTodoListMap,Map<BigInteger,List<TodoDTO>> timeTypeIdTodoListMap){
        Set<BigInteger> activityIds = isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) ? getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) : new HashSet<>();
        Set<BigInteger> timeTypeIds = isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE)) ? getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE)) : new HashSet<>();
        double todoStatusCount =0.0d;
        if(isCollectionNotEmpty(activityIds)&&activityIds.containsAll(activityIdTodoListMap.keySet())){
            for(Map.Entry<BigInteger,List<TodoDTO>> entry : activityIdTodoListMap.entrySet()){
                if(activityIds.contains(entry.getKey())) {
                   todoStatusCount += getActivityStatusCount(entry.getValue(),kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
                }
            }

        }else if(isCollectionNotEmpty(timeTypeIds) && timeTypeIds.containsAll(timeTypeIdTodoListMap.keySet())){
            for(Map.Entry<BigInteger,List<TodoDTO>> entry : timeTypeIdTodoListMap.entrySet()){
                if(timeTypeIds.contains(entry.getKey())) {
                    todoStatusCount += getActivityStatusCount(entry.getValue(),kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
                }
            }
        }
        else {
            todoStatusCount += getActivityStatusCount(kpiCalculationRelatedInfo.getTodoDTOS(),kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
        }

        return todoStatusCount;
    }

    public double getActivityStatusCount(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,XAxisConfig xAxisConfig) {
        int disapprove = 0;
        int approve = 0;
        for (TodoDTO todoDTO : todoDTOS) {
            switch (todoDTO.getStatus()) {
                case DISAPPROVE:
                    disapprove++;
                    break;
                case APPROVE:
                    approve++;
                    break;
                default:
                    break;
            }
        }
        boolean isActivityStatusIsExist =isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS));
        if(XAxisConfig.HOURS.equals(xAxisConfig)){
            return getHoursOfTheShifts(todoDTOS, kpiCalculationRelatedInfo);
        }
        else if(isActivityStatusIsExist && ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,approve);
        }
        else if(isActivityStatusIsExist && ShiftStatus.REJECT.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,disapprove);
        }else{
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,approve+disapprove);
        }

    }

    private double getHoursOfTheShifts(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0))&&kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            todoDTOS=todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList());
            return getHoursOfTheTodos(todoDTOS);
        }else if(ShiftStatus.REJECT.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            todoDTOS=todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList());
            return getHoursOfTheTodos(todoDTOS);
        }
        else {
            return getHoursOfTheTodos(todoDTOS);
        }
    }

    public double getHoursOfTheTodos(List<TodoDTO> todoDTOS){
        double shiftHours =0.0d;
        List<BigInteger> shiftIds =todoDTOS.stream().map(TodoDTO::getEntityId).collect(Collectors.toList());
        List<Shift> shiftList = shiftMongoRepository.findAllByIdInAndDeletedFalseOrderByStartDateAsc(shiftIds);
        for(Shift shift :shiftList){
            shiftHours += DateUtils.getMinutesBetweenDate(shift.getStartDate(),shift.getEndDate());
        }
        return shiftHours;
    }



}
