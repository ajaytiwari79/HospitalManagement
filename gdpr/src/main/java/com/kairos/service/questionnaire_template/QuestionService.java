package com.kairos.service.questionnaire_template;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.questionnaire_template.QuestionDTO;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionMD;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.repository.questionnaire_template.QuestionMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionMDRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuestionService extends MongoBaseService {

    private Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Inject
    private QuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionnaireSectionMDRepository questionnaireSectionRepository;

    @Inject
    private QuestionRepository questionRepository;


    /**
     * @param referenceId
     * @param isUnitId
     * @param sectionAndQuestionDTOListMap
     * @param templateType
     * @return
     */
    public List<QuestionnaireSection> saveAndUpdateQuestionAndAddToQuestionnaireSection(Long referenceId, boolean isUnitId, Map<QuestionnaireSection, List<QuestionDTO>> sectionAndQuestionDTOListMap, QuestionnaireTemplateType templateType) {

        List<Question> globalQuestionList = new ArrayList<>();
        Map<QuestionnaireSection, List<Question>> sectionQuestionListMap = new HashMap<>();
        Map<BigInteger, QuestionDTO> questionDTOAndIdMap = new HashMap<>();
        List<QuestionnaireSection> questionnaireSections = new ArrayList<>();
        Set<String> titles = new HashSet<>();
        sectionAndQuestionDTOListMap.forEach((questionnaireSection, questionDTOS) -> {

            List<QuestionDTO> questionListCorrespondingToSection = new ArrayList<>();
            questionDTOS.forEach(questionDTO -> {
                if (titles.contains(questionDTO.getQuestion().toLowerCase().trim())) {
                    exceptionService.duplicateDataException("message.duplicate", " question ", questionDTO.getQuestion());
                }
                titles.add(questionDTO.getQuestion().toLowerCase().trim());
                if (Optional.ofNullable(questionDTO.getId()).isPresent())
                    questionDTOAndIdMap.put(questionDTO.getId(), questionDTO);
                else
                    questionListCorrespondingToSection.add(questionDTO);
            });
            if (CollectionUtils.isNotEmpty(questionListCorrespondingToSection)) {
                List<Question> questions = buildQuestionForQuestionnaireSectionAtUnitLevel(referenceId, isUnitId,questionListCorrespondingToSection, templateType);
                sectionQuestionListMap.put(questionnaireSection, questions);
                globalQuestionList.addAll(questions);
            }
            questionnaireSections.add(questionnaireSection);
        });
        if (CollectionUtils.isNotEmpty(questionDTOAndIdMap.keySet())) {
            List<Question> existingQuestionList = isUnitId ? questionMongoRepository.getQuestionByUnitIdAndIds(referenceId, questionDTOAndIdMap.keySet()) : questionMongoRepository.getMasterQuestionByCountryIdAndIds(referenceId, questionDTOAndIdMap.keySet());
            existingQuestionList.forEach(question -> {
                QuestionDTO questionDTO = questionDTOAndIdMap.get(question.getId());
                question.setQuestion(questionDTO.getQuestion()).setDescription(questionDTO.getDescription()).
                        setQuestionType(questionDTO.getQuestionType()).setNotSureAllowed(questionDTO.isNotSureAllowed()).setRequired(questionDTO.isRequired()).setAttributeName(questionDTO.getAttributeName());
            });
            globalQuestionList.addAll(existingQuestionList);
        }
        questionMongoRepository.saveAll(getNextSequence(globalQuestionList));
        questionnaireSections.forEach(questionnaireSection -> {
            if (Optional.ofNullable(sectionQuestionListMap.get(questionnaireSection)).isPresent()) {
                questionnaireSection.getQuestions().addAll(sectionQuestionListMap.get(questionnaireSection).stream().map(Question::getId).collect(Collectors.toList()));
            }
        });
        return questionnaireSections;
    }


    private List<Question> buildQuestionForQuestionnaireSectionAtUnitLevel(Long referenceId, boolean isUnitId, List<QuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed());
            if (isUnitId)
                question.setOrganizationId(referenceId);
            else
                question.setCountryId(referenceId);
            addAttributeNameToQuestion(question, questionDTO, templateType);
            questions.add(question);
        }
        return questions;
    }


    public void addAttributeNameToQuestion(Question masterQuestion, QuestionDTO masterQuestionDTO, QuestionnaireTemplateType templateType) {

        if (!Optional.ofNullable(templateType).isPresent()) {
            exceptionService.invalidRequestException("message.invalid.request", " Attribute name is incorrect");
        }
        switch (templateType) {
            case ASSET_TYPE:
                if (Optional.ofNullable(AssetAttributeName.valueOf(masterQuestionDTO.getAttributeName()).value).isPresent()) {
                    masterQuestion.setAttributeName(masterQuestionDTO.getAttributeName());
                    break;
                }
                throw new InvalidRequestException("Attribute not found for Asset ");
            case PROCESSING_ACTIVITY:
                if (Optional.ofNullable(ProcessingActivityAttributeName.valueOf(masterQuestionDTO.getAttributeName()).value).isPresent()) {
                    masterQuestion.setAttributeName(masterQuestionDTO.getAttributeName());
                    break;
                }
                throw new InvalidRequestException("Attribute not found for Processing Activity");
        }

    }


    /**
     * @param countryId
     * @param questionId - id of question
     * @param sectionId  -sectionId id of questionnaire section
     * @return
     * @description deleted question by id ,and also remove id of question from questionnaire section.
     */
    public boolean deleteQuestionOfQuestionnaireSection(Long countryId, Long questionId, Long sectionId) {
        QuestionMD question = questionRepository.findQuestionByIdAndSectionId( questionId,sectionId);
        if (!Optional.ofNullable(question).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Question", sectionId);
        }
        try{
        question.delete();
        questionnaireSectionRepository.unlinkQuestionFromQuestionnaireSection(sectionId, questionId);
        questionRepository.save(question);
        }catch (EntityNotFoundException ene){
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Question", questionId);
        }
        return true;
    }


    /*public boolean deleteQuestionOfQuestionnaireSectionOfUnit(Long unitId, BigInteger questionId, BigInteger sectionId) {
        QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findQuestionnaireSectionByUnitIdAndId(unitId, sectionId);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Section", sectionId);
        }
        questionnaireSection.getQuestions().remove(questionId);
        questionnaireSectionRepository.save(questionnaireSection);
        questionMongoRepository.safeDeleteById(questionId);
        return true;
    }*/


    public List<QuestionMD> getAllMasterQuestion(Long countryId) {
        return questionRepository.getAllMasterQuestion(countryId);

    }


}
