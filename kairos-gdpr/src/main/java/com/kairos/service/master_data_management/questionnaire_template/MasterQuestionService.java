package com.kairos.service.master_data_management.questionnaire_template;

import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.master_data.MasterQuestionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterQuestionService extends MongoBaseService {


    @Inject
    private MasterQuestionMongoRepository questionMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    public List<MasterQuestion> addMasterQuestion(Long countryId, ValidateListOfRequestBody<MasterQuestionDto> masterQuestionDtos) {


        List<MasterQuestionDto> masterQuestionDtoList = masterQuestionDtos.getRequestBody();
        List<MasterQuestion> masterQuestions = new ArrayList<>();

        for (MasterQuestionDto masterQuestionDto : masterQuestionDtoList) {

            MasterQuestion masterQuestion = new MasterQuestion(masterQuestionDto.getName(), masterQuestionDto.getDescription(), masterQuestionDto.getQuestionType().value, countryId);
            masterQuestion.setRequired(masterQuestionDto.getRequired());
            masterQuestion.setNotSureAllowed(masterQuestionDto.getNotSureAllowed());
            masterQuestions.add(masterQuestion);

        }
        masterQuestions = save(masterQuestions);
        return masterQuestions;

    }

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
            exceptionService.duplicateDataException("message.duplicate", "question", questionDto.getName());
        }
        if (questionMongoRepository.findByNameAndCountryId(countryId, questionDto.getName()) == null) {
            exist.setName(questionDto.getName());
            exist.setCountryId(countryId);
            exist.setDescription(questionDto.getDescription());
            exist.setRequired(questionDto.getRequired());
            exist.setNotSureAllowed(questionDto.getNotSureAllowed());
            exist.setQuestionType(questionDto.getQuestionType().value);
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


}
