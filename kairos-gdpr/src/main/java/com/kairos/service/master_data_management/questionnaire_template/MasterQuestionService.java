package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class MasterQuestionService extends MongoBaseService {


    @Inject
    private MasterQuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public MasterQuestion addQuestion(Long countryId, MasterQuestionDto questionDto) {

        MasterQuestion exist = questionMongoRepository.findByNameAndCountryId(countryId, questionDto.getName());
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.duplicate", "question", questionDto.getName());
        }
        MasterQuestion newQuestion = new MasterQuestion();
        newQuestion.setCountryId(countryId);
        newQuestion.setDescription(questionDto.getDescription());
        newQuestion.setRequired(questionDto.getRequired());
        newQuestion.setNotSureAllowed(questionDto.getNotSureAllowed());
        newQuestion.setQuestionType(questionDto.getQuestionType().value);
        return save(newQuestion);

    }

}
