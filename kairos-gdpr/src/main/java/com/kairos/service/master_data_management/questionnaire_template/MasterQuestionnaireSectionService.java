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


    public MasterQuestionnaireTemplate addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, BigInteger templateId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto) {
        MasterQuestionnaireTemplate existing = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, templateId);
        if (!Optional.ofNullable(existing).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", templateId);
        }
        Map<String, Object> questionnaireSection = new HashMap<>();
        questionnaireSection = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, masterQuestionnaireSectionDto);
        existing.setSections((List<BigInteger>) questionnaireSection.get(IDS_LIST));
        try {
            existing = save(existing);
        } catch (Exception e) {
            masterQuestionnaireSectionRepository.deleteAll((Set<MasterQuestionnaireSection>) questionnaireSection.get(QUESTIONNIARE_SECTIONS));
            masterQuestionMongoRepository.deleteAll((Set<MasterQuestion>) questionnaireSection.get(QUESTION_LIST));
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return existing;

    }


    public Map<String, Object> createQuestionnaireSectionAndCreateAndAddQuestions(Long countryId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDtos) {

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


    public Boolean deletedQuestionniareSection(Long countryId, BigInteger id) {
        MasterQuestionnaireSection questionnaireSection = masterQuestionnaireSectionRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", id);
        }
        questionnaireSection.setDeleted(true);
        save(questionnaireSection);
        return true;
    }





/*

    public MasterQuestionnaireTemplate updateAndDeleteQuestionnaireSectionsAndQuestionsAndAddToQuestionnaireTemplate(Long countryId, BigInteger templateId, List<MasterQuestionnaireSectionDto> masterQuestionnaireSectionDto) {
        MasterQuestionnaireTemplate existing = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, templateId);
        if (!Optional.ofNullable(existing).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionniare template", templateId);
        }
        Map<String, Object> questionnaireSection = new HashMap<>();
        questionnaireSection = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, masterQuestionnaireSectionDto);
        existing.setSections((List<BigInteger>) questionnaireSection.get(IDS_LIST));
        try {
            existing = save(existing);
        } catch (Exception e) {
            masterQuestionnaireSectionRepository.deleteAll((Set<MasterQuestionnaireSection>) questionnaireSection.get(QUESTIONNIARE_SECTIONS));
            masterQuestionMongoRepository.deleteAll((Set<MasterQuestion>) questionnaireSection.get(QUESTION_LIST));
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return existing;

    }


*/

    //TODO update Questionnaire section and Questions

    public Map<String, Object> updateAndDeleteAndModifyQuestionniareSection(Long countryId, BigInteger id, List<MasterQuestionnaireSectionDto> questionnaireSectionDto) {

        List<BigInteger> questionniareSectionsIdsToBeDeleted = new ArrayList<>();
        List<MasterQuestionnaireSectionDto> updatingAndModifyingQuestionniareSectionIds = new ArrayList<>();
        List<MasterQuestionnaireSectionDto> newQuestionniareSectionsAndQuestionsToBeCreated = new ArrayList<>();

        questionnaireSectionDto.forEach(section -> {
            if (Optional.ofNullable(section.getId()).isPresent() && section.getDeleted()) {
                questionniareSectionsIdsToBeDeleted.add(section.getId());
            } else if (Optional.ofNullable(section.getId()).isPresent() && !section.getDeleted()) {

                updatingAndModifyingQuestionniareSectionIds.add(section);
            } else {
                newQuestionniareSectionsAndQuestionsToBeCreated.add(section);
            }
        });

        Map<String, Object> newSections = new HashMap<>();
        if (questionniareSectionsIdsToBeDeleted.size() != 0) {
            deleteQuestionniareSectionListAndQuestions(countryId, questionniareSectionsIdsToBeDeleted);

        }
        if (newQuestionniareSectionsAndQuestionsToBeCreated.size() != 0) {
            newSections = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, newQuestionniareSectionsAndQuestionsToBeCreated);
        }
        if (newQuestionniareSectionsAndQuestionsToBeCreated.size() != 0) {
            newSections.putAll(updateQuestionnaireSectionAndQuestionList(countryId, newQuestionniareSectionsAndQuestionsToBeCreated));
        }

        //TODO   working on it
        return null;


    }


    public void deleteQuestionniareSectionListAndQuestions(Long countryId, List<BigInteger> ids) {

        List<MasterQuestionnaireSection> questionnaireSections = masterQuestionnaireSectionRepository.getQuestionnniareSectionListByIds(countryId, ids);
        questionnaireSections.forEach(section ->
        {
            masterQuestionService.deleteQuestionsListByIds(countryId, section.getQuestions());
            section.setDeleted(true);
        });
        save(questionnaireSections);

    }

    //TODO  update questionnaire sections list and also filter deleted question ,new Question and update questiom
    public Map<String, Object> updateQuestionnaireSectionAndQuestionList(Long countryId, List<MasterQuestionnaireSectionDto> updateSectionsAndQuestionsListDto) {


        List<BigInteger> questionniareSectionsIds = new ArrayList<>();
        updateSectionsAndQuestionsListDto.forEach(sectionDto -> {
            questionniareSectionsIds.add(sectionDto.getId());

        });

        List<MasterQuestionnaireSection> questionnaireSections = masterQuestionnaireSectionRepository.getQuestionnniareSectionListByIds(countryId, questionniareSectionsIds);

        Map<BigInteger, MasterQuestionnaireSection> sections = new HashMap<>();
        questionnaireSections.forEach(questionnaireSection -> {

            sections.put(questionnaireSection.getId(), questionnaireSection);


        });


        //  working on it
        return null;

    }


}
