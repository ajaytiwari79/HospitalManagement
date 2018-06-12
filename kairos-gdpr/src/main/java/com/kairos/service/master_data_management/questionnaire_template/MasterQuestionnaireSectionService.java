package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.custome_exception.DuplicateDataException;
import com.kairos.dto.master_data.MasterQuestionDto;
import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.validate_list.ValidateListOfRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class MasterQuestionnaireSectionService extends MongoBaseService {

    private Logger LOGGER = LoggerFactory.getLogger(MasterQuestionnaireSectionService.class);


    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private MasterQuestionMongoRepository masterQuestionMongoRepository;

    @Inject
    private MongoTemplate mongoTemplate;


    //add Master Questionnaire Section and Check if Master Question Exist then add id and if not then throw Exception
   /* public List<MasterQuestionnaireSection> addMasterQuestionSection(Long countryId, ValidateListOfRequestBody<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtoList) {

        List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos = masterQuestionnaireSectionDtoList.getRequestBody();
        List<BigInteger> questionIds = new ArrayList<>();
        masterQuestionnaireSectionDtos.forEach(masterQuestionnaireSectionDto -> {
            questionIds.addAll(masterQuestionnaireSectionDto.getQuestions());
        });
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        return null;
    }

    public Boolean deleteMasterQuestionnaireSection(BigInteger id) {
        MasterQuestionnaireSection exists = masterQuestionnaireSectionRepository.findByid(id);
        if (!Optional.ofNullable(exists).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.duplicate", "message.questionnaire.section", id);
        }
        exists.setDeleted(true);
        save(exists);
        return true;

    }*/


    public  Map<String, Object> addQuestionnaireSection(Long countryId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {


        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> masterQuestionnaireSections = new ArrayList<>();
        List<MasterQuestion> questionList = new ArrayList<>();
        Set<BigInteger> questionSectionIds=new HashSet<>();
        checkForDuplicacyInTitle(masterQuestionnaireSectionDtos);
        try {
            for (MasterQuestionnaireSectionDto questionnaireSectionDto : masterQuestionnaireSectionDtos) {
                MasterQuestionnaireSection questionnaireSection = new MasterQuestionnaireSection(questionnaireSectionDto.getTitle(), countryId);
                Map<String, Object> questions = addQuestionsToQuestionSection(countryId, questionnaireSectionDto.getQuestions());
                questionList = (List<MasterQuestion>) questions.get("questions");
                questionnaireSection.setQuestions((Set<BigInteger>) questions.get("ids"));
                masterQuestionnaireSections.add(questionnaireSection);

            }
            masterQuestionnaireSections = save(masterQuestionnaireSections);

            masterQuestionnaireSections.forEach(masterQuestionnaireSection -> {
                questionSectionIds.add(masterQuestionnaireSection.getId());
            });
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);

        }
        result.put("ids",questionSectionIds);
        result.put("sections",masterQuestionnaireSections);
        return result;


    }


    public Map<String, Object> addQuestionsToQuestionSection(Long countryId, List<MasterQuestionDto> masterQuestionDtos) {

        Set<BigInteger> questionSectionIds = new HashSet<>();
        Map<String, Object> result = new HashMap<>();
        List<MasterQuestion> masterQuestions = new ArrayList<>();
        for (MasterQuestionDto masterQuestion : masterQuestionDtos) {
            MasterQuestion question = new MasterQuestion(masterQuestion.getQuestion().trim(), masterQuestion.getDescription(), masterQuestion.getQuestionType(), countryId);
            masterQuestion.setNotApplicableAllowed(masterQuestion.getNotApplicableAllowed());
            masterQuestion.setNotSureAllowed(masterQuestion.getNotSureAllowed());
            masterQuestion.setRequired(masterQuestion.getRequired());
            masterQuestions.add(question);
        }
        masterQuestions = masterQuestionMongoRepository.saveAll(masterQuestions);
        masterQuestions.forEach(masterQuestion -> questionSectionIds.add(masterQuestion.getId()));
        result.put("ids", questionSectionIds);
        result.put("questions", masterQuestions);
        return result;

    }


    public void checkForDuplicacyInTitle(List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {
        List<String> titles = new ArrayList<>();
        for (MasterQuestionnaireSectionDto questionnaireSectionDto : masterQuestionnaireSectionDtos) {
            if (titles.contains(questionnaireSectionDto.getTitle())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle());
        }
    }


}
