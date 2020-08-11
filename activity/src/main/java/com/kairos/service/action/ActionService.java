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
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.action.ActionRepository;
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
    private static final String BEFORE = "BEFORE";
    private static final String AFTER = "AFTER";
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActionRepository actionRepository;
    @Inject
    private ShiftService shiftService;

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

    public Map<String,Object> getAvailabilityUnavailabilityBeforeAfterShift(Long staffId, Date date) {
        Map<String,Boolean> beforeMap = new HashMap<String,Boolean>(){{put(AVAILABILITY,false);put(UNAVAILABILITY,false);}};
        Map<String,Boolean> afterMap = new HashMap<>(beforeMap);
        List<Shift> shifts = shiftService.findShiftBetweenDurationByStaffId(staffId,getStartOfDay(date),getEndOfDay(date));
        shifts.forEach(shift -> {
            List<TimeTypeEnum> timeTypeEnums = shift.getActivities().stream().map(ShiftActivity::getSecondLevelTimeType).collect(Collectors.toList());
            if(shift.getStartDate().before(date)){
                if(timeTypeEnums.contains(TimeTypeEnum.AVAILABLE_TIME)){
                    beforeMap.put(AVAILABILITY,true);
                } else if(timeTypeEnums.contains(TimeTypeEnum.UNAVAILABLE_TIME)){
                    beforeMap.put(UNAVAILABILITY,true);
                }
            } else {
                if(timeTypeEnums.contains(TimeTypeEnum.AVAILABLE_TIME)){
                    afterMap.put(AVAILABILITY,true);
                } else if(timeTypeEnums.contains(TimeTypeEnum.UNAVAILABLE_TIME)){
                    afterMap.put(UNAVAILABILITY,true);
                }
            }
        });
        Map<String,Object> response = new HashMap<>();
        response.put(BEFORE,beforeMap);
        response.put(AFTER,afterMap);
        return response;
    }

    public List<ShiftWithViolatedInfoDTO> removeAvailabilityUnavailabilityBeforeAfterShift(Long staffId, boolean isAvailability, boolean isBefore, Date date) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        List<Shift> shifts = shiftService.findShiftBetweenDurationByStaffId(staffId, isBefore ? getStartOfDay(date) : date, isBefore ? date : getEndOfDay(date));
        for (Shift shift : shifts) {
            List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS1 = deleteAvailabilityUnavailabilityShift(shift, isAvailability ? TimeTypeEnum.AVAILABLE_TIME : TimeTypeEnum.UNAVAILABLE_TIME);
            if(isCollectionNotEmpty(shiftWithViolatedInfoDTOS1)){
                shiftWithViolatedInfoDTOS.addAll(shiftWithViolatedInfoDTOS1);
            }
        }
        return shiftWithViolatedInfoDTOS;
    }

    private List<ShiftWithViolatedInfoDTO> deleteAvailabilityUnavailabilityShift(Shift shift, TimeTypeEnum timeTypeEnum) {
        List<ShiftWithViolatedInfoDTO> shiftWithViolatedInfoDTOS = new ArrayList<>();
        List<ShiftActivity> shiftActivities = shift.getActivities().stream().filter(shiftActivity -> !shiftActivity.getSecondLevelTimeType().equals(timeTypeEnum)).collect(Collectors.toList());
        if(isCollectionEmpty(shiftActivities)){
            shiftWithViolatedInfoDTOS.add(shiftService.deleteAllLinkedShifts(shift.getId()));
        } else if(shiftActivities.size() != shift.getActivities().size()){
            ShiftDTO shiftDTO = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftDTO.class);
            shiftDTO.setActivities(ObjectMapperUtils.copyCollectionPropertiesByMapper(shiftActivities, ShiftActivityDTO.class));
            shiftWithViolatedInfoDTOS.addAll(shiftService.updateShift(shiftDTO, false, false, ShiftActionType.SAVE));
        }
        return shiftWithViolatedInfoDTOS;
    }

}
