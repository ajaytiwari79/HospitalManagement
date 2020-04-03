package com.kairos.service.action;

import com.kairos.commons.custom_exception.DataNotFoundByIdException;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.ActionType;
import com.kairos.persistence.model.action.Action;
import com.kairos.persistence.repository.action.ActionRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.action.ActionDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.kairos.constants.ActivityMessagesConstants.ACTION;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_DATANOTFOUND;
import static com.kairos.service.shift.ShiftValidatorService.convertMessage;

/**
 * Created By G.P.Ranjan on 2/4/20
 **/
@Service
public class ActionService {
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ActionRepository actionRepository;

    public ActionDTO saveAction(Long unitId, ActionDTO actionDTO) {
        actionDTO.setUnitId(unitId);
        Action action = ObjectMapperUtils.copyPropertiesOrCloneByMapper(actionDTO, Action.class);
        actionRepository.save(action);
        actionDTO.setId(action.getId());
        return actionDTO;
    }

    public ActionDTO getAction(BigInteger actionId){
        Action action = actionRepository.findById(actionId).orElseThrow(()->new DataNotFoundByIdException(convertMessage(MESSAGE_DATANOTFOUND,ACTION,actionId)));
        return ObjectMapperUtils.copyPropertiesOrCloneByMapper(action, ActionDTO.class);
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
}
