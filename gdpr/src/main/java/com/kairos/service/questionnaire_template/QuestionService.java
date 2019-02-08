package com.kairos.service.questionnaire_template;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.questionnaire_template.QuestionDTO;
import com.kairos.persistence.model.questionnaire_template.QuestionDeprecated;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSectionDeprecated;
import com.kairos.persistence.repository.questionnaire_template.QuestionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
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


   /* *//**
     * @param referenceId
     * @param isUnitId
     * @param sectionAndQuestionDTOListMap
     * @param templateType
     * @return
     *//*
    public List<QuestionnaireSectionDeprecated> saveAndUpdateQuestionAndAddToQuestionnaireSection(Long referenceId, boolean isUnitId, Map<QuestionnaireSectionDeprecated, List<QuestionDTO>> sectionAndQuestionDTOListMap, QuestionnaireTemplateType templateType) {

        List<QuestionDeprecated> globalQuestionList = new ArrayList<>();
        Map<QuestionnaireSectionDeprecated, List<QuestionDeprecated>> sectionQuestionListMap = new HashMap<>();
       // Map<BigInteger, QuestionDTO> questionDTOAndIdMap = new HashMap<>();
        List<QuestionnaireSectionDeprecated> questionnaireSections = new ArrayList<>();
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
                List<QuestionDeprecated> questions = buildQuestionForQuestionnaireSectionAtUnitLevel(referenceId, isUnitId,questionListCorrespondingToSection, templateType);
                sectionQuestionListMap.put(questionnaireSection, questions);
                globalQuestionList.addAll(questions);
            }
            questionnaireSections.add(questionnaireSection);
        });
        //TODO
      *//*  if (CollectionUtils.isNotEmpty(questionDTOAndIdMap.keySet())) {
            List<Question> existingQuestionList = isUnitId ? questionMongoRepository.getQuestionByUnitIdAndIds(referenceId, questionDTOAndIdMap.keySet()) : questionMongoRepository.getMasterQuestionByCountryIdAndIds(referenceId, questionDTOAndIdMap.keySet());
            existingQuestionList.forEach(question -> {
                QuestionDTO questionDTO = questionDTOAndIdMap.get(question.getId());
                question.setQuestion(questionDTO.getQuestion()).setDescription(questionDTO.getDescription()).
                        setQuestionType(questionDTO.getQuestionType()).setNotSureAllowed(questionDTO.isNotSureAllowed()).setRequired(questionDTO.isRequired()).setAttributeName(questionDTO.getAttributeName());
            });
            globalQuestionList.addAll(existingQuestionList);
        }
        questionMongoRepository.saveAll(getNextSequence(globalQuestionList));*//*
        questionnaireSections.forEach(questionnaireSection -> {
            if (Optional.ofNullable(sectionQuestionListMap.get(questionnaireSection)).isPresent()) {
               // questionnaireSection.getQuestions().addAll(sectionQuestionListMap.get(questionnaireSection).stream().map(Question::getId).collect(Collectors.toList()));
            }
        });
        return questionnaireSections;
    }

*/
    private List<QuestionDeprecated> buildQuestionForQuestionnaireSectionAtUnitLevel(Long referenceId, boolean isUnitId, List<QuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {
        List<QuestionDeprecated> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            QuestionDeprecated question = new QuestionDeprecated(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed());
           /* if (isUnitId)
                question.setOrganizationId(referenceId);
            else
                question.setCountryId(referenceId);*/
            addAttributeNameToQuestion(question, questionDTO, templateType);
            questions.add(question);
        }
        return questions;
    }


    public void addAttributeNameToQuestion(QuestionDeprecated masterQuestion, QuestionDTO masterQuestionDTO, QuestionnaireTemplateType templateType) {

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
        Question question = questionRepository.findQuestionByIdAndSectionId( questionId,sectionId);
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


    public List<Question> getAllMasterQuestion(Long countryId) {
        return questionRepository.getAllMasterQuestion(countryId);

    }


}
