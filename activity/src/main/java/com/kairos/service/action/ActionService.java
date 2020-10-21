package com.kairos.service.action;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithViolatedInfoDTO;
import com.kairos.enums.ActionType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftActionType;
import com.kairos.persistence.model.action.Action;
import com.kairos.persistence.model.action.ActionInfo;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.action.ActionInfoRepository;
import com.kairos.persistence.repository.action.ActionRepository;
import com.kairos.service.activity.ActivityService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.wrapper.action.ActionDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getEndOfDay;
import static com.kairos.commons.utils.DateUtils.getStartOfDay;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.ACTION;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;

/**
 * Created By G.P.Ranjan on 2/4/20
 **/
@Service
public class ActionService {
    private static final String AVAILABILITY = "AVAILABILITY";
    private static final String UNAVAILABILITY = "UNAVAILABILITY";
    private static final String STOP_BRICK = "STOP_BRICK";
    private static final String BEFORE = "BEFORE";
    private static final String AFTER = "AFTER";
    private static final String DELETE = "DELETE";
    private static final String UPDATE = "UPDATE";
    private static final String AVAILABILITY_NAME = "AVAILABILITY_NAME";
    private static final String UNAVAILABILITY_NAME = "UNAVAILABILITY_NAME";
    private static final String STOP_BRICK_NAME = "STOP_BRICK_NAME";
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActionRepository actionRepository;
    @Inject
    private ActionInfoRepository actionInfoRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ActivityService activityService;

    public ActionDTO saveAction(Long unitId, ActionDTO actionDTO) {
        actionDTO.setUnitId(unitId);
        Action action = ObjectMapperUtils.copyPropertiesByMapper(actionDTO, Action.class);
        actionRepository.save(action);
        actionDTO.setId(action.getId());
        return actionDTO;
    }

