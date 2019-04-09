package com.kairos.service.questionnaire_template;


import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.questionnaire_template.QuestionDTO;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireTemplateSectionDTO;
import com.kairos.enums.gdpr.*;
import com.kairos.dto.gdpr.questionnaire_template.QuestionnaireSectionDTO;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.questionnaire_template.*;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentRepository;
import com.kairos.persistence.repository.questionnaire_template.*;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Field;
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
    public QuestionnaireTemplateResponseDTO createOrUpdateQuestionnaireSectionAndAddToQuestionnaireTemplate(Long referenceId, Long templateId, QuestionnaireTemplateSectionDTO templateSectionDTO, boolean isOrganization) {
        if (isOrganization) {
            List<String> assessmentNames = assessmentRepository.findAllNamesByUnitIdQuestionnaireTemplateIdAndStatus(referenceId, templateId, AssessmentStatus.IN_PROGRESS);
            if (CollectionUtils.isNotEmpty(assessmentNames)) {
                exceptionService.invalidRequestException("message.cannot.update.questionnaireTemplate.inProgress.assessment.linked", StringUtils.join(assessmentNames, ","));
            }
        }
        QuestionnaireTemplate questionnaireTemplate = isOrganization ? questionnaireTemplateRepository.findByIdAndOrganizationIdAndDeletedFalse(templateId, referenceId) : questionnaireTemplateRepository.findByIdAndCountryIdAndDeletedFalse(templateId, referenceId);
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireTemplate", templateId);
        }
        checkForDuplicacyInTitleOfSectionsAndQuestionTitle(templateSectionDTO.getSections());
        if (isOrganization)
            checkDuplicateQuestionnaireTemplateOrTemplateLinkedWithAnyInProgressAssessment(referenceId, templateId, questionnaireTemplate, templateSectionDTO.getTemplateStatus());
        questionnaireTemplate.setSections(buildQuestionnaireSections(referenceId, isOrganization, templateSectionDTO.getSections(), questionnaireTemplate.getTemplateType()));
        questionnaireTemplate.setTemplateStatus(templateSectionDTO.getTemplateStatus());
        questionnaireTemplateRepository.save(questionnaireTemplate);
        return questionnaireTemplateService.getQuestionnaireTemplateWithSectionsByTemplateIdAndCountryIdOrOrganisationId(referenceId, questionnaireTemplate.getId(), isOrganization);
    }


    private List<QuestionnaireSection> buildQuestionnaireSections(Long referenceId, boolean isOrganization, List<QuestionnaireSectionDTO> questionnaireSectionDTOS, QuestionnaireTemplateType templateType) {

        List<QuestionnaireSection> questionnaireSections = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionnaireSectionDTOS)) {
            questionnaireSectionDTOS.forEach(questionnaireSectionDTO ->
                    {
                        QuestionnaireSection questionnaireSection;
                        if (Optional.ofNullable(questionnaireSectionDTO.getId()).isPresent()) {
                            questionnaireSection = ObjectMapperUtils.copyPropertiesByMapper(questionnaireSectionDTO, QuestionnaireSection.class);
                        } else {
                            questionnaireSection = isOrganization ? new QuestionnaireSection(questionnaireSectionDTO.getTitle(), null, referenceId)
                                    : new QuestionnaireSection(questionnaireSectionDTO.getTitle(), referenceId, null);
                        }
                        questionnaireSection.setQuestions(buildQuestionsAndUpdate(referenceId, isOrganization, questionnaireSectionDTO.getQuestions(), templateType));
                        questionnaireSections.add(questionnaireSection);
                    }

            );
        }
        return questionnaireSections;
    }


    private List<Question> buildQuestionsAndUpdate(Long referenceId, boolean isOrganization, List<QuestionDTO> questionDTOS, QuestionnaireTemplateType templateType) {
        List<Question> questions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(questionDTOS)) {
            questionDTOS.forEach(questionDTO -> {

                Question question;
                if (Optional.ofNullable(questionDTO.getId()).isPresent()) {
                    question = ObjectMapperUtils.copyPropertiesByMapper(questionDTO, Question.class);
                } else {
                    question = isOrganization ? new Question(questionDTO.getQuestion(), questionDTO.getDescription(), questionDTO.isRequired(), questionDTO.getQuestionType(), questionDTO.isNotSureAllowed(), null, referenceId)
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
        try {
            Class aClass = null;
            switch (templateType) {
                case ASSET_TYPE:
                    if (!Optional.ofNullable(AssetAttributeName.valueOf(attributeName).value).isPresent()) {
                        exceptionService.invalidRequestException("Attribute not found for Asset ");
                    }
                    aClass = Asset.class.getDeclaredField(AssetAttributeName.valueOf(attributeName).value).getType();
                    break;
                case PROCESSING_ACTIVITY:
                    if (!Optional.ofNullable(ProcessingActivityAttributeName.valueOf(attributeName).value).isPresent()) {
                        exceptionService.invalidRequestException("Attribute not found for Asset ");
                    }
                    aClass = ProcessingActivity.class.getDeclaredField(ProcessingActivityAttributeName.valueOf(attributeName).value).getType();
                    break;
            }
            boolean inValidQuestionType=false;
            if (List.class.equals(aClass) && !question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                inValidQuestionType=true;
            } else if ((String.class.equals(aClass) || Integer.class.equals(aClass)) && !question.getQuestionType().equals(QuestionType.TEXTBOX)) {
                inValidQuestionType=true;
            } else if (Boolean.class.equals(aClass) && !question.getQuestionType().equals(QuestionType.YES_NO_MAYBE)) {
                inValidQuestionType=true;
            } else if (!question.getQuestionType().equals(QuestionType.SELECT_BOX)) {
                inValidQuestionType=true;
            }
            if (inValidQuestionType)
            {
                exceptionService.illegalArgumentException("message.invalid.question.type.selected",question.getAttributeName());
            }
        } catch (NoSuchFieldException e) {
            LOGGER.debug("No such field Exception error in method addAttributeNameToQuestion");
            exceptionService.unsupportedOperationException("message.invalid.request");
        }
        question.setAttributeName(attributeName);
    }


    public boolean deleteQuestionnaireSectionFromTemplate(boolean isOrganizationId, Long referenceId, Long templateId, Long questionnaireSectionId) {
        if (isOrganizationId) {
            List<String> assessmentNames = assessmentRepository.findAllNamesByUnitIdQuestionnaireTemplateIdAndStatus(referenceId, templateId, AssessmentStatus.IN_PROGRESS);
            if (CollectionUtils.isNotEmpty(assessmentNames)) {
                exceptionService.invalidRequestException("message.cannot.update.questionnaireTemplate.inProgress.assessment.linked", StringUtils.join(assessmentNames, ","));
            }
        }
        QuestionnaireSection questionnaireSection = questionnaireSectionRepository.findByIdAndDeletedFalse(questionnaireSectionId);
        if (!Optional.ofNullable(questionnaireSection).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.questionnaireSection", questionnaireSectionId);
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


    // to check if already a questionnaire template of same asset type is present in published state , duplicate template with same asset type and risk in draft state is accepted
    private void checkIfQuestionnaireTemplateOfAssetTypeIsInPublishedState(Long unitId, QuestionnaireTemplate questionnaireTemplate) {

        QuestionnaireTemplate previousTemplate;
        if (questionnaireTemplate.isDefaultAssetTemplate()) {
            previousTemplate = questionnaireTemplateRepository.findDefaultTemplateByUnitIdAndTemplateTypeAndStatus(unitId, QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
            if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                exceptionService.duplicateDataException("duplicate.questionnaire.template.assetType.defaultTemplate");
            }
        } else {
            if (Optional.ofNullable(questionnaireTemplate.getSubAssetType()).isPresent()) {
                previousTemplate = questionnaireTemplateRepository.findTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeIdTemplateTypeAndStatus(unitId, questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getSubAssetType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
                if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                    exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName());
                }
            } else {
                previousTemplate = questionnaireTemplateRepository.findTemplateByUnitIdAssetTypeIdAndTemplateTypeAndTemplateStatus(unitId, questionnaireTemplate.getAssetType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
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
                if (Optional.ofNullable(questionnaireTemplate.getSubAssetType()).isPresent()) {
                    previousTemplate = questionnaireTemplateRepository.findTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeIdTemplateTypeAndStatus(unitId, questionnaireTemplate.getAssetType().getId(), questionnaireTemplate.getSubAssetType().getId(), QuestionnaireTemplateType.RISK, QuestionnaireTemplateStatus.PUBLISHED);
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType.subType", previousTemplate.getName());
                    }
                } else {
                    previousTemplate = questionnaireTemplateRepository.findTemplateByUnitIdAssetTypeIdAndTemplateTypeAndTemplateStatus(unitId, questionnaireTemplate.getAssetType().getId(), QuestionnaireTemplateType.RISK, QuestionnaireTemplateStatus.PUBLISHED);
                    if (Optional.ofNullable(previousTemplate).isPresent() && !previousTemplate.getId().equals(questionnaireTemplate.getId())) {
                        exceptionService.duplicateDataException("message.duplicate.questionnaireTemplate.assetType", previousTemplate.getName());
                    }
                }
                break;
            case PROCESSING_ACTIVITY:
                previousTemplate = questionnaireTemplateRepository.findTemplateByUnitIdAndRiskAssociatedEntityAndTemplateTypeAndStatus(unitId, QuestionnaireTemplateType.RISK, QuestionnaireTemplateType.PROCESSING_ACTIVITY, QuestionnaireTemplateStatus.PUBLISHED);
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
                exceptionService.duplicateDataException("message.duplicate", "message.questionnaireSection", questionnaireSectionDto.getTitle());
            }
            titles.add(questionnaireSectionDto.getTitle().toLowerCase());


        }
    }


}
