package com.kairos.service.questionnaire_template;

import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.custom_exception.InvalidRequestException;
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
import com.mongodb.MongoClientException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.QUESTION_LIST;


@Service
public class QuestionService extends MongoBaseService {

    private Logger logger = LoggerFactory.getLogger(QuestionService.class);

    @Inject
    private QuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    /**
     * @param countryId
     * @param masterQuestionDTOs list of questionDto which belongs to section
     * @return map contain list of questions and question ids
     * @description
     */
    public Map<String, Object> addQuestionsToQuestionSection(Long countryId, List<QuestionDTO> masterQuestionDTOs, QuestionnaireTemplateType templateType) {

        List<BigInteger> questionSectionIds = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<Question> masterQuestions = new ArrayList<>();
        checkForDuplicacyInQuestion(masterQuestionDTOs);
        for (QuestionDTO masterQuestion : masterQuestionDTOs) {
            if (Optional.ofNullable(masterQuestion.getQuestionType()).isPresent()) {
            /*    Question question = new Question(masterQuestion.getQuestion().trim(), masterQuestion.getDescription(), masterQuestion.getQuestionType(), countryId);
                question.setNotSureAllowed(masterQuestion.getNotSureAllowed());
                question.setRequired(masterQuestion.getRequired());
                addAttributeNameToQuestion(question, masterQuestion, templateType);
                masterQuestions.add(question);*/
            } else {
                exceptionService.invalidRequestException("message.invalid.request", masterQuestion.getQuestion() + " not exist");
            }
        }
        try {
            masterQuestions = questionMongoRepository.saveAll(getNextSequence(masterQuestions));
            masterQuestions.forEach(masterQuestion -> questionSectionIds.add(masterQuestion.getId()));
        } catch (MongoClientException e) {
            logger.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;

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
     * @param id        - id of question
     * @param sectionId -sectionId id of questionnaire section
     * @return
     * @description deleted question by id ,and also remove id of question from questionnaire section.
     */
    public Boolean deleteMasterQuestion(Long countryId, BigInteger id, BigInteger sectionId) {

        Question exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " question ", id);
        }
        QuestionnaireSection questionnaireSection = masterQuestionnaireSectionRepository.findByIdAndNonDeleted(countryId, sectionId);
        List<BigInteger> questionsIdList = questionnaireSection.getQuestions();
        if (!questionsIdList.contains(id)) {
            exceptionService.invalidRequestException("message.invalid.request", "question  not present in questionnaire section " + questionnaireSection.getTitle() + "");
        }
        questionsIdList.remove(id);
        questionnaireSection.setQuestions(questionsIdList);
        masterQuestionnaireSectionRepository.save(questionnaireSection);
        delete(exist);
        return true;

    }


    public List<Question> getAllMasterQuestion(Long countryId) {
        return questionMongoRepository.getAllMasterQuestion(countryId);

    }


