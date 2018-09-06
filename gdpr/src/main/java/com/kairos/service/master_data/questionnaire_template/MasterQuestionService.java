package com.kairos.service.master_data.questionnaire_template;

import com.kairos.custom_exception.InvalidRequestException;
import com.kairos.enums.AssetAttributeName;
import com.kairos.enums.ProcessingActivityAttributeName;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.gdpr.master_data.MasterQuestionDTO;
import com.kairos.enums.QuestionType;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.IDS_LIST;
import static com.kairos.constants.AppConstant.QUESTION_LIST;


@Service
public class MasterQuestionService extends MongoBaseService {

    private Logger logger = LoggerFactory.getLogger(MasterQuestionService.class);

    @Inject
    private MasterQuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    /**
     * @param countryId
     * @param organizationId
     * @param masterQuestionDTOs list of questionDto which belongs to section
     * @return map contain list of questions and question ids
     * @description
     */
    public Map<String, Object> addQuestionsToQuestionSection(Long countryId, Long organizationId, List<MasterQuestionDTO> masterQuestionDTOs, QuestionnaireTemplateType templateType) {

        List<BigInteger> questionSectionIds = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        checkForDuplicacyInQuestion(masterQuestionDTOs);
        for (MasterQuestionDTO masterQuestion : masterQuestionDTOs) {
            QuestionType questionType = QuestionType.valueOf(masterQuestion.getQuestionType());
            if (Optional.ofNullable(questionType).isPresent()) {
                MasterQuestion question = new MasterQuestion(masterQuestion.getQuestion().trim(), masterQuestion.getDescription(), questionType, countryId);
                question.setNotSureAllowed(masterQuestion.getNotSureAllowed());
                question.setRequired(masterQuestion.getRequired());
                question.setOrganizationId(organizationId);
                addAttributeNameToQuestion(question, masterQuestion, templateType);
                masterQuestions.add(question);
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


    public void checkForDuplicacyInQuestion(List<MasterQuestionDTO> masterQuestionDTOs) {

        List<String> titles = new ArrayList<>();
        for (MasterQuestionDTO masterQuestionDto : masterQuestionDTOs) {
            if (titles.contains(masterQuestionDto.getQuestion().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", " question ", masterQuestionDto.getQuestion());
            }
            titles.add(masterQuestionDto.getQuestion().toLowerCase());
        }

    }


    public void addAttributeNameToQuestion(MasterQuestion masterQuestion, MasterQuestionDTO masterQuestionDTO, QuestionnaireTemplateType templateType) {

        if (!Optional.ofNullable(templateType).isPresent()) {
            exceptionService.invalidRequestException("message.invalid.request", " Attribute name is incorrect");
        }
        switch (templateType) {
            case ASSET_TYPE:
                if (Optional.ofNullable(AssetAttributeName.valueOf(masterQuestionDTO.getAttributeName())).isPresent()) {
                    masterQuestion.setAttributeName(masterQuestionDTO.getAttributeName());
                    break;
                }
                throw new InvalidRequestException("Attribute not found for Asset ");
            case PROCESSING_ACTIVITY:
                if (Optional.ofNullable(ProcessingActivityAttributeName.valueOf(masterQuestionDTO.getAttributeName())).isPresent()) {
                    masterQuestion.setAttributeName(masterQuestionDTO.getAttributeName());
                    break;
                }
                throw new InvalidRequestException("Attribute not found for Processing Activity");
        }

    }


    /**
     * @param countryId
     * @param organizationId
     * @param id             - id of question
     * @param sectionId      -sectionId id of questionnaire section
     * @return
     * @description deleted question by id ,and also remove id of question from questionnaire section.
     */
    public Boolean deleteMasterQuestion(Long countryId, Long organizationId, BigInteger id, BigInteger sectionId) {

        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " question ", id);
        }
        MasterQuestionnaireSection questionnaireSection = masterQuestionnaireSectionRepository.findByIdAndNonDeleted(countryId, organizationId, sectionId);
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


    public List<MasterQuestion> getAllMasterQuestion(Long countryId, Long organizationId) {
        return questionMongoRepository.getAllMasterQuestion(countryId, organizationId);

    }


    public MasterQuestion getMasterQuestion(Long countryId, Long organizationId, BigInteger id) {
        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, organizationId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", id);
        }
        return exist;

    }


    /**
     * @param countryId
     * @param organizationId
     * @param questionDTOs   contain list of Existing questions and new questions
     * @return map contain list of questions and question ids.
     * @description method update the existing question(if question contain id) and create new question questions(if not contain id)
     */
    public Map<String, Object> updateExistingQuestionAndCreateNewQuestions(Long countryId, Long organizationId, List<MasterQuestionDTO> questionDTOs, QuestionnaireTemplateType templateType) {

        checkForDuplicacyInQuestion(questionDTOs);
        List<MasterQuestionDTO> updateExistingQuestions = new ArrayList<>();
        List<MasterQuestionDTO> createNewQuestions = new ArrayList<>();

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
        List<MasterQuestion> masterQuestions = new ArrayList<>();

        if (createNewQuestions.size() != 0) {
            newQuestions = addQuestionsToQuestionSection(countryId, organizationId, createNewQuestions, templateType);
            questionIds.addAll((List<BigInteger>) newQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<MasterQuestion>) newQuestions.get(QUESTION_LIST));
        }
        if (updateExistingQuestions.size() != 0) {

            updatedQuestions = updateQuestionsList(countryId, organizationId, updateExistingQuestions, templateType);
            questionIds.addAll((List<BigInteger>) updatedQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<MasterQuestion>) updatedQuestions.get(QUESTION_LIST));
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, questionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;

    }


    public Map<String, Object> updateQuestionsList(Long countryId, Long organizationId, List<MasterQuestionDTO> masterQuestionDTOs, QuestionnaireTemplateType templateType) {

        List<BigInteger> questionIds = new ArrayList<>();
        masterQuestionDTOs.forEach(question -> questionIds.add(question.getId()));
        List<MasterQuestion> existingMasterQuestions = questionMongoRepository.getMasterQuestionListByIds(countryId, organizationId, questionIds);

        Map<BigInteger, Object> masterQuestionDtoCorrespondingToId = new HashMap<>();
        masterQuestionDTOs.forEach(masterQuestionDto -> {
            masterQuestionDtoCorrespondingToId.put(masterQuestionDto.getId(), masterQuestionDto);
        });
        List<MasterQuestion> updatedQuestionsList = new ArrayList<>();
        for (MasterQuestion masterQuestion : existingMasterQuestions) {

            MasterQuestionDTO questionDto = (MasterQuestionDTO) masterQuestionDtoCorrespondingToId.get(masterQuestion.getId());
            QuestionType questionType = QuestionType.valueOf(questionDto.getQuestionType());
            if (Optional.ofNullable(questionType).isPresent()) {
                masterQuestion.setQuestion(questionDto.getQuestion());
                masterQuestion.setNotSureAllowed(questionDto.getNotSureAllowed());
                masterQuestion.setRequired(questionDto.getRequired());
                masterQuestion.setQuestionType(questionType);
                addAttributeNameToQuestion(masterQuestion, questionDto, templateType);
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


    public Boolean deleteAll(Long countryId, Long orgId, List<BigInteger> questionIdsList) {

        List<MasterQuestion> questions = questionMongoRepository.getMasterQuestionListByIds(countryId, orgId, questionIdsList);
        questions.forEach(masterQuestion -> {
            masterQuestion.setDeleted(true);
        });
        questionMongoRepository.saveAll(questions);
        return true;

    }


}
