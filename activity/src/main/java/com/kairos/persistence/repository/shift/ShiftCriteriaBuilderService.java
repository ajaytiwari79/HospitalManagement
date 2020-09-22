package com.kairos.persistence.repository.shift;

import com.kairos.dto.activity.shift.SelfRosteringFilterDTO;
import com.kairos.dto.activity.shift.ShiftFilterDefaultData;
import com.kairos.dto.gdpr.FilterSelectionDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import com.kairos.dto.user.filter.RequiredDataForFilterDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.FilterType;
import com.kairos.enums.RealTimeStatus;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.activity.TimeTypeService;
import com.kairos.utils.counter.KPIUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;
import static com.kairos.enums.FilterType.*;
import static com.kairos.enums.FilterType.TIME_SLOT;
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
    private Set<FilterType> FILTER_WHICH_REQUIRED_DATA = newHashSet(TIME_SLOT, TEAM,FUNCTIONS);

    @Inject
    private TimeTypeService timeTypeService;
    @Inject
    private UserIntegrationService userIntegrationService;


    public <T> void updateCriteria(Long unitId,Map<FilterType, Set<T>> filterTypeMap, Criteria criteria,RequiredDataForFilterDTO requiredDataForFilterDTO){
        if(isNull(requiredDataForFilterDTO)){
            boolean requiredDataFromUserService = filterTypeMap.keySet().stream().anyMatch(filterType -> FILTER_WHICH_REQUIRED_DATA.contains(filterType));
            requiredDataForFilterDTO = new RequiredDataForFilterDTO();
            if(requiredDataFromUserService) {
                requiredDataForFilterDTO = userIntegrationService.getRequiredDataForFilter(unitId, filterTypeMap);
            }
        }
        updateTimeTypeCriteria(unitId,filterTypeMap,criteria,requiredDataForFilterDTO);
        updateFunctionCriteria(unitId,filterTypeMap,criteria,requiredDataForFilterDTO);
        updateValidatedByCriteria(filterTypeMap,criteria);
        updateActivityCriteria(unitId,filterTypeMap,criteria,requiredDataForFilterDTO);
        updatePlannerByCriteria(filterTypeMap,criteria);
        updateEscalationCriteria(filterTypeMap,criteria);
        updatePlannedTimeTypeCriteria(filterTypeMap,criteria);
        updatePhaseCriteria(filterTypeMap,criteria);
        updateActivityTimeCalculationType(filterTypeMap,criteria);
        updateActivityStatusCriteria(filterTypeMap,criteria);
        updateTimeSlotCriteria(unitId,filterTypeMap,criteria,requiredDataForFilterDTO);
    }

    private <T> void updateActivityTimeCalculationType(Map<FilterType, Set<T>> filterTypeMap, Criteria criteria) {
        if(filterTypeMap.containsKey(ACTIVITY_TIMECALCULATION_TYPE) && isCollectionNotEmpty(filterTypeMap.get(ACTIVITY_TIMECALCULATION_TYPE))){
            criteria.and(ACTIVITIES+".methodForCalculatingTime").in(filterTypeMap.get(ACTIVITY_TIMECALCULATION_TYPE));
        }
    }

    private <T> void updateTimeTypeCriteria(Long unitId, Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO){
        Set<BigInteger> timeTypeIds = null;
        if(filterTypeMap.containsKey(TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(TIME_TYPE))) {
            timeTypeIds = new HashSet<>(getBigInteger(filterTypeMap.get(TIME_TYPE)));
            timeTypeIds = timeTypeService.getAllTimeTypeWithItsLowerLevel(UserContext.getUserDetails().getCountryId(), timeTypeIds).keySet();
        }
        if(filterTypeMap.containsKey(REAL_TIME_STATUS) && isCollectionNotEmpty(filterTypeMap.get(REAL_TIME_STATUS))){
            if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.SICK.toString())) {
                timeTypeIds.addAll(requiredDataForFilterDTO.getSickTimeTypeIds());
            }if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.CURRENTLY_WORKING.toString())){

            }if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.UPCOMING.toString())){

            }if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.ON_BREAK.toString())){

            }if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.ON_LEAVE.toString())){

            }if(filterTypeMap.get(REAL_TIME_STATUS).contains(RealTimeStatus.RESTING.toString())){

            }
        }
        if(isCollectionNotEmpty(timeTypeIds)){
            criteria.and(TIMETYPE_IDS).in(timeTypeIds);
        }
    }

    private <T> void updateFunctionCriteria(Long unitId, Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO){
        if(filterTypeMap.containsKey(FilterType.FUNCTIONS) && isCollectionNotEmpty(filterTypeMap.get(FUNCTIONS))) {
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
        if(filterTypeMap.containsKey(FilterType.VALIDATED_BY) && isCollectionNotEmpty(filterTypeMap.get(VALIDATED_BY))) {
            criteria.and(VALIDATED_BY_ROLES).in(filterTypeMap.get(FilterType.VALIDATED_BY).stream().map(v->v.toString()).collect(Collectors.toSet()));
        }
    }

    private <T> void updateActivityCriteria(Long unitId, Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO) {
        List<BigInteger> selectedActivityIds = new ArrayList<>();
        if(filterTypeMap.containsKey(ABSENCE_ACTIVITY) && isCollectionNotEmpty(filterTypeMap.get(ABSENCE_ACTIVITY))) {
            selectedActivityIds.addAll(filterTypeMap.get(ABSENCE_ACTIVITY).stream().map(s -> new BigInteger(s.toString())).collect(Collectors.toList()));
        }
        if(filterTypeMap.containsKey(TEAM) && isCollectionNotEmpty(filterTypeMap.get(TEAM))){
            selectedActivityIds.addAll(requiredDataForFilterDTO.getTeamActivityIds());
        }
        if(isCollectionNotEmpty(selectedActivityIds)){
            criteria.and(ACTIVITY_IDS).in(selectedActivityIds);
        }
    }

    private <T> void updatePlannerByCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria) {
        if(filterTypeMap.containsKey(PLANNED_BY) && isCollectionNotEmpty(filterTypeMap.get(PLANNED_BY))){
            Set<AccessGroupRole> accessGroups = filterTypeMap.get(PLANNED_BY).stream().map(s -> AccessGroupRole.valueOf(s.toString())).collect(Collectors.toSet());
            criteria.and("createdBy.accessGroupRole").in(accessGroups);
        }
    }

    private <T> void updateEscalationCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(filterTypeMap.containsKey(ESCALATION_CAUSED_BY) && isCollectionNotEmpty(filterTypeMap.get(ESCALATION_CAUSED_BY))) {
            criteria.and("shiftViolatedRules.escalationCausedBy").in(filterTypeMap.get(ESCALATION_CAUSED_BY)).and("shiftViolatedRules.escalationResolved").is(false);
        }
    }

    private <T> void updatePlannedTimeTypeCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(filterTypeMap.containsKey(PLANNED_TIME_TYPE) && isCollectionNotEmpty(filterTypeMap.get(PLANNED_TIME_TYPE))) {
            List<BigInteger> plannedTimeTypeIds = getBigInteger(filterTypeMap.get(PLANNED_TIME_TYPE));
            criteria.and(PLANNED_TIME_IDS).in(plannedTimeTypeIds);
        }
    }

    private <T> void updatePhaseCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria){
        if(filterTypeMap.containsKey(PHASE) && isCollectionNotEmpty(filterTypeMap.get(PHASE))){
            criteria.and("phaseId").in(getBigInteger(filterTypeMap.get(PHASE)));
        }
    }

    public <T> void updateActivityStatusCriteria(Map<FilterType, Set<T>> filterTypeMap,Criteria criteria) {
        if(filterTypeMap.containsKey(FilterType.ACTIVITY_STATUS) && isCollectionNotEmpty(filterTypeMap.get(FilterType.ACTIVITY_STATUS))){
            criteria.and(ACTIVITY_STATUS).in(filterTypeMap.get(FilterType.ACTIVITY_STATUS));
        }
    }

    private <T> void updateTimeSlotCriteria(Long unitId, Map<FilterType, Set<T>> filterTypeMap, Criteria criteria, RequiredDataForFilterDTO requiredDataForFilterDTO) {
        if(filterTypeMap.containsKey(TIME_SLOT) && isCollectionNotEmpty(filterTypeMap.get(TIME_SLOT))) {
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