    public Question getMasterQuestion(Long countryId, BigInteger id) {
        Question exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", id);
        }
        return exist;

    }


    /**
     * @param countryId
     * @param questionDTOs contain list of Existing questions and new questions
     * @return map contain list of questions and question ids.
     * @description method update the existing question(if question contain id) and create new question questions(if not contain id)
     */
    public Map<String, Object> updateExistingQuestionAndCreateNewQuestions(Long countryId, List<QuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {

        checkForDuplicacyInQuestion(questionDTOs);
        List<QuestionDTO> updateExistingQuestions = new ArrayList<>();
        List<QuestionDTO> createNewQuestions = new ArrayList<>();

        questionDTOs.forEach(sectionDto -> {
                    if (Optional.ofNullable(sectionDto.getId()).isPresent()) {
                        updateExistingQuestions.add(sectionDto);
                    } else {
                        createNewQuestions.add(sectionDto);
                    }
                }
        );
        Map<String, Object> updatedQuestions, newQuestions;
        List<BigInteger> questionIds = new ArrayList<>();
        List<Question> masterQuestions = new ArrayList<>();

        if (createNewQuestions.size() != 0) {
            newQuestions = addQuestionsToQuestionSection(countryId, createNewQuestions, templateType);
            questionIds.addAll((List<BigInteger>) newQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<Question>) newQuestions.get(QUESTION_LIST));
        }
        if (updateExistingQuestions.size() != 0) {

            updatedQuestions = updateQuestionsList(countryId, updateExistingQuestions, templateType);
            questionIds.addAll((List<BigInteger>) updatedQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<Question>) updatedQuestions.get(QUESTION_LIST));
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, questionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;

    }


    public Map<String, Object> updateQuestionsList(Long countryId, List<QuestionDTO> masterQuestionDTOs, QuestionnaireTemplateType templateType) {

        List<BigInteger> questionIds = new ArrayList<>();
        masterQuestionDTOs.forEach(question -> questionIds.add(question.getId()));
        List<Question> existingMasterQuestions = questionMongoRepository.getMasterQuestionByCountryIdAndIds(countryId, questionIds);

        Map<BigInteger, Object> masterQuestionDtoCorrespondingToId = new HashMap<>();
        masterQuestionDTOs.forEach(masterQuestionDto -> {
            masterQuestionDtoCorrespondingToId.put(masterQuestionDto.getId(), masterQuestionDto);
        });
        List<Question> updatedQuestionsList = new ArrayList<>();
        for (Question masterQuestion : existingMasterQuestions) {

            QuestionDTO questionDto = (QuestionDTO) masterQuestionDtoCorrespondingToId.get(masterQuestion.getId());
            if (Optional.ofNullable(masterQuestion.getQuestionType()).isPresent()) {
              /*  masterQuestion.setQuestion(questionDto.getQuestion());
                masterQuestion.setNotSureAllowed(questionDto.getNotSureAllowed());
                masterQuestion.setRequired(questionDto.getRequired());
                masterQuestion.setQuestionType(masterQuestion.getQuestionType());
                addAttributeNameToQuestion(masterQuestion, questionDto, templateType);*/
                masterQuestion.setDescription(questionDto.getDescription());
                updatedQuestionsList.add(masterQuestion);
            } else {
                exceptionService.invalidRequestException("message.invalid.request", masterQuestion.getQuestion() + " not exist");
            }
        }
        try {
            updatedQuestionsList = questionMongoRepository.saveAll(getNextSequence(updatedQuestionsList));
        } catch (MongoClientException e) {
            logger.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, questionIds);
        result.put(QUESTION_LIST, updatedQuestionsList);
        return result;

    }


    public Boolean deleteAll(Long countryId, List<BigInteger> questionIdsList) {

        List<Question> questions = questionMongoRepository.getMasterQuestionByCountryIdAndIds(countryId, questionIdsList);
        questions.forEach(masterQuestion -> {
            masterQuestion.setDeleted(true);
        });
        questionMongoRepository.saveAll(questions);
        return true;

    }


    /**
     *
     * @param referenceId
     * @param isUnit
     * @param sectionAndQuestionDTOListMap
     * @param templateType
     * @return
     */
    public List<QuestionnaireSection> saveAndUpdateQuestionAndAddToQuestionnaireSection(Long referenceId, boolean isUnit, Map<QuestionnaireSection, List<QuestionDTO>> sectionAndQuestionDTOListMap, QuestionnaireTemplateType templateType) {

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
                List<Question> questions = isUnit ? buildQuestionForQuestionnaireSectionAtUnitLevel(referenceId, questionListCoresspondingToSection, templateType) : buildQuestionForMasterQuestionnaireSection(referenceId, questionListCoresspondingToSection, templateType);
                sectionQuestionListMap.put(questionnaireSection, questions);
                globalQuestionList.addAll(questions);
            }
            questionnaireSections.add(questionnaireSection);
        });
        if (CollectionUtils.isNotEmpty(questionIdList)) {
            List<Question> existingQuestionList = isUnit ? questionMongoRepository.getQuestionByUnitIdAndIds(referenceId, questionIdList) : questionMongoRepository.getMasterQuestionByCountryIdAndIds(referenceId, questionIdList);
            existingQuestionList.forEach(question -> {
                ObjectMapperUtils.copyProperties(question, questionDTOAndIdMap.get(question.getId()));
            });
            globalQuestionList.addAll(existingQuestionList);
        }
        questionMongoRepository.saveAll(getNextSequence(globalQuestionList));
        questionnaireSections.forEach(questionnaireSection -> {
            questionnaireSection.getQuestions().addAll(sectionQuestionListMap.get(questionnaireSection).stream().map(Question::getId).collect(Collectors.toList()));

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


}
