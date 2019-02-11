package com.kairos.service.questionnaire_template;

import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.repository.questionnaire_template.QuestionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.util.*;

@Service
public class QuestionService{

    private Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionnaireSectionRepository questionnaireSectionRepository;

    @Inject
    private QuestionRepository questionRepository;



    /**
     * @param questionId - id of question
     * @param sectionId  -sectionId id of questionnaire section
     * @return
     * @description deleted question by id ,and also remove id of question from questionnaire section.
     */
    public boolean deleteQuestionOfQuestionnaireSection(Long questionId, Long sectionId) {
        Question question = questionRepository.findByIdAndDeletedFalse( questionId);
        if (!Optional.ofNullable(question).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Question", questionId);
        }
        questionRepository.delete(question);
        return true;
    }




    public List<Question> getAllMasterQuestion(Long countryId) {
        return questionRepository.getAllMasterQuestion(countryId);

    }


}
