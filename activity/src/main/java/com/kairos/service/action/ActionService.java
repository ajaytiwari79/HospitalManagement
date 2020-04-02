package com.kairos.service.action;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.persistence.model.action.Action;
import com.kairos.persistence.repository.action.ActionRepository;
import com.kairos.service.exception.ExceptionService;
import com.kairos.wrapper.action.ActionDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNull;

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
        Action action = ObjectMapperUtils.copyPropertiesByMapper(actionDTO, Action.class);
        actionRepository.save(action);
        actionDTO.setId(action.getId());
        return actionDTO;
    }

    public ActionDTO getAction(BigInteger actionId){
        Action action = actionRepository.findOne(actionId);
        if(isNull(action)){
            exceptionService.dataNotFoundByIdException("Action Not Found");
        }
        return ObjectMapperUtils.copyPropertiesByMapper(action, ActionDTO.class);
    }

    public List<ActionDTO> getAllActionByUnitId(Long unitId){
        return actionRepository.getAllByUnitId(unitId);
    }

    public ActionDTO updateAction(Long unitId, BigInteger actionId, ActionDTO actionDTO) {
        Action action = actionRepository.findOne(actionId);
        if(isNull(action) || unitId != action.getUnitId()){
            exceptionService.actionNotPermittedException("Invalid Action Id");
        }
        action.setName(actionDTO.getName());
        action.setDescription(actionDTO.getDescription());
        actionRepository.save(action);
        actionDTO.setId(actionId);
        return actionDTO;
    }
}
