package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterQuestionnaireSectionService extends MongoBaseService {
    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private MasterQuestionService masterQuestionService;


    //add Master Questionnaire Section and Check if Master Question Exist then add id and if not then throw Exception
    public List<MasterQuestionnaireSection> addMasterQuestionSection(Long countryId, ValidateListOfRequestBody<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtoList) {

        List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos = masterQuestionnaireSectionDtoList.getRequestBody();
        List<BigInteger> questionIds = new ArrayList<>();
        masterQuestionnaireSectionDtos.forEach(masterQuestionnaireSectionDto -> {
            questionIds.addAll(masterQuestionnaireSectionDto.getQuestions());
        });
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        return null;
    }

    public Boolean deleteMasterQuestionnaireSection(BigInteger id) {
        MasterQuestionnaireSection exists = masterQuestionnaireSectionRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.duplicate", "message.questionnaire.section", id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }


}