    public ActionDTO getAction(BigInteger actionId){
        Action action = actionRepository.findById(actionId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_DATANOTFOUND,ACTION,actionId)));
        return ObjectMapperUtils.copyPropertiesByMapper(action, ActionDTO.class);
    }

    public List<ActionDTO> getAllActionByUnitId(Long unitId){
        return actionRepository.getAllByUnitId(unitId);
    }

    public ActionDTO updateAction(BigInteger actionId, ActionDTO actionDTO) {
        Action action = actionRepository.findById(actionId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_DATANOTFOUND,ACTION,actionId)));
        action.setName(actionDTO.getName());
        action.setDescription(actionDTO.getDescription());
        actionRepository.save(action);
        actionDTO.setId(actionId);
        return actionDTO;
    }

    public List<Action> createDefaultAction(Long unitId) {
        List<Action> actions = new ArrayList<>();
        for (ActionType value : ActionType.values()) {
            actions.add(new Action(value, unitId));
        }
        return actionRepository.saveAll(actions);
    }

    public Map<String,Object> getCountBeforeAfterDate(Long unitId, Long staffId, Date shiftDate) {
        Map<String,Integer> beforeMap = new HashMap<String,Integer>(){{put(AVAILABILITY,0);put(UNAVAILABILITY,0);put(STOP_BRICK,0);}};
        Map<String,Integer> afterMap = new HashMap<>(beforeMap);
        List<Shift> shifts = shiftService.findShiftBetweenDurationByStaffId(staffId,getStartOfDay(shiftDate),getEndOfDay(shiftDate));
        shifts.forEach(shift -> {
            if(!shift.getEndDate().after(getEndOfDay(shiftDate))) {
                List<TimeTypeEnum> timeTypeEnums = shift.getActivities().stream().map(ShiftActivity::getSecondLevelTimeType).collect(Collectors.toList());
                if (shift.getStartDate().before(shiftDate)) {
                    updateMap(beforeMap, timeTypeEnums);
                } else {
                    updateMap(afterMap, timeTypeEnums);
                }
            }
        });
        Map<String,Object> response = new HashMap<>();
        response.put(BEFORE,beforeMap);
        response.put(AFTER,afterMap);
        setTimeTypeActivityNameInMap(unitId, response);
        return response;
    }

    private void updateMap(Map<String, Integer> map, List<TimeTypeEnum> timeTypeEnums) {
        if (timeTypeEnums.contains(TimeTypeEnum.AVAILABLE_TIME)) {
            map.put(AVAILABILITY, map.get(AVAILABILITY)+1);
        } else if (timeTypeEnums.contains(TimeTypeEnum.UNAVAILABLE_TIME)) {
            map.put(UNAVAILABILITY, map.get(UNAVAILABILITY)+1);
        } else if (timeTypeEnums.contains(TimeTypeEnum.STOP_BRICK)) {
            map.put(STOP_BRICK, map.get(STOP_BRICK)+1);
        }
    }

    private void setTimeTypeActivityNameInMap(Long unitId, Map<String,Object> response){
        //TODO As discuss with priya configure only one activity of availability and unavailability at unit
        List<Activity> availabilityActivities = activityService.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.AVAILABLE_TIME, newHashSet(unitId));
        response.put(AVAILABILITY_NAME, isCollectionNotEmpty(availabilityActivities) ? availabilityActivities.get(0).getName() : "");
        List<Activity> unavailabilityActivities = activityService.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.UNAVAILABLE_TIME, newHashSet(unitId));
        response.put(UNAVAILABILITY_NAME, isCollectionNotEmpty(unavailabilityActivities) ? unavailabilityActivities.get(0).getName() : "");
        List<Activity> stopBrickActivities = activityService.findAllBySecondLevelTimeTypeAndUnitIds(TimeTypeEnum.STOP_BRICK, newHashSet(unitId));
        response.put(STOP_BRICK_NAME, isCollectionNotEmpty(stopBrickActivities) ? stopBrickActivities.get(0).getName() : "");
    }

    public List<ShiftWithViolatedInfoDTO> removeAvailabilityUnavailabilityBeforeAfterShift(Long staffId, TimeTypeEnum timeTypeEnum, boolean before, boolean removeNearestOne, Date shiftDate) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        Date startDate = before ? getStartOfDay(shiftDate) : shiftDate;
        Date endDate = before ? shiftDate : getEndOfDay(shiftDate);
        List<Shift> shifts = shiftService.findShiftBetweenDurationByStaffId(staffId, startDate, endDate);
        if(removeNearestOne) {
            Collections.sort(shifts, new Comparator<Shift>() {
                @Override
                public int compare(Shift s1, Shift s2) {
                    return before ? s2.getStartDate().compareTo(s1.getStartDate()) : s1.getStartDate().compareTo(s2.getStartDate());
                }
            });
        }
        for (Shift shift : shifts) {
            if(!shift.getEndDate().after(endDate)) {
                List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS1 = removeAvailabilityUnavailabilityActivity(shift, timeTypeEnum);
                if (isCollectionNotEmpty(shiftWithViolatedInfoDTOS1)) {
                    shiftWithViolatedInfoDTOS.addAll(shiftWithViolatedInfoDTOS1);
                    if (removeNearestOne) {
                        break;
                    }
                }
            }
        }
        return shiftWithViolatedInfoDTOS;
    }

    private List<ShiftWithViolatedInfoDTO> removeAvailabilityUnavailabilityActivity(Shift shift, TimeTypeEnum timeTypeEnum) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        List<ShiftActivity> shiftActivities = shift.getActivities().stream().filter(shiftActivity -> !shiftActivity.getSecondLevelTimeType().equals(timeTypeEnum)).collect(Collectors.toList());
        if(isCollectionEmpty(shiftActivities)){
            ShiftWithViolatedInfoDTO shiftWithViolatedInfoDTO = shiftService.deleteAllLinkedShifts(shift.getId());
            shiftWithViolatedInfoDTO.setActionPerformed(DELETE);
            shiftWithViolatedInfoDTOS.add(shiftWithViolatedInfoDTO);
        } else if(shiftActivities.size() != shift.getActivities().size()){
            ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            shiftDTO.setActivities(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftActivities, ShiftActivityDTO.class));
            updateStartAndEndDate(shift, shiftActivities);
            shiftDTO.setStartDate(shiftActivities.get(0).getStartDate());
            shiftDTO.setEndDate(shiftActivities.get(shiftActivities.size()-1).getEndDate());
            List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOList = shiftService.updateShift(shiftDTO, false, false, ShiftActionType.SAVE);
            shiftWithViolatedInfoDTOList.forEach(shiftWithViolatedInfoDTO -> shiftWithViolatedInfoDTO.setActionPerformed(UPDATE));
            shiftWithViolatedInfoDTOS.addAll(shiftWithViolatedInfoDTOList);
        }
        return shiftWithViolatedInfoDTOS;
    }

    private void updateStartAndEndDate(Shift shift, List<ShiftActivity> shiftActivities) {
        for(int n=0; n < shiftActivities.size() ; n++){
            if(shiftActivities.get(n).getStartDate().equals(shift.getActivities().get(n).getStartDate()) && shiftActivities.get(n).getEndDate().equals(shift.getActivities().get(n).getStartDate())){
                continue;
            }
            shiftActivities.get(n).setStartDate(shift.getActivities().get(n+1).getStartDate());
            shiftActivities.get(n).setEndDate(shift.getActivities().get(n+1).getEndDate());
        }
    }

    public Map<String, Long> updateActionInfoOfStaff(Long unitId, Long staffId, String actionName) {
        ActionInfo actionInfo = actionInfoRepository.getByUnitIdAndStaffId(unitId, staffId).orElse(new ActionInfo(unitId, staffId, new HashMap<>()));
        Long actionCount = actionInfo.getActionCount().getOrDefault(actionName, 0L);
        actionInfo.getActionCount().put(actionName, ++actionCount);
        actionInfoRepository.save(actionInfo);
        return actionInfo.getActionCount();
    }

    public Map<String, Long> getActionInfoOfStaff(Long unitId, Long staffId) {
        ActionInfo actionInfo = actionInfoRepository.getByUnitIdAndStaffId(unitId, staffId).orElse(new ActionInfo(unitId, staffId, new HashMap<>()));
        return actionInfo.getActionCount();
    }
}
