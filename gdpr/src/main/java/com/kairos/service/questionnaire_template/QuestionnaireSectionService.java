package com.kairos.service.questionnaire_template;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.questionnaire_template.QuestionDTO;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateSectionDTO;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireSectionDTO;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.questionnaire_template.*;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentRepository;
import com.kairos.persistence.repository.questionnaire_template.*;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class QuestionnaireSectionService {

    private Logger LOGGER = LoggerFactory.getLogger(QuestionnaireSectionService.class);


    @Inject
    private QuestionnaireSectionRepository questionnaireSectionRepository;

    @Inject
    private QuestionnaireTemplateRepository questionnaireTemplateRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionService questionService;

    @Inject
    private QuestionnaireTemplateService questionnaireTemplateService;

    @Inject
    private AssessmentRepository assessmentRepository;


    /**
     * @param referenceId
     * @param templateId  questionnaire template id ,required to fetch
     * @return add sections ids to questionnaire template and return questionnaire template
     * @description questionnaireSection contain list of sections and list of sections ids.
     */
    public QuestionnaireTemplateResponseDTO createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplate(Long referenceId, Long templateId, QuestionnaireTemplateSectionDTO templateSectionDTO, boolean isUnitId) {
        QuestionnaireTemplate questionnaireTemplate = isUnitId ? questionnaireTemplateRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : questionnaireTemplateRepository.findByIdAndCountryIdAndDeletedFalse(templateId, referenceId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", templateId);
        }

        checkForDuplicacyInTitleOfSectionsAndQuestionTitle(templateSectionDTO.getSections());
        if (isUnitId)
            checkDuplicateQuestionnaireTemplateOrTemplateLinkedWithAnyInProgressAssessment(referenceId, templateId, questionnaireTemplate, templateSectionDTO.getTemplateStatus());
        questionnaireTemplate.setSections(buildQuestionnaireSections(referenceId, isUnitId, templateSectionDTO.getSections(), questionnaireTemplate.getTemplateType()));
        questionnaireTemplate.setTemplateStatus(templateSectionDTO.getTemplateStatus());
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return ObjectMapperUtils.copyPropertiesByMapper(questionnaireTemplate, QuestionnaireTemplateResponseDTO.class);
    }


    private List<QuestionnaireSection> buildQuestionnaireSections(Long referenceId, boolean isUnitId, List<QuestionnaireSectionDTO> questionnaireSectionDTOS, QuestionnaireTemplateType templateType) {

        List<QuestionnaireSection> questionnaireSections = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionnaireSectionDTOS)) {
            questionnaireSectionDTOS.forEach(questionnaireSectionDTO ->
                    {
                        QuestionnaireSection questionnaireSection;
                        if (Optional.ofNullable(questionnaireSectionDTO.getId()).isPresent()) {
                            questionnaireSection = ObjectMapperUtils.copyPropertiesByMapper(questionnaireSectionDTO, QuestionnaireSection.class);
                        } else {
                            questionnaireSection = isUnitId ? new QuestionnaireSection(questionnaireSectionDTO.getTitle(), null, referenceId)
                                    : new QuestionnaireSection(questionnaireSectionDTO.getTitle(), referenceId, null);
                        }
                        questionnaireSection.setQuestions(buildQuestionsAndUpdate(referenceId, isUnitId, questionnaireSectionDTO.getQuestions(), templateType));
                        questionnaireSections.add(questionnaireSection);
                    }

            );
        }
        return questionnaireSections;
    }


    private List<Question> buildQuestionsAndUpdate(Long referenceId, boolean isUnitId, List<QuestionDTO> questionDTOS, QuestionnaireTemplateType templateType) {
        List<Question> questions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionDTOS)) {
            questionDTOS.forEach(questionDTO -> {

                Question question;
                if (Optional.ofNullable(questionDTO.getId()).isPresent()) {
                    question = ObjectMapperUtils.copyPropertiesByMapper(questionDTO, Question.class);
                } else {
                    question = isUnitId ? new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed(), null, referenceId)
                            : new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed(), referenceId, null);
                }
                addAttributeNameToQuestion(question, questionDTO.getAttributeName(), templateType);
                questions.add(question);
            });
        }
        return questions;
    }


    private void addAttributeNameToQuestion(Question question, String attributeName, QuestionnaireTemplateType templateType) {

        if (!Optional.ofNullable(templateType).isPresent()) {
            exceptionService.invalidRequestException("message.invalid.request", " Attribute name is incorrect");
        }
        switch (templateType) {
            case ASSET_TYPE:
                if (!Optional.ofNullable(AssetAttributeName.valueOf(attributeName).value).isPresent()) {
                    exceptionService.invalidRequestException("Attribute not found for Asset ");
                }
                break;
            case PROCESSING_ACTIVITY:
                if (!Optional.ofNullable(ProcessingActivityAttributeName.valueOf(attributeName).value).isPresent()) {
                    exceptionService.invalidRequestException("Attribute not found for Asset ");
                }
                break;
        }
        question.setAttributeName(attributeName);
    }

      public boolean deleteQuestionnaireSectionFromTemplate( Long templateId, Long questionnaireSectionId) {
          QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findByIdAndDeletedFalse(questionnaireSectionId);
          if (!Optional.ofNullable(questionnaireSection).isPresent()) {
              exceptionService.dataNotFoundByIdException("message.dataNotFound", "questionnaire  section", templateId);
          }
          questionnaireSectionRepository.deleteById(questionnaireSectionId);
          return true;
      }


    private void checkDuplicateQuestionnaireTemplateOrTemplateLinkedWithAnyInProgressAssessment(Long unitId, Long questionnaireTemplateId, QuestionnaireTemplate questionnaireTemplate, QuestionnaireTemplateStatus templateStatus) {
        if (!Optional.ofNullable(templateStatus).isPresent()) {
            exceptionService.invalidRequestException("error.message.questionnaireTemplate.template.status.null");
        }
        if (QuestionnaireTemplateStatus.PUBLISHED.equals(questionnaireTemplate.getTemplateStatus())) {
            List<Assessment> inProgressAssessmentsLinkedWithQuestionnaireTemplate = assessmentRepository.findByOrgIdAndQuestionnaireTemplateIdAndAssessmentStatusNewOrInProgress(unitId, questionnaireTemplateId);
            if (CollectionUtils.isNotEmpty(inProgressAssessmentsLinkedWithQuestionnaireTemplate)) {
                exceptionService.invalidRequestException("message.questionnaire.cannotbe.edit", new StringBuilder(inProgressAssessmentsLinkedWithQuestionnaireTemplate.stream().map(Assessment::getName).map(String::toString).collect(Collectors.joining(","))));
            }
        }
        switch (questionnaireTemplate.getTemplateType()) {
            case ASSET_TYPE:
                checkIfQuestionnaireTemplateOfAssetTypeIsInPublishedState(unitId, questionnaireTemplate);
                break;
            case RISK:
                checkIfRiskQuestionnaireTemplateIsInPublishedState(unitId, questionnaireTemplate);
                break;
            default:
                QuestionnaireTemplate previousTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByOrganizationIdAndTemplateType(unitId, questionnaireTemplate.getTemplateType(), QuestionnaireTemplateStatus.PUBLISHED);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("duplicate.questionnaireTemplate.ofTemplateType", questionnaireTemplate.getTemplateType());
                }
                break;
        }

    }


    // to check if already a questionnaire tepmlate of same asset type is present in published state , duplicate template with same asset type and risk in draft state is accepted
    private void checkIfQuestionnaireTemplateOfAssetTypeIsInPublishedState(Long unitId, QuestionnaireTemplate questionnaireTemplate) {

        QuestionnaireTemplate previousTemplate;
        if (questionnaireTemplate.isDefaultAssetTemplate()) {
            previousTemplate = questionnaireTemplateRepository.getDefaultPublishedAssetQuestionnaireTemplateByUnitId(unitId);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
        } else {
            if (Optional.ofNullable(questionnaireTemplate.getAssetSubType()).isPresent()) {
                previousTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(unitId, questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getAssetSubType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName());
                }
            } else {
                previousTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByUnitIdAssetTypeIdAndTemplateStatus(unitId, questionnaireTemplate.getAssetType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName());
                }
            }
        }
    }

    private void checkIfRiskQuestionnaireTemplateIsInPublishedState(Long unitId, QuestionnaireTemplate questionnaireTemplate) {
        QuestionnaireTemplate previousTemplate = null;
        switch (questionnaireTemplate.getRiskAssociatedEntity()) {
            case ASSET_TYPE:
                if (Optional.ofNullable(questionnaireTemplate.getAssetSubType()).isPresent()) {
                    previousTemplate = questionnaireTemplateRepository.findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(unitId, questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getAssetSubType().getId());
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName());
                    }
                } else {
                    previousTemplate = questionnaireTemplateRepository.findPublishedRiskTemplateByAssetTypeIdAndOrgId(unitId, questionnaireTemplate.getAssetType().getId());
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName());
                    }
                }
                break;
            case PROCESSING_ACTIVITY:
                previousTemplate = questionnaireTemplateRepository.findPublishedRiskTemplateByAssociatedEntityAndOrgId(unitId,QuestionnaireTemplateType.PROCESSING_ACTIVITY);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("duplicate.questionnaireTemplate.ofTemplateType", questionnaireTemplate.getTemplateType().value);
                }
                break;
        }
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
