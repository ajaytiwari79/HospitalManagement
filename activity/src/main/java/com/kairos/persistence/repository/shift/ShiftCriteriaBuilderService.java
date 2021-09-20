package com.kairos.persistence.repository.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.filter.RequiredDataForFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.RealTimeStatus;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.service.time_slot.TimeSlotSetService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.TimeTypeEnum.STOP_BRICK;

@Service
public class ShiftCriteriaBuilderService {

    public static final String DELETED = "deleted";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ACTIVITIES = "activities";
    public static final String TIME_TYPE = "timeType";
    public static final String DRAFT ="draft";
    private static final String ACTIVITY_STATUS = "activities.status";
    private static final String ACTIVITY_IDS = "activities.activityId";
    private static final String TIMETYPE_IDS = "activities.timeTypeId";
    private static final String PLANNED_TIME_IDS = "activities.plannedTimes.plannedTimeId";
    private static final String VALIDATED_BY_ROLES = "accessGroupRole";
    private static final String START_TIME = "shiftStartTime";
    private Set<FilterType> FILTER_WHICH_REQUIRED_DATA = newHashSet(TEAM,FUNCTIONS);

    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private TimeSlotSetService timeSlotSetService;


    public <T> void updateCriteria(Long unitId,Map<FilterType, Set<T>> filterTypeMap, Criteria criteria,RequiredDataForFilterDTO requiredDataForFilterDTO){
        if(isNull(requiredDataForFilterDTO)){
            boolean requiredDataFromUserService = filterTypeMap.keySet().stream().anyMatch(filterType -> FILTER_WHICH_REQUIRED_DATA.contains(filterType));
            requiredDataForFilterDTO = new RequiredDataForFilterDTO();
            if(requiredDataFromUserService) {
                requiredDataForFilterDTO = userIntegrationService.getRequiredDataForFilter(unitId, filterTypeMap);
            }
            requiredDataForFilterDTO.setTimeSlotDTOS(timeSlotSetService.getUnitTimeSlotByNames(unitId,(Set<String>) filterTypeMap.get(TIME_SLOT)));
        }
        updateTimeTypeCriteria(filterTypeMap,criteria,requiredDataForFilterDTO);
        updateFunctionCriteria(filterTypeMap,criteria,requiredDataForFilterDTO);
        updateValidatedByCriteria(filterTypeMap,criteria);
        updateActivityCriteria(filterTypeMap,criteria,requiredDataForFilterDTO);
        updatePlannerByCriteria(filterTypeMap,criteria);
        updateEscalationCriteria(filterTypeMap,criteria);
        updatePlannedTimeTypeCriteria(filterTypeMap,criteria);
        updatePhaseCriteria(filterTypeMap,criteria);
        updateActivityTimeCalculationType(filterTypeMap,criteria);
        updateActivityStatusCriteria(filterTypeMap,criteria);
        updateTimeSlotCriteria(filterTypeMap,criteria,requiredDataForFilterDTO);
        updateDataAfterPlanningPeriodPublishAndDeletedStopBrick(filterTypeMap,criteria);
    }

    private <T> void updateDataAfterPlanningPeriodPublishAndDeletedStopBrick(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria) {
        boolean addFalseCriteria = false;
        if(isValidFilter(filterTypeMap,UPDATED_DATA_AFTER_PLANNING_PERIOD_PUBLISH)){
            criteria.and("planningPeriodPublished").in(filterTypeMap.get(UPDATED_DATA_AFTER_PLANNING_PERIOD_PUBLISH));
            addFalseCriteria = true;
        }
        if(isValidFilter(filterTypeMap,STOPBRICK_DELETED_BY)){
            criteria.orOperator(Criteria.where(DELETED).is(true).and(ACTIVITIES+".secondLevelType").is(STOP_BRICK).and("deletedBy").in(filterTypeMap.get(STOPBRICK_DELETED_BY)),Criteria.where(DELETED).is(false));
            addFalseCriteria = true;
        }
        if(!addFalseCriteria) {
            criteria.and(DELETED).is(false);
        }
    }

    private <T> void updateActivityTimeCalculationType(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria) {
        if(isValidFilter(filterTypeMap,ACTIVITY_TIMECALCULATION_TYPE)){
            criteria.and(ACTIVITIES+".methodForCalculatingTime").in(filterTypeMap.get(ACTIVITY_TIMECALCULATION_TYPE));
        }
    }

