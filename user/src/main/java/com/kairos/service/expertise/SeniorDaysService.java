package com.kairos.service.expertise;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.expertise.SeniorAndChildCareDaysDTO;
import com.kairos.persistence.model.user.expertise.CareDays;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.service.exception.ExceptionService;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_EXPERTISE_AGE_OVERLAP;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_EXPERTISE_AGE_RANGEINVALID;

public class SeniorDaysService {

    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private ExceptionService exceptionService;

    public SeniorAndChildCareDaysDTO saveSeniorDays(Long expertiseId, @Valid List<AgeRangeDTO> ageRangeDTO) {
        validateAgeRange(ageRangeDTO);
        Expertise expertise = expertiseService.findById(expertiseId,1);
        List<CareDays> careDays = ObjectMapperUtils.copyCollectionPropertiesByMapper(ageRangeDTO, CareDays.class);
        FunctionalPayment functionalPayment = validateAndGetDomainObject(functionalPaymentDTO, expertiseLine);
        functionalPaymentGraphRepository.save(functionalPayment);
        functionalPaymentDTO.setId(functionalPayment.getId());
        return functionalPaymentDTO;
    }

    //Validating age range
    private void validateAgeRange(List<AgeRangeDTO> ageRangeDTO) {
        Collections.sort(ageRangeDTO);
        for (int i = 0; i < ageRangeDTO.size(); i++) {
            if (ageRangeDTO.get(i).getTo() != null && (ageRangeDTO.get(i).getFrom() > ageRangeDTO.get(i).getTo()))
                exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_AGE_RANGEINVALID, ageRangeDTO.get(i).getFrom(), ageRangeDTO.get(i).getTo());
            if (ageRangeDTO.size() > 1 && i < ageRangeDTO.size() - 1 && ageRangeDTO.get(i).getTo() > ageRangeDTO.get(i + 1).getFrom())
                exceptionService.actionNotPermittedException(MESSAGE_EXPERTISE_AGE_OVERLAP);

        }

    }
}
