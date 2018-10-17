package com.kairos.service.questionnaire_template;


import com.kairos.dto.gdpr.questionnaire_template.QuestionDTO;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateSectionDTO;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireSectionDTO;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireSectionRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


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

    @Inject
    private AssessmentMongoRepository assessmentMongoRepository;


    /**
     * @param countryId
     * @param templateId                    questionnaire template id ,required to fetch
     * @param masterQuestionnaireSectionDto contains list of sections ,And section contain list of questions
     * @return add sections ids to questionnaire template and return questionnaire template
     * @description questionnaireSection contain list of sections and list of sections ids.
     */
    public QuestionnaireTemplateResponseDTO addMasterQuestionnaireSectionToQuestionnaireTemplate(Long countryId, BigInteger templateId, QuestionnaireTemplateSectionDTO masterQuestionnaireSectionDto) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByCountryIdAndId(countryId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  template", templateId);
        }

        checkForDuplicacyInTitleOfSectionsAndQuestionTitle(masterQuestionnaireSectionDto.getSections());
        List<BigInteger> sectionIdList = createAndUpdateQuestionnaireSectionsAndQuestions(countryId, false, masterQuestionnaireSectionDto.getSections(), questionnaireTemplate.getTemplateType());
        questionnaireTemplate.setSections(sectionIdList);
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return questionnaireTemplateService.getMasterQuestionnaireTemplateWithSectionById(countryId, questionnaireTemplate.getId());

    }


    public List<BigInteger> createAndUpdateQuestionnaireSectionsAndQuestions(Long referenceId, boolean isReferenceIdUnitId, List<QuestionnaireSectionDTO> questionnaireSectionDTOS, QuestionnaireTemplateType templateType) {

        Map<BigInteger, QuestionnaireSectionDTO> questionnaireSectionIdDTOMap = new HashMap<>();
        Map<QuestionnaireSection, List<QuestionDTO>> questionDTOListCoresspondingToSection = new HashMap<>();
        List<QuestionnaireSection> globalQuestionnaireSections = new ArrayList<>();
        List<QuestionnaireSection> newQuestionnaireSections = new ArrayList<>();


        for (QuestionnaireSectionDTO questionnaireSectionDTO : questionnaireSectionDTOS) {
            if (Optional.ofNullable(questionnaireSectionDTO.getId()).isPresent()) {
                questionnaireSectionIdDTOMap.put(questionnaireSectionDTO.getId(), questionnaireSectionDTO);
            } else {
                QuestionnaireSection questionnaireSection = buildQuestionnaireSection(questionnaireSectionDTO, referenceId, isReferenceIdUnitId);
                newQuestionnaireSections.add(questionnaireSection);
                if (CollectionUtils.isNotEmpty(questionnaireSectionDTO.getQuestions())) {
                    questionDTOListCoresspondingToSection.put(questionnaireSection, questionnaireSectionDTO.getQuestions());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(questionnaireSectionIdDTOMap.keySet())) {
            List<QuestionnaireSection> previousQuestionnaireSections = isReferenceIdUnitId ? questionnaireSectionRepository.findSectionByUnitIdAndIds(referenceId, questionnaireSectionIdDTOMap.keySet()) : questionnaireSectionRepository.findSectionByCountryIdAndIds(referenceId, questionnaireSectionIdDTOMap.keySet());
            previousQuestionnaireSections.forEach(questionnaireSection -> {
                QuestionnaireSectionDTO questionnaireSectionDTO = questionnaireSectionIdDTOMap.get(questionnaireSection.getId());
                questionnaireSection.setTitle(questionnaireSectionDTO.getTitle());
                if (CollectionUtils.isNotEmpty(questionnaireSectionDTO.getQuestions())) {
                    questionDTOListCoresspondingToSection.put(questionnaireSection, questionnaireSectionDTO.getQuestions());
                }
            });
            globalQuestionnaireSections.addAll(previousQuestionnaireSections);
        }
        List<BigInteger> sectionIdList;
        if (!questionDTOListCoresspondingToSection.isEmpty()) {
            questionService.saveAndUpdateQuestionAndAddToQuestionnaireSection(referenceId, false, questionDTOListCoresspondingToSection, templateType);
            globalQuestionnaireSections.addAll(newQuestionnaireSections);
            sectionIdList = questionnaireSectionRepository.saveAll(getNextSequence(globalQuestionnaireSections)).stream().map(QuestionnaireSection::getId).collect(Collectors.toList());

        } else {
            globalQuestionnaireSections.addAll(newQuestionnaireSections);
            sectionIdList = questionnaireSectionRepository.saveAll(getNextSequence(globalQuestionnaireSections)).stream().map(QuestionnaireSection::getId).collect(Collectors.toList());
        }
        return sectionIdList;
    }


    private QuestionnaireSection buildQuestionnaireSection(QuestionnaireSectionDTO questionnaireSectionDTO, Long referenceId, boolean isReferenceIdUnitId) {
        if (isReferenceIdUnitId) {
            QuestionnaireSection questionnaireSection = new QuestionnaireSection(questionnaireSectionDTO.getTitle());
            questionnaireSection.setOrganizationId(referenceId);
            return questionnaireSection;
        } else {
            return new QuestionnaireSection(questionnaireSectionDTO.getTitle(), referenceId);
        }
    }

    /**
     * @param countryId
     * @param templateId
     * @param questionnaireSectionId
     * @return
     * @description soft delete section and remove section id from template
     */
    public boolean deleteMasterQuestionnaireSection(Long countryId, BigInteger templateId, BigInteger questionnaireSectionId) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByCountryIdAndId(countryId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  template", templateId);
        }
        questionnaireTemplate.getSections().remove(questionnaireSectionId);
        questionnaireSectionRepository.safeDelete(questionnaireSectionId);
        return true;
    }


    /**
     * @param unitId
     * @param questionnaireTemplateId
     * @param questionnaireSectionDTO
     * @return
     */
    public QuestionnaireTemplateResponseDTO createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplateOfUnit(Long unitId, BigInteger questionnaireTemplateId, QuestionnaireTemplateSectionDTO questionnaireSectionDTO) {

        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByUnitIdAndId(unitId, questionnaireTemplateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template", questionnaireTemplateId);
        }
        checkIfQuestionnaireTemplatePublishedOrLinkedWithAnyInProgressAssessment(unitId, questionnaireTemplateId, questionnaireTemplate, questionnaireSectionDTO);
        checkForDuplicacyInTitleOfSectionsAndQuestionTitle(questionnaireSectionDTO.getSections());
        List<BigInteger> sectionIdList = createAndUpdateQuestionnaireSectionsAndQuestions(unitId, true, questionnaireSectionDTO.getSections(), questionnaireTemplate.getTemplateType());
        questionnaireTemplate.setSections(sectionIdList);
        questionnaireTemplate.setTemplateStatus(questionnaireSectionDTO.getTemplateStatus());
        questionnaireTemplateMongoRepository.save(questionnaireTemplate);
        return questionnaireTemplateService.getQuestionnaireTemplateWithSectionByUnitIdAndId(unitId, questionnaireTemplate.getId());
    }

    private void checkIfQuestionnaireTemplatePublishedOrLinkedWithAnyInProgressAssessment(Long unitId, BigInteger questionnaireTemplateId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateSectionDTO questionnaireSectionDTO) {
        if (!Optional.ofNullable(questionnaireSectionDTO.getTemplateStatus()).isPresent()) {
            exceptionService.invalidRequestException("error.message.questionnaireTemplate.template.status.null");
        }
        if (QuestionnaireTemplateStatus.PUBLISHED.equals(questionnaireTemplate.getTemplateStatus())) {
            List<Assessment> inProgressAssessmentsLinkedWithQuestionnaireTemplate = assessmentMongoRepository.getAssessmentLinkedWithQuestionnaireTemplateByTemplateIdAndUnitId(unitId, questionnaireTemplateId);
            if (CollectionUtils.isNotEmpty(inProgressAssessmentsLinkedWithQuestionnaireTemplate)) {
                exceptionService.invalidRequestException("message.questionnaire.cannotbe.edit", new StringBuilder(inProgressAssessmentsLinkedWithQuestionnaireTemplate.stream().map(Assessment::getName).map(String::toString).collect(Collectors.joining(","))));
            }
        }
    }

    public boolean deleteQuestionnaireSectionByUnitId(Long unitId, BigInteger templateId, BigInteger questionnaireSectionId) {
        QuestionnaireTemplate questionnaireTemplate = questionnaireTemplateMongoRepository.findByUnitIdAndId(unitId, templateId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  template", templateId);
        }
        questionnaireTemplate.getSections().remove(questionnaireSectionId);
        questionnaireSectionRepository.safeDelete(questionnaireSectionId);
        return true;
    }


    private void checkForDuplicacyInTitleOfSectionsAndQuestionTitle(List<QuestionnaireSectionDTO> questionnaireSectionDTOs) {
        List<String> titles = new ArrayList<>();
        List<String> questionTitles = new ArrayList<>();
        for (QuestionnaireSectionDTO questionnaireSectionDto : questionnaireSectionDTOs) {
            for (QuestionDTO questionDTO : questionnaireSectionDto.getQuestions()) {
                if (questionTitles.contains(questionDTO.getQuestion().toLowerCase())) {
                    exceptionService.invalidRequestException("message.duplicate.question.questionnaire.section", questionDTO.getQuestion(), questionnaireSectionDto.getTitle());
                }
                questionTitles.add(questionDTO.getQuestion().toLowerCase());
            }
            if (titles.contains(questionnaireSectionDto.getTitle().toLowerCase())) {
                exceptionService.duplicateDataException("message.duplicate", "Questionnaire section", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle().toLowerCase());


        }
    }


}