    private <T> void updateTimeTypeCriteria(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO){
        Set<BigInteger> timeTypeIds = new HashSet<>();
        FilterType timeType = FilterType.TIME_TYPE;
        if(isValidFilter(filterTypeMap, timeType)) {
            timeTypeIds = new HashSet<>(getBigInteger(filterTypeMap.get(timeType)));
            //timeTypeIds = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), timeTypeIds).keySet();
        }
        if(isValidFilter(filterTypeMap, REAL_TIME_STATUS)){
            if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.SICK.toString())) {
                timeTypeIds.addAll(requiredDataForFilterDTO.getSickTimeTypeIds());
            }
        }
        if(isCollectionNotEmpty(timeTypeIds)){
            criteria.and(TIMETYPE_IDS).in(timeTypeIds);
        }
    }


    private <T> boolean isValidFilter(Map<FilterType, Set<T>> filterTypeMap, FilterType timeType) {
        return filterTypeMap.containsKey(timeType) && isCollectionNotEmpty(filterTypeMap.get(timeType));
    }

    private <T> void updateFunctionCriteria(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO){
        if(isValidFilter(filterTypeMap,FUNCTIONS)) {
            Criteria[] criterias = new Criteria[requiredDataForFilterDTO.getFunctionDates().size()];
            int i = 0;
            for (LocalDate functionDate : requiredDataForFilterDTO.getFunctionDates()) {
                criterias[i] = Criteria.where(START_DATE).gte(functionDate).and(END_DATE).lt(functionDate);
                i++;
            }
            criteria.orOperator(criterias);
        }
    }

    private <T> void updateValidatedByCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria) {
        if(isValidFilter(filterTypeMap,VALIDATED_BY)) {
            criteria.and(VALIDATED_BY_ROLES).in(filterTypeMap.get(FilterType.VALIDATED_BY).stream().map(v->v.toString()).collect(Collectors.toSet()));
        }
    }

    private <T> void updateActivityCriteria( Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO) {
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(isValidFilter(filterTypeMap,ABSENCE_ACTIVITY)){
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toList()));
        }
        if(isValidFilter(filterTypeMap,TEAM)){
            selectedActivityIds.addAll(requiredDataForFilterDTO.getTeamActivityIds());
        }
        if(isCollectionNotEmpty(selectedActivityIds)){
            criteria.and(ACTIVITY_IDS).in(selectedActivityIds);
        }
    }

    private <T> void updatePlannerByCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria) {
        if(isValidFilter(filterTypeMap,PLANNED_BY)){
            Set<AccessGroupRole> accessGroups = filterTypeMap.get(PLANNED_BY).stream().map(s -> AccessGroupRole.valueOf(s.toString())).collect(Collectors.toSet());
            criteria.and("createdBy.accessGroupRole").in(accessGroups);
        }
    }

    private <T> void updateEscalationCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(isValidFilter(filterTypeMap,ESCALATION_CAUSED_BY)) {
            criteria.and("shiftViolatedRules.escalationCausedBy").in(filterTypeMap.get(ESCALATION_CAUSED_BY)).and("shiftViolatedRules.escalationResolved").is(false);
        }
    }

    private <T> void updatePlannedTimeTypeCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(isValidFilter(filterTypeMap,PLANNED_TIME_TYPE)) {
            List<BigInteger> plannedTimeTypeIds = getBigInteger(filterTypeMap.get(PLANNED_TIME_TYPE));
            criteria.and(PLANNED_TIME_IDS).in(plannedTimeTypeIds);
        }
    }

    private <T> void updatePhaseCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(isValidFilter(filterTypeMap,PHASE)){
            criteria.and("phaseId").in(getBigInteger(filterTypeMap.get(PHASE)));
        }
    }

    public <T> void updateActivityStatusCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria) {
        if(isValidFilter(filterTypeMap,FilterType.ACTIVITY_STATUS)){
            criteria.and(ACTIVITY_STATUS).in(filterTypeMap.get(FilterType.ACTIVITY_STATUS));
        }
    }


    private <T> void updateTimeSlotCriteria(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO) {
        if(isValidFilter(filterTypeMap,TIME_SLOT)) {
            Criteria[] criterias = new Criteria[requiredDataForFilterDTO.getTimeSlotDTOS().size()];
            int i = 0;
            for (TimeSlotDTO timeSlotDTO : requiredDataForFilterDTO.getTimeSlotDTOS()) {
                Integer startTime = (timeSlotDTO.getStartHour() * 60 * 60) + (timeSlotDTO.getStartMinute() * 60);
                Integer endTime = (timeSlotDTO.getEndHour() * 60 * 60) + (timeSlotDTO.getEndMinute() * 60);
                criterias[i] =Criteria.where(START_TIME).gte(startTime).lt(endTime);
                i++;
            }
            criteria.orOperator(criterias);
        }
    }
}
