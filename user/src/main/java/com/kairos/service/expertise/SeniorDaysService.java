package com.kairos.service.expertise;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.user.country.experties.AgeRangeDTO;
import com.kairos.dto.user.country.experties.SeniorDaysDTO;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.SeniorCareDays;
import com.kairos.persistence.repository.user.expertise.ExpertiseGraphRepository;
import com.kairos.persistence.repository.user.expertise.SeniorDaysGraphRepository;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static com.kairos.constants.UserMessagesConstants.*;

@Service
public class SeniorDaysService {
    @Inject
    private ExpertiseService expertiseService;
    @Inject
    private ExpertiseGraphRepository expertiseGraphRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SeniorDaysGraphRepository seniorDaysGraphRepository;

    public List<SeniorDaysDTO> addSeniorDaysInExpertise(Long expertiseId, SeniorDaysDTO seniorDaysDTO) {
        Expertise expertise = expertiseService.findById(expertiseId,0);
        validateAgeRange(seniorDaysDTO.getSeniorDaysDetails());

        SeniorCareDays seniorCareDays = ObjectMapperUtils.copyPropertiesByMapper(seniorDaysDTO, SeniorCareDays.class);
        seniorCareDays.setExpertise(expertise);
        expertiseGraphRepository.save(expertise);
        return Collections.singletonList(seniorDaysDTO);
    }

//    public List<SeniorDaysDTO> updateSeniorDays(SeniorDaysDTO seniorDaysDTO){
//        SeniorCareDays seniorCareDays=seniorDaysGraphRepository.findById(seniorDaysDTO.getId()).orElse(null);
//        if (seniorCareDays.isHasDraftCopy()) {
//            exceptionService.dataNotFoundByIdException(MESSAGE_DRAFT_COPY_CREATED);
//        }
//        seniorCareDays = ObjectMapperUtils.copyPropertiesByMapper(seniorDaysDTO, SeniorCareDays.class);
//        if(seniorCareDays.isPublished()){
//            seniorCareDays.setId(null);
//            seniorCareDays.setHasDraftCopy(true);
//        }
//
//    }

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
