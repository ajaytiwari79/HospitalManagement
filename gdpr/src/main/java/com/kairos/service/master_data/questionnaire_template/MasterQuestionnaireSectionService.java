package com.kairos.service.master_data.questionnaire_template;


import com.kairos.gdpr.master_data.MasterQuestionnaireSectionDTO;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestion;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireSection;
import com.kairos.persistance.model.master_data.questionnaire_template.MasterQuestionnaireTemplate;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireSectionRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

import static com.kairos.constants.AppConstant.QUESTIONNAIRE_SECTIONS;
import static com.kairos.constants.AppConstant.QUESTION_LIST;
import static com.kairos.constants.AppConstant.IDS_LIST;


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
    private MasterQuestionnaireTemplateService masterQuestionnaireTemplateService;


    @Inject
    private MasterQuestionnaireTemplateMongoRepository masterQuestionnaireTemplateMongoRepository;


    /**
     * @param countryId
     * @param templateId                    questionnaire template id ,required to fetch
     * @param masterQuestionnaireSectionDto contains list of sections ,And section contain list of questions
     * @return add sections ids to questionnaire template and return questionnaire template
     * @description questionnaireSection contain list of sections and list of sections ids.
     */
    public MasterQuestionnaireTemplateResponseDTO addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, Long orgId, BigInteger templateId, List<MasterQuestionnaireSectionDTO> masterQuestionnaireSectionDto) {
        MasterQuestionnaireTemplate questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, orgId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  template", templateId);
        }
        Boolean flag = false;
        for (MasterQuestionnaireSectionDTO sectionDTO : masterQuestionnaireSectionDto) {
            if (Optional.ofNullable(sectionDTO.getId()).isPresent()) {
                flag = true;
                break;
            }
        }
        Map<String, Object> questionnaireSection;
        if (flag) {
            questionnaireSection = updateExistingQuestionnaireSectionsAndCreateNewSectionsWithQuestions(countryId, orgId, masterQuestionnaireSectionDto);
        } else {
            questionnaireSection = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, orgId, masterQuestionnaireSectionDto);

        }
        questionnaireTemplate.setSections((List<BigInteger>) questionnaireSection.get(IDS_LIST));
        try {
            questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        } catch (Exception e) {
            masterQuestionnaireSectionRepository.deleteAll((Set<MasterQuestionnaireSection>) questionnaireSection.get(QUESTIONNAIRE_SECTIONS));
            masterQuestionMongoRepository.deleteAll((Set<MasterQuestion>) questionnaireSection.get(QUESTION_LIST));
            LOGGER.info(e.getMessage());
            throw new RuntimeException(e);
        }
        return masterQuestionnaireTemplateService.getMasterQuestionnaireTemplateWithSectionById(countryId, orgId, questionnaireTemplate.getId());

    }


    /**
     * @param countryId
     * @param orgId
     * @param masterQuestionnaireSectionDTOs contain list of sections ,and section list of questions
     * @return return map ,which contain list of section ,list of sections id and list of questions which is needed for rollback
     * @description method create new question sections belong to questionnaire template and and questions which belong to section.
     * and rollback if exception occur in questionnaire section service to delete inserted questions
     */
    public Map<String, Object> createQuestionnaireSectionAndCreateAndAddQuestions(Long countryId, Long orgId, List<MasterQuestionnaireSectionDTO> masterQuestionnaireSectionDTOs) {

        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> masterQuestionnaireSections = new ArrayList<>();
        List<MasterQuestion> questionList = new ArrayList<>();
        List<BigInteger> questionSectionIds = new ArrayList<>();
        checkForDuplicacyInTitleOfSections(masterQuestionnaireSectionDTOs);
        for (MasterQuestionnaireSectionDTO questionnaireSectionDto : masterQuestionnaireSectionDTOs) {
            MasterQuestionnaireSection questionnaireSection = new MasterQuestionnaireSection(questionnaireSectionDto.getTitle(), countryId);
            questionnaireSection.setOrganizationId(orgId);
            if (Optional.ofNullable(questionnaireSectionDto.getQuestions()).isPresent() && !questionnaireSectionDto.getQuestions().isEmpty()) {
                Map<String, Object> questions = masterQuestionService.addQuestionsToQuestionSection(countryId, orgId, questionnaireSectionDto.getQuestions());
                questionList = (List<MasterQuestion>) questions.get(QUESTION_LIST);
                questionnaireSection.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
            }
            masterQuestionnaireSections.add(questionnaireSection);
        }
        try {
            masterQuestionnaireSections = masterQuestionnaireSectionRepository.saveAll(getNextSequence(masterQuestionnaireSections));
            masterQuestionnaireSections.forEach(masterQuestionnaireSection -> {
                questionSectionIds.add(masterQuestionnaireSection.getId());
            });
        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, questionSectionIds);
        result.put(QUESTIONNAIRE_SECTIONS, masterQuestionnaireSections);
        result.put(QUESTION_LIST, questionList);
        return result;


    }


    public void checkForDuplicacyInTitleOfSections(List<MasterQuestionnaireSectionDTO> masterQuestionnaireSectionDTOs) {
        List<String> titles = new ArrayList<>();
        for (MasterQuestionnaireSectionDTO questionnaireSectionDto : masterQuestionnaireSectionDTOs) {
            if (titles.contains(questionnaireSectionDto.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "questionnaire section", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle().toLowerCase());
        }
    }


    /**
     *
     * @param countryId
     * @param orgId
     * @param id
     * @param templateId
     * @return
     */
    public Boolean deleteQuestionnaireSection(Long countryId, Long orgId, BigInteger id, BigInteger templateId) {
        MasterQuestionnaireSection questionnaireSection = masterQuestionnaireSectionRepository.findByIdAndNonDeleted(countryId, orgId, id);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire section", id);
        }
        MasterQuestionnaireTemplate questionnaireTemplate = masterQuestionnaireTemplateMongoRepository.findByIdAndNonDeleted(countryId, orgId, templateId);
        List<BigInteger> questionnaireTemplateSectionIdList = questionnaireTemplate.getSections();
        if (!questionnaireTemplateSectionIdList.contains(id)) {
            exceptionService.invalidRequestException("message.invalid", "section not present in template");
        }
        questionnaireTemplateSectionIdList.remove(id);
        questionnaireTemplate.setSections(questionnaireTemplateSectionIdList);
        if (!questionnaireSection.getQuestions().isEmpty()) {
            masterQuestionService.deleteAll(countryId, orgId, questionnaireSection.getQuestions());
        }
        delete(questionnaireSection);
        masterQuestionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return true;
    }


    /**
     * @param countryId
     * @param questionnaireSectionDto contain list of  existing sections and new sections,( section contain list of existing questions and new question)
     * @return return update master questionnaire template with sections qnd questions
     * @description this method update existing sections (if contain id) and create new sections(if not contain id in request)
     * and update existing questions if contain id in request and create new question if not contain id in request which belongs to section
     **/
    public Map<String, Object> updateExistingQuestionnaireSectionsAndCreateNewSectionsWithQuestions(Long countryId, Long orgId, List<MasterQuestionnaireSectionDTO> questionnaireSectionDto) {

        checkForDuplicacyInTitleOfSections(questionnaireSectionDto);
        List<MasterQuestionnaireSectionDTO> updateExistingSectionsList = new ArrayList<>();
        List<MasterQuestionnaireSectionDTO> createNewQuestionnaireSections = new ArrayList<>();

        questionnaireSectionDto.forEach(sectionDto -> {
            if (Optional.ofNullable(sectionDto.getId()).isPresent()) {
                updateExistingSectionsList.add(sectionDto);
            } else {
                createNewQuestionnaireSections.add(sectionDto);
            }
        });
        List<MasterQuestion> questions = new ArrayList<>();
        List<MasterQuestionnaireSection> sections = new ArrayList<>();

        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<String, Object> updatedSections = new HashMap<>(), newSections = new HashMap<>();
        if (updateExistingSectionsList.size() != 0) {
            updatedSections = updateQuestionnaireSectionAndQuestionList(countryId, orgId, updateExistingSectionsList);
            sectionsIds.addAll((List<BigInteger>) updatedSections.get(IDS_LIST));
            questions.addAll((List<MasterQuestion>) updatedSections.get(QUESTION_LIST));
            sections.addAll((List<MasterQuestionnaireSection>) updatedSections.get(QUESTIONNAIRE_SECTIONS));

        }
        if (!createNewQuestionnaireSections.isEmpty()) {
            newSections = createQuestionnaireSectionAndCreateAndAddQuestions(countryId, orgId, createNewQuestionnaireSections);
            sectionsIds.addAll((List<BigInteger>) newSections.get(IDS_LIST));
            questions.addAll((List<MasterQuestion>) newSections.get(QUESTION_LIST));
            sections.addAll((List<MasterQuestionnaireSection>) newSections.get(QUESTIONNAIRE_SECTIONS));

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
    public Map<String, Object> updateQuestionnaireSectionAndQuestionList(Long countryId, Long orgId, List<MasterQuestionnaireSectionDTO> updateSectionsAndQuestionsListDto) {

        List<MasterQuestionnaireSection> updateSectionsList = new ArrayList<>();
        List<BigInteger> sectionsIds = new ArrayList<>();
        Map<BigInteger, Object> sectionsDtoCorrespondingToId = new HashMap<>();

        updateSectionsAndQuestionsListDto.forEach(section -> {
            sectionsIds.add(section.getId());
            sectionsDtoCorrespondingToId.put(section.getId(), section);
        });
        List<MasterQuestion> questionList = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        List<MasterQuestionnaireSection> sections = masterQuestionnaireSectionRepository.getQuestionnaireSectionListByIds(countryId, orgId, sectionsIds);
        for (MasterQuestionnaireSection section : sections) {
            MasterQuestionnaireSectionDTO sectionDto = (MasterQuestionnaireSectionDTO) sectionsDtoCorrespondingToId.get(section.getId());
            if (Optional.ofNullable(sectionDto.getQuestions()).isPresent() && !sectionDto.getQuestions().isEmpty()) {
                Map<String, Object> questions = masterQuestionService.updateExistingQuestionAndCreateNewQuestions(countryId, orgId, sectionDto.getQuestions());
                section.setTitle(sectionDto.getTitle());
                section.setQuestions((List<BigInteger>) questions.get(IDS_LIST));
                questionList.addAll((List<MasterQuestion>) questions.get(QUESTION_LIST));
            }
            updateSectionsList.add(section);
        }

        try {
            updateSectionsList = masterQuestionnaireSectionRepository.saveAll(getNextSequence(updateSectionsList));

        } catch (MongoException e) {
            LOGGER.info(e.getMessage());
            masterQuestionMongoRepository.deleteAll(questionList);
            throw new MongoException(e.getMessage());
        }
        result.put(IDS_LIST, sectionsIds);
        result.put(QUESTIONNAIRE_SECTIONS, updateSectionsList);
        result.put(QUESTION_LIST, questionList);
        return result;

    }


    /**
     * @param countryId
     * @param orgId
     * @param sectionIdList
     * @return true on successful deletion on questionnaire sections and questions
     */
    public Boolean deleteAll(Long countryId, Long orgId, List<BigInteger> sectionIdList) {
        List<MasterQuestionnaireSection> questionnaireSections = masterQuestionnaireSectionRepository.getQuestionnaireSectionListByIds(countryId, orgId, sectionIdList);
        List<BigInteger> questionIdList = new ArrayList<>();
        questionnaireSections.forEach(masterQuestionnaireSection -> {
            masterQuestionnaireSection.setDeleted(true);
            questionIdList.addAll(masterQuestionnaireSection.getQuestions());
        });
        if (!questionIdList.isEmpty()) {
            masterQuestionService.deleteAll(countryId, orgId, questionIdList);
        }
        masterQuestionnaireSectionRepository.saveAll(questionnaireSections);
        return true;
    }


}
