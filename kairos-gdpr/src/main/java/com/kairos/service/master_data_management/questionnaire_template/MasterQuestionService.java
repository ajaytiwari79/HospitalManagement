package com.kairos.service.master_data_management.questionnaire_template;

import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.master_data.MasterQuestionDto;
import com.kairos.enums.QuestionType;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import com.mongodb.MongoClientException;
import jdk.nashorn.internal.runtime.options.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constant.AppConstant.IDS_LIST;
import static com.kairos.constant.AppConstant.QUESTION_LIST;


@Service
public class MasterQuestionService extends MongoBaseService {

    private Logger logger = LoggerFactory.getLogger(MasterQuestionService.class);

    @Inject
    private MasterQuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;


    public Map<String, Object> addQuestionsToQuestionSection(Long countryId, List<MasterQuestionDto> masterQuestionDtos) {

        List<BigInteger> questionSectionIds = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        checkForDuplicacyInQuestion(masterQuestionDtos);
        for (MasterQuestionDto masterQuestion : masterQuestionDtos) {

            if (QuestionType.valueOf(masterQuestion.getQuestionType())!=null) {
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
        } catch (Exception e) {
            logger.info(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTION_LIST, masterQuestions);
        return result;

    }


    public void checkForDuplicacyInQuestion(List<MasterQuestionDto> masterQuestionDtos) {

        List<String> titles = new ArrayList<>();
        for (MasterQuestionDto masterQuestionDto : masterQuestionDtos) {
            if (titles.contains(masterQuestionDto.getQuestion())) {
                exceptionService.duplicateDataException("message.duplicate", " question ", masterQuestionDto.getQuestion());
            }
            titles.add(masterQuestionDto.getQuestion());
        }


    }



/*
    public MasterQuestion getMasterQuestion(Long countryId, BigInteger id) {

        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", id);
        }
        return exist;

    }

    public MasterQuestion deleteMasterQuestion(BigInteger id) {

        MasterQuestion exist = questionMongoRepository.findByid(id);
        if (exist == null) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", id);
        }
        exist.setDeleted(true);
        save(exist);
        return exist;

    }

    public MasterQuestion updateMasterQuestion(Long countryId, BigInteger id, MasterQuestionDto questionDto) {

        MasterQuestion exist = questionMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (Optional.ofNullable(exist).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "question", questionDto.getQuestion());
        }
        if (questionMongoRepository.findByNameAndCountryId(countryId, questionDto.getQuestion()) == null) {
            exist.setQuestion(questionDto.getQuestion());
            exist.setCountryId(countryId);
            exist.setDescription(questionDto.getDescription());
            exist.setRequired(questionDto.getRequired());
            exist.setNotSureAllowed(questionDto.getNotSureAllowed());
            exist.setQuestionType(questionDto.getQuestionType());
            return save(exist);

        } else
            throw new DuplicateDataException("question with same name Exist");
    }


    public List<MasterQuestion> getAllMasterQuestion(Long countryId) {

        return questionMongoRepository.getAllMasterQuestion(countryId);

    }

    //get master Question by Ids and check if data not exist for id then throw exception
    public List<MasterQuestion> getMasterQuestionListByIds(Long countryId, Set<BigInteger> ids) {
        List<MasterQuestion> masterQuestions = questionMongoRepository.getMasterQuestionListByIds(countryId, ids);
        Set<BigInteger> questionIds = new HashSet<>();
        masterQuestions.forEach(masterQuestion -> {
            questionIds.add(masterQuestion.getId());
        });
        if (questionIds.size() != ids.size()) {
            ids.removeAll(questionIds);
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.master.question", ids.iterator().next());

        }
        return masterQuestions;

    }
*/


}
