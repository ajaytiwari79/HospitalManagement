package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import static com.kairos.constant.AppConstant.QUESTIONNIARE_SECTIONS;
import static com.kairos.constant.AppConstant.QUESTION_LIST;
import static com.kairos.constant.AppConstant.IDS_LIST;


@Service
public class MasterQuestionnaireSectionService extends MongoBaseService {

    private Logger LOGGER = LoggerFactory.getLogger(MasterQuestionnaireSectionService.class);


    @Inject
    private MasterQuestionnaireSectionRepository masterQuestionnaireSectionRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private MasterQuestionService masterQuestionService;

    @Inject
    private MasterQuestionMongoRepository masterQuestionMongoRepository;

    @Inject
    private MongoTemplate mongoTemplate;


    //add questionnaire section in questionnaire template
    //masterQuestionnaireSectionDtos is list or questionnaire section ,each section contain questions list
    public Map<String, Object> addQuestionnaireSection(Long countryId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {

        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> masterQuestionnaireSections = new ArrayList<>();
        List<MasterQuestion> questionList = new ArrayList<>();
        List<BigInteger> questionSectionIds = new ArrayList<>();
        checkForDuplicacyInTitle(masterQuestionnaireSectionDtos);
        for (MasterQuestionnaireSectionDto questionnaireSectionDto : masterQuestionnaireSectionDtos) {
            MasterQuestionnaireSection questionnaireSection = new MasterQuestionnaireSection(questionnaireSectionDto.getTitle(), countryId);
            Map<String, Object> questions = masterQuestionService.addQuestionsToQuestionSection(countryId, questionnaireSectionDto.getQuestions());
            questionList = (List<MasterQuestion>) questions.get(QUESTION_LIST);
            questionnaireSection.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
            masterQuestionnaireSections.add(questionnaireSection);

        }
        try {
            masterQuestionnaireSections = save(masterQuestionnaireSections);
            masterQuestionnaireSections.forEach(masterQuestionnaireSection -> {
                questionSectionIds.add(masterQuestionnaireSection.getId());
            });
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);

        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTIONNIARE_SECTIONS, masterQuestionnaireSections);
        result.put(QUESTION_LIST, questionList);
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
