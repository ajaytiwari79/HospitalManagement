package com.kairos.service.questionnaire_template;


import com.kairos.dto.gdpr.QuestionDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.QuestionnaireSectionDTO;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.questionnaire_template.QuestionMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.constants.AppConstant.QUESTIONNAIRE_SECTIONS;
import static com.kairos.constants.AppConstant.QUESTION_LIST;
import static com.kairos.constants.AppConstant.IDS_LIST;


@Service
public class QuestionnaireSectionService extends MongoBaseService {

    private Logger LOGGER = LoggerFactory.getLogger(QuestionnaireSectionService.class);


    @Inject
    private QuestionnaireSectionRepository questionnaireSectionRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionService questionService;

    @Inject
    private QuestionMongoRepository questionMongoRepository;


    @Inject
    private QuestionnaireTemplateService questionnaireTemplateService;


    @Inject
    private QuestionnaireTemplateMongoRepository questionnaireTemplateMongoRepository;


    /**
     * @param countryId
     * @param templateId                    questionnaire template id ,required to fetch
     * @param masterQuestionnaireSectionDto contains list of sections ,And section contain list of questions
     * @return add sections ids to questionnaire template and return questionnaire template
     * @description questionnaireSection contain list of sections and list of sections ids.
     */
    public QuestionnaireTemplateResponseDTO addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, BigInteger templateId, List<QuestionnaireSectionDTO> masterQuestionnaireSectionDto) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByCountryIdAndId(countryId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  template", templateId);
        }
        Boolean flag = false;
        for (QuestionnaireSectionDTO sectionDTO : masterQuestionnaireSectionDto) {
            if (Optional.ofNullable(sectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }
        }
        Map<String, Object> questionnaireSection;
        if (flag) {
            questionnaireSection = updateExistingQuestionnaireSectionsAndCreateNewSectionsWithQuestions(countryId, masterQuestionnaireSectionDto, questionnaireTemplate.getTemplateType());
        } else {
            questionnaireSection = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, masterQuestionnaireSectionDto, questionnaireTemplate.getTemplateType());

        }
        questionnaireTemplate.setSections((List<BigInteger>) questionnaireSection.get(IDS_LIST));
        try {
            questionnaireTemplate = questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        } catch (Exception e) {
            questionnaireSectionRepository.deleteAll((Set<QuestionnaireSection>) questionnaireSection.get(QUESTIONNAIRE_SECTIONS));
            questionMongoRepository.deleteAll((Set<Question>) questionnaireSection.get(QUESTION_LIST));
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return questionnaireTemplateService.getMasterQuestionnaireTemplateWithSectionById(countryId, questionnaireTemplate.getId());

    }


    /**
     * @param countryId
     * @param masterQuestionnaireSectionDTOs contain list of sections ,and section list of questions
     * @return return map ,which contain list of section ,list of sections id and list of questions which is needed for rollback
     * @description method create new question sections belong to questionnaire template and and questions which belong to section.
     * and rollback if exception occur in questionnaire section service to delete inserted questions
     */
    public Map<String, Object> createQuestionnaireSectionAndCreateAndAddQuestions(Long countryId, List<QuestionnaireSectionDTO> masterQuestionnaireSectionDTOs, QuestionnaireTemplateType templateType) {

        Map<String, Object> result = new HashMap<>();
        List<QuestionnaireSection> masterQuestionnaireSections = new ArrayList<>();
        List<Question> questionList = new ArrayList<>();
        List<BigInteger> questionSectionIds = new ArrayList<>();
        checkForDuplicacyInTitleOfSections(masterQuestionnaireSectionDTOs);
        for (QuestionnaireSectionDTO questionnaireSectionDto : masterQuestionnaireSectionDTOs) {
            QuestionnaireSection questionnaireSection = new QuestionnaireSection(questionnaireSectionDto.getTitle(), countryId);
            if (Optional.ofNullable(questionnaireSectionDto.getQuestions()).isPresent() && !questionnaireSectionDto.getQuestions().isEmpty()) {
                Map<String, Object> questions = questionService.addQuestionsToQuestionSection(countryId, questionnaireSectionDto.getQuestions(), templateType);
                questionList = (List<Question>) questions.get(QUESTION_LIST);
                questionnaireSection.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
            }
            masterQuestionnaireSections.add(questionnaireSection);
        }
        try {
            masterQuestionnaireSections = questionnaireSectionRepository.saveAll(getNextSequence(masterQuestionnaireSections));
            masterQuestionnaireSections.forEach(masterQuestionnaireSection -> questionSectionIds.add(masterQuestionnaireSection.getId()));
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            questionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTIONNAIRE_SECTIONS, masterQuestionnaireSections);
        result.put(QUESTION_LIST, questionList);
        return result;


    }


    /**
     * @param countryId
     * @param id
     * @param templateId
     * @return
     */
    public Boolean deleteQuestionnaireSection(Long countryId, BigInteger id, BigInteger templateId) {
        QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findByIdAndNonDeleted(countryId, id);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire section", id);
        }
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByCountryIdAndId(countryId, templateId);
        List<BigInteger> questionnaireTemplateSectionIdList = questionnaireTemplate.getSections();
        if (!questionnaireTemplateSectionIdList.contains(id)) {
            exceptionService.invalidRequestException("message.invalid", "section not present in template");
        }
        questionnaireTemplateSectionIdList.remove(id);
        questionnaireTemplate.setSections(questionnaireTemplateSectionIdList);
        if (!questionnaireSection.getQuestions().isEmpty()) {
            questionService.deleteAll(countryId, questionnaireSection.getQuestions());
        }
        delete(questionnaireSection);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return true;
    }


    /**
     * @param countryId
     * @param questionnaireSectionDto contain list of  existing sections and new sections,( section contain list of existing questions and new question)
     * @return return update master questionnaire template with sections qnd questions
     * @description this method update existing sections (if contain id) and create new sections(if not contain id in request)
     * and update existing questions if contain id in request and create new question if not contain id in request which belongs to section
     **/
    public Map<String, Object> updateExistingQuestionnaireSectionsAndCreateNewSectionsWithQuestions(Long countryId, List<QuestionnaireSectionDTO> questionnaireSectionDto, QuestionnaireTemplateType templateType) {

        checkForDuplicacyInTitleOfSections(questionnaireSectionDto);
        List<QuestionnaireSectionDTO> updateExistingSectionsList = new ArrayList<>();
        List<QuestionnaireSectionDTO> createNewQuestionnaireSections = new ArrayList<>();

        questionnaireSectionDto.forEach(sectionDto -> {
            if (Optional.ofNullable(sectionDto.getId()).isPresent()) {
                updateExistingSectionsList.add(sectionDto);
            } else {
                createNewQuestionnaireSections.add(sectionDto);
            }
        });
        List<Question> questions = new ArrayList<>();
        List<QuestionnaireSection> sections = new ArrayList<>();

        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<String, Object> updatedSections = new HashMap<>(), newSections = new HashMap<>();
        if (updateExistingSectionsList.size() != 0) {
            updatedSections = updateQuestionnaireSectionAndQuestionList(countryId, updateExistingSectionsList, templateType);
            sectionsIds.addAll((List<BigInteger>) updatedSections.get(IDS_LIST));
            questions.addAll((List<Question>) updatedSections.get(QUESTION_LIST));
            sections.addAll((List<QuestionnaireSection>) updatedSections.get(QUESTIONNAIRE_SECTIONS));

        }
        if (!createNewQuestionnaireSections.isEmpty()) {
            newSections = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, createNewQuestionnaireSections, templateType);
            sectionsIds.addAll((List<BigInteger>) newSections.get(IDS_LIST));
            questions.addAll((List<Question>) newSections.get(QUESTION_LIST));
            sections.addAll((List<QuestionnaireSection>) newSections.get(QUESTIONNAIRE_SECTIONS));

        }
        Map<String, Object> result = new HashMap<>();
        result.put(QUESTIONNAIRE_SECTIONS, sections);
        result.put(QUESTION_LIST, questions);
        result.put(IDS_LIST, sectionsIds);
        return result;
    }


    /**
     * @param countryId
     * @param updateSectionsAndQuestionsListDto contain list of Questionnaire section and questions list
     * @return map which contain list of sections ,section ids and questions.
     * @description this method update list of existing sections and update questions if exist already other wise create questions
     */
    public Map<String, Object> updateQuestionnaireSectionAndQuestionList(Long countryId, List<QuestionnaireSectionDTO> updateSectionsAndQuestionsListDto, QuestionnaireTemplateType templateType) {

        List<QuestionnaireSection> updateSectionsList = new ArrayList<>();
        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<BigInteger, Object> sectionsDtoCorrespondingToId = new HashMap<>();

        updateSectionsAndQuestionsListDto.forEach(section -> {
            sectionsIds.add(section.getId());
            sectionsDtoCorrespondingToId.put(section.getId(), section);
        });
        List<Question> questionList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<QuestionnaireSection> sections = questionnaireSectionRepository.findSectionByCountryIdAndIds(countryId, sectionsIds);
        for (QuestionnaireSection section : sections) {
            QuestionnaireSectionDTO sectionDto = (QuestionnaireSectionDTO) sectionsDtoCorrespondingToId.get(section.getId());
            if (Optional.ofNullable(sectionDto.getQuestions()).isPresent() && !sectionDto.getQuestions().isEmpty()) {
                Map<String, Object> questions = questionService.updateExistingQuestionAndCreateNewQuestions(countryId, sectionDto.getQuestions(), templateType);
                section.setTitle(sectionDto.getTitle());
                section.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
                questionList.addAll((List<Question>) questions.get(QUESTION_LIST));
            }
            updateSectionsList.add(section);
        }

        try {
            updateSectionsList = questionnaireSectionRepository.saveAll(getNextSequence(updateSectionsList));

        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            questionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, sectionsIds);
        result.put(QUESTIONNAIRE_SECTIONS, updateSectionsList);
        result.put(QUESTION_LIST, questionList);
        return result;

    }


    //todo  add save and update method sections
    public List<QuestionnaireSectionDTO> createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplateOfUnit(Long unitId, BigInteger questionnaireTemplateId, List<QuestionnaireSectionDTO> questionnaireSectionDTOS) {

        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByUnitIdAndId(unitId, questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template", questionnaireTemplateId);
        }
        checkForDuplicacyInTitleOfSections(questionnaireSectionDTOS);
        List<BigInteger> sectionIdList = createAndUpdateQuestionnaireSectionsAndQuestions(unitId, questionnaireSectionDTOS, questionnaireTemplate.getTemplateType());
        questionnaireTemplate.setSections(sectionIdList);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return questionnaireSectionDTOS;
    }



    public List<BigInteger> createAndUpdateQuestionnaireSectionsAndQuestions(Long unitId, List<QuestionnaireSectionDTO> questionnaireSectionDTOS, QuestionnaireTemplateType templateType) {

        List<BigInteger> existingSectionIdList = new ArrayList<>();
        Map<BigInteger, List<QuestionDTO>> questionDTOsCoresspondingToExisitingSectionId = new HashMap<>();
        Map<QuestionnaireSection, List<QuestionDTO>> questionDTOListCoresspondingToSection = new HashMap<>();
        List<QuestionnaireSection> questionnaireSections = new ArrayList<>();

        for (QuestionnaireSectionDTO questionnaireSectionDTO : questionnaireSectionDTOS) {
            if (Optional.ofNullable(questionnaireSectionDTO.getId()).isPresent()) {
                existingSectionIdList.add(questionnaireSectionDTO.getId());
                questionDTOsCoresspondingToExisitingSectionId.put(questionnaireSectionDTO.getId(), questionnaireSectionDTO.getQuestions());

            } else {
                QuestionnaireSection questionnaireSection = new QuestionnaireSection(questionnaireSectionDTO.getTitle());
                questionnaireSection.setOrganizationId(unitId);
                questionnaireSections.add(questionnaireSection);
                questionDTOListCoresspondingToSection.put(questionnaireSection, questionnaireSectionDTO.getQuestions());

            }
        }
        if (CollectionUtils.isNotEmpty(existingSectionIdList)) {
            List<QuestionnaireSection> previousQuestionnaireSections = questionnaireSectionRepository.findSectionByUnitIdAndIds(unitId, existingSectionIdList);
            questionnaireSections.addAll(previousQuestionnaireSections);
            questionnaireSections.forEach(questionnaireSection -> {
                questionDTOListCoresspondingToSection.put(questionnaireSection, questionDTOsCoresspondingToExisitingSectionId.get(questionnaireSection.getId()));
            });
        }
        List<BigInteger> sectionIdList;
        if (!questionDTOListCoresspondingToSection.isEmpty()) {
            questionnaireSections = questionService.saveAndUpdateQuestionAndAddToQuestionnaireSection(unitId, true, questionDTOListCoresspondingToSection, templateType);
            sectionIdList = questionnaireSectionRepository.saveAll(getNextSequence(questionnaireSections)).stream().map(QuestionnaireSection::getId).collect(Collectors.toList());

        } else {
            sectionIdList = questionnaireSectionRepository.saveAll(getNextSequence(questionnaireSections)).stream().map(QuestionnaireSection::getId).collect(Collectors.toList());
        }
        return sectionIdList;
    }




    public void checkForDuplicacyInTitleOfSections(List<QuestionnaireSectionDTO> questionnaireSectionDTOs) {
        List<String> titles = new ArrayList<>();
        for (QuestionnaireSectionDTO questionnaireSectionDto : questionnaireSectionDTOs) {
            if (titles.contains(questionnaireSectionDto.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Questionnaire section", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle().toLowerCase());
        }
    }


}
