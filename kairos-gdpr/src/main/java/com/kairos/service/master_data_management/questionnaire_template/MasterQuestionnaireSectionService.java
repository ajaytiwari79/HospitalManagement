package com.kairos.service.master_data_management.questionnaire_template;


import com.kairos.dto.master_data.MasterQuestionnaireSectionDto;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.model.master_data_management.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data_management.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
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


    @Inject
    private MasterQuestionnaireTemplateMongoRepository masterQuestionnaireTemplateMongoRepository;


    /**
     *
     * @param countryId
     * @param templateId
     * @param masterQuestionnaireSectionDto contains list of sections and questions
     * @return  add sections ids to questionniare template and return questionniare template
     */
    public MasterQuestionnaireTemplate addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, BigInteger templateId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto) {
        MasterQuestionnaireTemplate questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", templateId);
        }
        Map<String, Object> questionnaireSection = new HashMap<>();
        questionnaireSection = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, masterQuestionnaireSectionDto);
        questionnaireTemplate.setSections((List<BigInteger>) questionnaireSection.get(IDS_LIST));
        try {
            questionnaireTemplate = save(questionnaireTemplate);
        } catch (Exception e) {
            masterQuestionnaireSectionRepository.deleteAll((Set<MasterQuestionnaireSection>) questionnaireSection.get(QUESTIONNIARE_SECTIONS));
            masterQuestionMongoRepository.deleteAll((Set<MasterQuestion>) questionnaireSection.get(QUESTION_LIST));
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return questionnaireTemplate;

    }

//create questionniare sections and create questions and add them to questionniare template
    public Map<String, Object> createQuestionnaireSectionAndCreateAndAddQuestions(Long countryId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {

        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> masterQuestionnaireSections = new ArrayList<>();
        List<MasterQuestion> questionList = new ArrayList<>();
        List<BigInteger> questionSectionIds = new ArrayList<>();
        checkForDuplicacyInTitleOfSections(masterQuestionnaireSectionDtos);
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
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTIONNIARE_SECTIONS, masterQuestionnaireSections);
        result.put(QUESTION_LIST, questionList);
        return result;


    }


    public void checkForDuplicacyInTitleOfSections(List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {
        List<String> titles = new ArrayList<>();
        for (MasterQuestionnaireSectionDto questionnaireSectionDto : masterQuestionnaireSectionDtos) {
            if (titles.contains(questionnaireSectionDto.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle().toLowerCase());
        }
    }



    public Boolean deletedQuestionniareSection(Long countryId, BigInteger id) {
        MasterQuestionnaireSection questionnaireSection = masterQuestionnaireSectionRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare section", id);
        }
        questionnaireSection.setDeleted(true);
        save(questionnaireSection);
        return true;
    }


    /**
     * @param countryId
     * @param id
     * @param questionnaireSectionDto contain list of updated section and new section which we have to create and add to questionniare template
     * @return
     */
    public MasterQuestionnaireTemplate updateExistingQuestionniareSectionsAndCreateNewSectionsWithQuestions(Long countryId, BigInteger id, List<MasterQuestionnaireSectionDto> questionnaireSectionDto) {

        MasterQuestionnaireTemplate template = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(template).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", id);
        }
        checkForDuplicacyInTitleOfSections(questionnaireSectionDto);
        List<MasterQuestionnaireSectionDto> updateExistingSectionsList = new ArrayList<>();
        List<MasterQuestionnaireSectionDto> createNewSectionsinTemplate = new ArrayList<>();

        questionnaireSectionDto.forEach(sectionDto -> {
                    if (Optional.ofNullable(sectionDto.getId()).isPresent()) {
                        updateExistingSectionsList.add(sectionDto);
                    } else {
                        createNewSectionsinTemplate.add(sectionDto);
                    } }
        );

        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<String, Object> updatedSections = new HashMap<>(), newSections = new HashMap<>();
        if (updateExistingSectionsList.size() != 0) {
            updatedSections = updateQuestionnaireSectionAndQuestionList(countryId, updateExistingSectionsList);
            sectionsIds.addAll((List<BigInteger>) updatedSections.get(IDS_LIST));
        }
        if (createNewSectionsinTemplate.size() != 0) {
            newSections = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, createNewSectionsinTemplate);
            sectionsIds.addAll((List<BigInteger>) newSections.get(IDS_LIST));
        }
        template.setSections(sectionsIds);
        try {
            template = save(template);
        } catch (MongoException e) {

            List<MasterQuestionnaireSection> sections = new ArrayList<>();
            sections.addAll((List<MasterQuestionnaireSection>) newSections.get(QUESTIONNIARE_SECTIONS));
            sections.addAll((List<MasterQuestionnaireSection>) updatedSections.get(QUESTIONNIARE_SECTIONS));
            List<MasterQuestion> questions = new ArrayList<>();
            questions.addAll((List<MasterQuestion>) newSections.get(QUESTION_LIST));
            questions.addAll((List<MasterQuestion>) updatedSections.get(QUESTION_LIST));
            masterQuestionMongoRepository.deleteAll(questions);
            masterQuestionnaireSectionRepository.deleteAll(sections);
            LOGGER.info(e.getMessage());
            throw new MongoException(e.getMessage());
        }

        return template;
    }


    /**
     * @param countryId
     * @param updateSectionsAndQuestionsListDto contain list of Questionniare section and questions list
     * @return
     */
    public Map<String, Object> updateQuestionnaireSectionAndQuestionList(Long countryId, List<MasterQuestionnaireSectionDto> updateSectionsAndQuestionsListDto) {

        List<MasterQuestionnaireSection> updateSectionsList = new ArrayList<>();
        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<BigInteger, Object> sectionsDtoCorrespondingToId = new HashMap<>();

        updateSectionsAndQuestionsListDto.forEach(section -> {
            sectionsIds.add(section.getId());
            sectionsDtoCorrespondingToId.put(section.getId(), section);

        });
        List<MasterQuestion> questionList = new ArrayList<>();

        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> sections = masterQuestionnaireSectionRepository.getQuestionnniareSectionListByIds(countryId, sectionsIds);
        for (MasterQuestionnaireSection section : sections) {
            MasterQuestionnaireSectionDto sectionDto = (MasterQuestionnaireSectionDto) sectionsDtoCorrespondingToId.get(section.getId());
            Map<String, Object> questions = masterQuestionService.updateExistingQuestionAndCreateNewQuestions(countryId, sectionDto.getQuestions());
            section.setTitle(sectionDto.getTitle());
            section.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
            questionList.addAll((List<MasterQuestion>) questions.get(QUESTION_LIST));
            updateSectionsList.add(section);
        }

        try {
            updateSectionsList = save(updateSectionsList);

        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, sectionsIds);
        result.put(QUESTIONNIARE_SECTIONS, updateSectionsList);
        result.put(QUESTION_LIST, questionList);
        return result;

    }

}
