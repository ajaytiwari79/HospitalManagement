package com.kairos.service.master_data_management.questionnaire_template;

import com.kairos.dto.master_data.MasterQuestionDTO;
import com.kairos.enums.QuestionType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.service.MongoBaseService;
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


    public Map<String, Object> addQuestionsToQuestionSection(Long countryId, List<MasterQuestionDTO> masterQuestionDtos) {

        List<BigInteger> questionSectionIds = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        checkForDuplicacyInQuestion(masterQuestionDtos);
        for (MasterQuestionDTO masterQuestion : masterQuestionDtos) {
            if (QuestionType.valueOf(masterQuestion.getQuestionType()) != null) {
                MasterQuestion question = new MasterQuestion(masterQuestion.getQuestion().trim(), masterQuestion.getDescription(), masterQuestion.getQuestionType(), countryId);
                masterQuestion.setNotApplicableAllowed(masterQuestion.getNotApplicableAllowed());
                masterQuestion.setNotSureAllowed(masterQuestion.getNotSureAllowed());
                masterQuestion.setRequired(masterQuestion.getRequired());
                masterQuestions.add(question);
            } else {
                exceptionService.invalidRequestException("message.invalid.request", masterQuestion.getQuestion() + " not exist");
            }
        }
        try {
            masterQuestions = save(masterQuestions);
            masterQuestions.forEach(masterQuestion -> questionSectionIds.add(masterQuestion.getId()));
        } catch (MongoClientException e) {
            logger.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;

    }


    public void checkForDuplicacyInQuestion(List<MasterQuestionDTO> masterQuestionDtos) {

        List<String> titles = new ArrayList<>();
        for (MasterQuestionDTO masterQuestionDto : masterQuestionDtos) {
            if (titles.contains(masterQuestionDto.getQuestion().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", " question ", masterQuestionDto.getQuestion());
            }
            titles.add(masterQuestionDto.getQuestion().toLowerCase());
        }


    }

    public Boolean deleteMasterQuestion(Long countryId, BigInteger id) {

        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", " question ", id);
        }
        exist.setDeleted(true);
        save(exist);
        return true;

    }


    public List<MasterQuestion> getAllMasterQuestion(Long countryId) {

        return questionMongoRepository.getAllMasterQuestion(countryId);

    }


    public MasterQuestion getMasterQuestion(Long countryId, BigInteger id) {

        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", id);
        }
        return exist;

    }


    public Map<String, Object> updateExistingQuestionAndCreateNewQuestions(Long countryId, List<MasterQuestionDTO> questionDtos) {

        checkForDuplicacyInQuestion(questionDtos);
        List<MasterQuestionDTO> updateExistingQuestions = new ArrayList<>();
        List<MasterQuestionDTO> createNewQuestions = new ArrayList<>();

        questionDtos.forEach(sectionDto -> {
                    if (Optional.ofNullable(sectionDto.getId()).isPresent()) {
                        updateExistingQuestions.add(sectionDto);
                    } else {
                        createNewQuestions.add(sectionDto);
                    }
                }
        );
        Map<String, Object> updatedQuestions,newQuestions ;
        List<BigInteger> questionIds = new ArrayList<>();
        List<MasterQuestion> masterQuestions = new ArrayList<>();

        if (createNewQuestions.size() != 0) {
            newQuestions = addQuestionsToQuestionSection(countryId, createNewQuestions);
            questionIds.addAll((List<BigInteger>) newQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<MasterQuestion>) newQuestions.get(QUESTION_LIST));
        }
        if (updateExistingQuestions.size() != 0) {

            updatedQuestions = updateQuestionsList(countryId, updateExistingQuestions);
            questionIds.addAll((List<BigInteger>) updatedQuestions.get(IDS_LIST));
            masterQuestions.addAll((List<MasterQuestion>) updatedQuestions.get(QUESTION_LIST));
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, questionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;


    }


    public Map<String, Object> updateQuestionsList(Long countryId, List<MasterQuestionDTO> masterQuestionDtos) {

        List<BigInteger> questionids = new ArrayList<>();
        masterQuestionDtos.forEach(question -> questionids.add(question.getId()));
        List<MasterQuestion> ExisitingMasterQuestions = questionMongoRepository.getMasterQuestionListByIds(countryId, questionids);

        Map<BigInteger, Object> masterQuestionDtoCorrespondingToId = new HashMap<>();
        masterQuestionDtos.forEach(masterQuestionDto -> {
            masterQuestionDtoCorrespondingToId.put(masterQuestionDto.getId(), masterQuestionDto);
        });
        List<MasterQuestion> updatedQuestionsList = new ArrayList<>();
        for (MasterQuestion masterQuestion : ExisitingMasterQuestions) {

            MasterQuestionDTO questionDto = (MasterQuestionDTO) masterQuestionDtoCorrespondingToId.get(masterQuestion.getId());
            if (QuestionType.valueOf(questionDto.getQuestionType()) != null) {
                masterQuestion.setQuestion(questionDto.getQuestion());
                masterQuestion.setNotSureAllowed(questionDto.getNotSureAllowed());
                masterQuestion.setRequired(questionDto.getRequired());
                masterQuestion.setQuestionType(questionDto.getQuestionType());
                masterQuestion.setDescription(questionDto.getDescription());
                updatedQuestionsList.add(masterQuestion);
            } else {
                exceptionService.invalidRequestException("message.invalid.request", masterQuestion.getQuestion() + " not exist");
            }
        }
        try {
            updatedQuestionsList = save(updatedQuestionsList);
        } catch (MongoClientException e) {
            logger.info(e.getMessage());
            throw new MongoClientException(e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put(IDS_LIST, questionids);
        result.put(QUESTION_LIST, updatedQuestionsList);
        return result;

    }


}
