package com.kairos.service.counter;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.counter.chart.ClusteredBarChartKpiDataUnit;
import com.kairos.dto.activity.counter.enums.XAxisConfig;
import com.kairos.dto.activity.kpi.StaffKpiFilterDTO;
import com.kairos.dto.activity.time_bank.EmploymentWithCtaDetailsDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.util.*;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.enums.FilterType.*;

@Service
public class TimeBankOffKPI {
    @Inject
    private KPIBuilderCalculationService kpiBuilderCalculationService;
    @Inject
    private AbsencePlanningKPIService absencePlanningKPIService;

    public double getCountAndHoursAndPercentageOfTODOS(Long staffId,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
       if(isNotNull(staffId)){
           kpiCalculationRelatedInfo.updateTodoDtosByStaffId(staffId);
       }
       return getTodoStatus(kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getActivityIdAndTodoListMap(),kpiCalculationRelatedInfo.getTimeTypeTodoListMap());
    }

    public double getTodoStatus (KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,Map<BigInteger,List<TodoDTO>> activityIdTodoListMap,Map<BigInteger,List<TodoDTO>> timeTypeIdTodoListMap){
        Set<BigInteger> activityIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) : new HashSet<>();
        Set<BigInteger> timeTypeIds = kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(TIME_TYPE) ? KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE)) : new HashSet<>();
        double todoStatusCount =0.0d;
        if(activityIdTodoListMap.keySet().contains(activityIds)&&timeTypeIdTodoListMap.keySet().contains(timeTypeIds)){
            for(Map.Entry<BigInteger,List<TodoDTO>> entry : activityIdTodoListMap.entrySet()){
                if(activityIds.contains(entry.getKey())) {
                   todoStatusCount= + getActivityStatusCount(entry.getValue(),kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
                }
            }

        }
        else if(kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(DAYS_OF_WEEK)){
            todoStatusCount =filterTODOSByDayOfTheWeek(kpiCalculationRelatedInfo,activityIdTodoListMap);
        }else {
            todoStatusCount =getActivityStatusCount(kpiCalculationRelatedInfo.getTodoDTOS(),kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
        }

        return todoStatusCount;
    }

    public double filterTODOSByDayOfTheWeek(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,Map<BigInteger,List<TodoDTO>> activityIdTodoListMap){
        double todoStatus =0.0d;
        for(Map.Entry<BigInteger,List<TodoDTO>> entry : activityIdTodoListMap.entrySet()){
            List<TodoDTO> todoDTOS =getTodoDTOSByRequestedDay(entry.getValue(),kpiCalculationRelatedInfo);
            todoStatus = + getActivityStatusCount(todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
        }
        return todoStatus;
    }

    public List<TodoDTO> getTodoDTOSByRequestedDay(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo){
        Set<BigInteger> daysOfWeeksIds =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(DAYS_OF_WEEK)?KPIUtils.getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(TIME_TYPE)) : new HashSet<>();
        List<TodoDTO> todoDTOList =new ArrayList<>();
        for(TodoDTO todoDTO :todoDTOS){
            if(daysOfWeeksIds.contains(todoDTO.getRequestedOn().getDay())){
              todoDTOList.add(todoDTO);
            }
        }
        return todoDTOList;
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
        if(ShiftStatus.APPROVE.equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,approve);
        }
        else if(ShiftStatus.REJECT.equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,disapprove);
        }else{
            return absencePlanningKPIService.getValueOfTodo(todoDTOS,xAxisConfig,approve+disapprove);
        }

    }


}
