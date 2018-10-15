package com.kairos.service.questionnaire_template;

import com.kairos.commons.custom_exception.InvalidRequestException;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.QuestionDTO;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.repository.questionnaire_template.QuestionMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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
    private QuestionnaireSectionRepository questionnaireSectionRepository;


    /**
     * @param referenceId
     * @param isReferenceIdUnitId
     * @param sectionAndQuestionDTOListMap
     * @param templateType
     * @return
     */
    public List<QuestionnaireSection> saveAndUpdateQuestionAndAddToQuestionnaireSection(Long referenceId, boolean isReferenceIdUnitId, Map<QuestionnaireSection, List<QuestionDTO>> sectionAndQuestionDTOListMap, QuestionnaireTemplateType templateType) {

        List<BigInteger> questionIdList = new ArrayList<>();
        List<Question> globalQuestionList = new ArrayList<>();
        Map<QuestionnaireSection, List<Question>> sectionQuestionListMap = new HashMap<>();
        Map<BigInteger, QuestionDTO> questionDTOAndIdMap = new HashMap<>();
        List<QuestionnaireSection> questionnaireSections = new ArrayList<>();
        sectionAndQuestionDTOListMap.forEach((questionnaireSection, questionDTOS) -> {

            List<QuestionDTO> questionListCoresspondingToSection = new ArrayList<>();
            questionDTOS.forEach(questionDTO -> {
                if (Optional.ofNullable(questionDTO.getId()).isPresent()) {
                    questionIdList.add(questionDTO.getId());
                    questionDTOAndIdMap.put(questionDTO.getId(), questionDTO);
                } else {
                    questionListCoresspondingToSection.add(questionDTO);
                }
            });
            if (CollectionUtils.isNotEmpty(questionListCoresspondingToSection)) {
                List<Question> questions = isReferenceIdUnitId ? buildQuestionForQuestionnaireSectionAtUnitLevel(referenceId, questionListCoresspondingToSection, templateType) : buildQuestionForMasterQuestionnaireSection(referenceId, questionListCoresspondingToSection, templateType);
                sectionQuestionListMap.put(questionnaireSection, questions);
                globalQuestionList.addAll(questions);
            }
            questionnaireSections.add(questionnaireSection);
        });
        if (CollectionUtils.isNotEmpty(questionIdList)) {
            List<Question> existingQuestionList = isReferenceIdUnitId ? questionMongoRepository.getQuestionByUnitIdAndIds(referenceId, questionIdList) : questionMongoRepository.getMasterQuestionByCountryIdAndIds(referenceId, questionIdList);
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


    private List<Question> buildQuestionForMasterQuestionnaireSection(Long countryId, List<QuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed(), countryId);
            addAttributeNameToQuestion(question, questionDTO, templateType);
            questions.add(question);
        }
        return questions;
    }


    private List<Question> buildQuestionForQuestionnaireSectionAtUnitLevel(Long unitId, List<QuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO questionDTO : questionDTOs) {
            Question question = new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed());
            question.setOrganizationId(unitId);
            addAttributeNameToQuestion(question, questionDTO, templateType);
            questions.add(question);
        }
        return questions;
    }

    public void checkForDuplicacyInQuestion(List<QuestionDTO> masterQuestionDTOs) {

        List<String> titles = new ArrayList<>();
        for (QuestionDTO masterQuestionDto : masterQuestionDTOs) {
            if (titles.contains(masterQuestionDto.getQuestion().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", " question ", masterQuestionDto.getQuestion());
            }
            titles.add(masterQuestionDto.getQuestion().toLowerCase());
        }

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
    public boolean deleteMasterQuestion(Long countryId, BigInteger questionId, BigInteger sectionId) {
        QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findByCountryIdAndId(countryId, sectionId);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Section", sectionId);
        }
        questionnaireSection.getQuestions().remove(questionId);
        questionnaireSectionRepository.save(questionnaireSection);
        questionMongoRepository.safeDelete(questionId);
        return true;
    }


    public boolean deleteQuestionOfQuestionnaireSectionOfUnit(Long unitId, BigInteger questionId, BigInteger sectionId) {
        QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findByUnitIdAndId(unitId, sectionId);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Section", sectionId);
        }
        questionnaireSection.getQuestions().remove(questionId);
        questionnaireSectionRepository.save(questionnaireSection);
        questionMongoRepository.safeDelete(questionId);
        return true;
    }


    public List<Question> getAllMasterQuestion(Long countryId) {
        return questionMongoRepository.getAllMasterQuestion(countryId);

    }


}
