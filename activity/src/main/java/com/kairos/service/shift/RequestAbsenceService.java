package com.kairos.service.shift;

import com.kairos.dto.activity.shift.RequestAbsenceDTO;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.constants.ActivityMessagesConstants.MESSAGE_SHIFT_ID;
import static com.kairos.constants.ActivityMessagesConstants.REQUEST_ABSENCE_APPROVED;

/**
 * Created by pradeep
 * Created at 13/6/19
 **/
@Service
public class RequestAbsenceService {

    @Inject private ShiftMongoRepository shiftMongoRepository;
    @Inject private ExceptionService exceptionService;

    public RequestAbsenceDTO createRequestAbsence(RequestAbsenceDTO requestAbsenceDTO){
        Optional<Shift> shiftOptional = shiftMongoRepository.findById(requestAbsenceDTO.getShiftId());
        if(!shiftOptional.isPresent()){
            exceptionService.dataNotFoundException(MESSAGE_SHIFT_ID,requestAbsenceDTO.getActivityId());
        }
        if(isNotNull(shiftOptional.get().getRequestAbsence()) && shiftOptional.get().getRequestAbsence().getTodoStatus().equals(TodoStatus.APPROVED)){
            exceptionService.actionNotPermittedException(REQUEST_ABSENCE_APPROVED);
        }

        return requestAbsenceDTO;
    }
}
