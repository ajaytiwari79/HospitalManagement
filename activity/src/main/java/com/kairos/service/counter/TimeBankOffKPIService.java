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
        double todoStatusCount =0.0d;
           if (isNotNull(staffId)&&YAxisConfig.ACTIVITY.equals(kpiCalculationRelatedInfo.getYAxisConfigs().get(0))) {
               kpiCalculationRelatedInfo.updateTodoDtosByStaffId(staffId);
           }
            if (isNotNull(staffId)&&YAxisConfig.TIME_TYPE.equals(kpiCalculationRelatedInfo.getYAxisConfigs().get(0))) {
                kpiCalculationRelatedInfo.getUpdateTimeTypeTodoDTOSMapByStaffId(staffId);
            }
           todoStatusCount =todoStatusCount + getTodoStatus(staffId,kpiCalculationRelatedInfo,dateTimeInterval);

           return todoStatusCount;
    }

    public double getTodoStatus (Long staffId,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,DateTimeInterval dateTimeInterval){
        double todoStatusCount =0.0d;
        todoStatusCount = getTodoStatusCount(staffId, kpiCalculationRelatedInfo, dateTimeInterval, todoStatusCount);

        return todoStatusCount;
    }

    private double getTodoStatusCount(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, double todoStatusCount) {
        todoStatusCount = getTodoStatusCountByTimeTypeAndByActivity(staffId, kpiCalculationRelatedInfo, dateTimeInterval, todoStatusCount);
        return todoStatusCount;
    }

    private double getTodoStatusCountByTimeTypeAndByActivity(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, double todoStatusCount) {
        int totalTodos=0;
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS);

        Map<BigInteger,List<TodoDTO>> idTodoListMap  = isActivityExist ? kpiCalculationRelatedInfo.getActivityIdAndTodoListMap() : kpiCalculationRelatedInfo.getTimeTypeTodoListMap();
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : idTodoListMap.entrySet()){
                List<TodoDTO> todoDTOList =isActivityExist?kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().get(entry.getKey())):kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeTodoListMap().get(entry.getKey()));
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
        double statusPercentage=0.0d;
        if(ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0))&&kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2) {
             statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
        }
        else if(ShiftStatus.DISAPPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0))&&kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2) {
             statusPercentage = todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList()).size();
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
