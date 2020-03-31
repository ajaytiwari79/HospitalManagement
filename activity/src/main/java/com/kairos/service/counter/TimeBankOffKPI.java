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
public class TimeBankOffKPI {
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
           if (isNotNull(staffId)&&kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(ACTIVITY_IDS)) {
               kpiCalculationRelatedInfo.updateTodoDtosByStaffId(staffId);
           }
            if (isNotNull(staffId)&&kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(TIME_TYPE)) {
                kpiCalculationRelatedInfo.getUpdateTimeTypeTodoDTOSMapByStaffId(staffId);
            }
           todoStatusCount =todoStatusCount + getTodoStatus(staffId,kpiCalculationRelatedInfo,dateTimeInterval);

           return todoStatusCount;
    }

    public double getTodoStatus (Long staffId,KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,DateTimeInterval dateTimeInterval){
        Set<BigInteger> activityIds = isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) ? getBigIntegerSet(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_IDS)) : new HashSet<>();
        double todoStatusCount =0.0d;
        todoStatusCount = getTodoStatusCount(staffId, kpiCalculationRelatedInfo, dateTimeInterval, activityIds, todoStatusCount);

        return todoStatusCount;
    }

    private double getTodoStatusCount(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Set<BigInteger> activityIds, double todoStatusCount) {
        if(isCollectionNotEmpty(activityIds)&&activityIds.containsAll(kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().keySet())){
            todoStatusCount = getTodoStatusCountByActivity(staffId, kpiCalculationRelatedInfo, dateTimeInterval, activityIds, todoStatusCount);

        }else if(kpiCalculationRelatedInfo.getTimeTypeTodoListMap().keySet().containsAll(activityIds)){
            todoStatusCount = getTodoStatusCountByTimeType(staffId, kpiCalculationRelatedInfo, dateTimeInterval, todoStatusCount);
        }
        else {
            List<TodoDTO> todoDTOS =isNotNull(staffId)?kpiCalculationRelatedInfo.getStaffIdAndTodoMap().get(staffId):kpiCalculationRelatedInfo.getTodoDTOS();
            todoStatusCount += getActivityStatusCount(todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
        }
        return todoStatusCount;
    }

    private double getTodoStatusCountByTimeType(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, double todoStatusCount) {
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : kpiCalculationRelatedInfo.getTimeTypeTodoListMap().entrySet()){
                List<TodoDTO> todoDTOS =isNotNull(staffId)?entry.getValue():kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getTimeTypeTodoListMap().get(entry.getKey()));
                todoStatusCount += getActivityStatusCount(todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
            }
        if(XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            return updateStatusCountByPercentage(kpiCalculationRelatedInfo,todoStatusCount);
        }else {
            return todoStatusCount;
        }
    }

    private double getTodoStatusCountByActivity(Long staffId, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo, DateTimeInterval dateTimeInterval, Set<BigInteger> activityIds, double todoStatusCount) {
        for(Map.Entry<BigInteger, List<TodoDTO>> entry : kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().entrySet()){
            if(activityIds.contains(entry.getKey())) {
                List<TodoDTO> todoDTOS =isNotNull(staffId)?entry.getValue():kpiCalculationRelatedInfo.getTodosByInterval(dateTimeInterval, kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().get(entry.getKey()));
               todoStatusCount += getActivityStatusCount(todoDTOS,kpiCalculationRelatedInfo,kpiCalculationRelatedInfo.getXAxisConfigs().get(0));
            }
        }
        if(XAxisConfig.PERCENTAGE.equals(kpiCalculationRelatedInfo.getXAxisConfigs().get(0))){
            return updateStatusCountByPercentage(kpiCalculationRelatedInfo,todoStatusCount);
        }else {
            return todoStatusCount;
        }
    }

    public double getActivityStatusCount(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,XAxisConfig xAxisConfig) {
        int disapprove = 0;
        int approve = 0;
        if(isCollectionNotEmpty(todoDTOS)) {
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
        }
        boolean isActivityStatusIsExist =isCollectionNotEmpty(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS));
        if(XAxisConfig.HOURS.equals(xAxisConfig)){
            return getHoursOfTheShifts(todoDTOS, kpiCalculationRelatedInfo);
        }
        else if(isActivityStatusIsExist && ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return approve;
        }
        else if(isActivityStatusIsExist && ShiftStatus.REJECT.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size()<2){
            return disapprove;
        }else{
            return approve+disapprove;
        }

    }

    private double getHoursOfTheShifts(List<TodoDTO> todoDTOS, KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo) {
        if(isNotNull(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS))) {
            if (ShiftStatus.APPROVE.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                todoDTOS = todoDTOS.stream().filter(todoDTO -> TodoStatus.APPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList());
                return getHoursOfTheTodos(todoDTOS);
            } else if (ShiftStatus.REJECT.name().equals(kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).get(0)) && kpiCalculationRelatedInfo.getFilterBasedCriteria().get(ACTIVITY_STATUS).size() < 2) {
                todoDTOS = todoDTOS.stream().filter(todoDTO -> TodoStatus.DISAPPROVE.equals(todoDTO.getStatus())).collect(Collectors.toList());
                return getHoursOfTheTodos(todoDTOS);
            }else {
                return getHoursOfTheTodos(todoDTOS);
            }
        }
        else {
            return getHoursOfTheTodos(todoDTOS);
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

    public double updateStatusCountByPercentage(KPIBuilderCalculationService.KPICalculationRelatedInfo kpiCalculationRelatedInfo,double statusCount){
        int totalTods=0;
        boolean isActivityExist =kpiCalculationRelatedInfo.getFilterBasedCriteria().containsKey(YAxisConfig.ACTIVITY);
            for(List<TodoDTO> todoDTOS :isActivityExist?kpiCalculationRelatedInfo.getActivityIdAndTodoListMap().values():kpiCalculationRelatedInfo.getTimeTypeTodoListMap().values()){
                totalTods +=todoDTOS.size();
            }
        double statusPercentage = getValueWithDecimalFormat((statusCount * 100) / totalTods);
        return statusPercentage;
    }



}
