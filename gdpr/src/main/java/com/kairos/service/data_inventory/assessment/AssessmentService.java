package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.dto.gdpr.assessment.*;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.*;
import com.kairos.persistence.model.data_inventory.assessment.*;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.questionnaire_template.Question;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireSection;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.model.risk_management.Risk;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.common.AssessmentResponseDTO;
import com.kairos.response.dto.common.MetaDataResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionBasicResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AssessmentService {


    private static Logger logger = LoggerFactory.getLogger(AssessmentService.class);

    @Inject
    private ProcessingActivityRepository processingActivityRepository;
    @Inject
    private QuestionnaireTemplateRepository questionnaireTemplateRepository;
    @Inject
    private AssetRepository assetRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private ObjectMapper objectMapper;
    @Inject
    private GenericRestClient genericRestClient;
    @Inject
    private AssessmentRepository assessmentRepository;
    @Inject
    private AccessorPartyRepository accessorPartyRepository;
    @Inject
    private ResponsibilityTypeRepository responsibilityTypeRepository;
    @Inject
    private ProcessingPurposeRepository processingPurposeRepository;
    @Inject
    private DataSourceRepository dataSourceRepository;
    @Inject
    private HostingProviderRepository hostingProviderRepository;
    @Inject
    private DataDisposalRepository dataDisposalRepository;
    @Inject
    private OrganizationalSecurityMeasureRepository organizationalSecurityMeasureRepository;
    @Inject
    private TechnicalSecurityMeasureRepository technicalSecurityMeasureRepository;
    @Inject
    private HostingTypeRepository hostingTypeRepository;
    @Inject
    private StorageFormatRepository storageFormatRepository;
    @Inject
    private ProcessingLegalBasisRepository processingLegalBasisRepository;
    @Inject
    private TransferMethodRepository transferMethodRepository;
    @Inject
    private AssetTypeRepository assetTypeRepository;


    private static List<AssessmentStatus> assessmentStatusList = Arrays.asList(AssessmentStatus.NEW, AssessmentStatus.IN_PROGRESS);


    /**
     * @param unitId        organization id
     * @param assetId       asset id for which assessment is related
     * @param assessmentDTO Assessment Dto contain detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO launchAssessmentForAsset(Long unitId, Long assetId, AssessmentDTO assessmentDTO) {

        if(!Optional.ofNullable(assessmentDTO.getRelativeDeadlineDuration()).isPresent() || !Optional.ofNullable(assessmentDTO.getRelativeDeadlineType()).isPresent()){
            exceptionService.illegalArgumentException("message.assessment.relativeDeadline.require");
        }
        Assessment previousAssessment = assessmentDTO.isRiskAssessment() ? assessmentRepository.findPreviousLaunchedAssessmentByUnitIdAndAssetId(unitId, assetId, assessmentStatusList, true) : assessmentRepository.findPreviousLaunchedAssessmentByUnitIdAndAssetId(unitId, assetId, assessmentStatusList, false);
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.duplicateDataException("message.assessment.cannotbe.launched.asset", previousAssessment.getName(), previousAssessment.getAssessmentStatus());
        }
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeletedFalse(assetId, unitId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.asset", assetId);
        }
        validateRelativeDeadLineDate(assessmentDTO);
        assessmentDTO.setRiskAssociatedEntity(QuestionnaireTemplateType.ASSET_TYPE);
        Assessment assessment = assessmentDTO.isRiskAssessment() ? validateLaunchAssessment(unitId, assessmentDTO, QuestionnaireTemplateType.RISK, asset) : validateLaunchAssessment(unitId, assessmentDTO, QuestionnaireTemplateType.ASSET_TYPE, asset);
        assessment.setAsset(asset);
        if (!assessmentDTO.isRiskAssessment()) {
            mapEntityValueAsAssessmentAnswer(assessment, asset, null);
        } else {
            mapRiskValueAsAssessmentAnswer(assessment);
        }
        assessmentRepository.save(assessment);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
    }

    private boolean validateRelativeDeadLineDate(AssessmentDTO assessmentDTO) {
        boolean result = true;
        if (assessmentDTO.getRelativeDeadlineType().equals(DurationType.DAYS) && !(assessmentDTO.getRelativeDeadlineDuration() <= 30)) {
            result = false;
        } else if (assessmentDTO.getRelativeDeadlineType().equals(DurationType.HOURS) && !(assessmentDTO.getRelativeDeadlineDuration() <= 24)) {
            result = false;
        } else if (assessmentDTO.getRelativeDeadlineType().equals(DurationType.MONTHS) && !(assessmentDTO.getRelativeDeadlineDuration() <= 12)) {
            result = false;
        } else {
            LocalDate endDate = DateUtils.addDurationInLocalDate(assessmentDTO.getStartDate(), assessmentDTO.getRelativeDeadlineDuration(), assessmentDTO.getRelativeDeadlineType(), 1);
            if (endDate.isAfter(assessmentDTO.getEndDate())) {
                result = false;
            }
        }
        if (!result) {
            exceptionService.illegalArgumentException("message.assessment.relativedeadline.value.invalid");
        }
        return result;
    }

    /**
     * @param unitId
     * @param processingActivityId Processing activity id for which assessment is related
     * @param assessmentDTO        Assessment Dto contain detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO launchAssessmentForProcessingActivity(Long unitId, Long processingActivityId, AssessmentDTO assessmentDTO, boolean subProcessingActivity) {

        Assessment previousAssessment = assessmentDTO.isRiskAssessment() ? assessmentRepository.findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(unitId, processingActivityId, assessmentStatusList, true) : assessmentRepository.findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(unitId, processingActivityId, assessmentStatusList, false);
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.assessment.cannotbe.launched.processing.activity", previousAssessment.getName(), previousAssessment.getAssessmentStatus());
        }
        assessmentDTO.setRiskAssociatedEntity(QuestionnaireTemplateType.PROCESSING_ACTIVITY);
        ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeletedAndIsSubProcessingActivity(processingActivityId, unitId, false);
        try {
            Assessment assessment = assessmentDTO.isRiskAssessment() ? validateLaunchAssessment(unitId, assessmentDTO, QuestionnaireTemplateType.RISK, processingActivity) : validateLaunchAssessment(unitId, assessmentDTO, QuestionnaireTemplateType.PROCESSING_ACTIVITY, processingActivity);
            assessment.setProcessingActivity(processingActivity);
            if (!assessmentDTO.isRiskAssessment()) {
                mapEntityValueAsAssessmentAnswer(assessment, null, processingActivity);
            } else {
                mapRiskValueAsAssessmentAnswer(assessment);
            }
            assessmentRepository.save(assessment);
            assessmentDTO.setId(assessment.getId());
        } catch (EntityNotFoundException ene) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "message.processingActivity", processingActivityId);
        }
        return assessmentDTO;
    }


    /**
     * @param unitId
     * @param assessmentDTO
     * @return
     */
    private Assessment validateLaunchAssessment(Long unitId, AssessmentDTO assessmentDTO, QuestionnaireTemplateType templateType, Object entity) {

        Assessment previousAssessment = assessmentRepository.findByOrganizationIdAndDeletedAndName(unitId, assessmentDTO.getName());
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Assessment", assessmentDTO.getName());
        }
        if (assessmentDTO.getStartDate().isBefore(LocalDate.now())) {
            exceptionService.invalidRequestException("message.assessment.enter.valid.startdate");
        } else if (assessmentDTO.getEndDate().isBefore(LocalDate.now()) || assessmentDTO.getEndDate().isBefore(assessmentDTO.getStartDate())) {
            exceptionService.invalidRequestException("message.assessment.enter.valid.enddate");
        }
        Assessment assessment = new Assessment(assessmentDTO.getName(), assessmentDTO.getEndDate(), assessmentDTO.getComment(), assessmentDTO.getStartDate());
        assessment.setApprover(ObjectMapperUtils.copyPropertiesByMapper(assessmentDTO.getApprover(), com.kairos.persistence.model.embeddables.Staff.class));
        assessment.setAssigneeList(ObjectMapperUtils.copyPropertiesOfListByMapper(assessmentDTO.getAssigneeList(), com.kairos.persistence.model.embeddables.Staff.class));
        assessment.setOrganizationId(unitId);
        QuestionnaireTemplate questionnaireTemplate;
        switch (templateType) {
            case ASSET_TYPE:
                Asset asset = (Asset) entity;
                questionnaireTemplate = getPublishedQuestionnaireTemplateByUnitIdAndAssetTypeOrSubAssetType(unitId, asset);
                break;
            case RISK:
                questionnaireTemplate = getPublishedQuestionnaireTemplateByUnitIdAndAssociatedEntity(unitId, assessmentDTO, assessment, entity);
                break;
            case PROCESSING_ACTIVITY:
                questionnaireTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByProcessingActivityAndByUnitId(unitId, QuestionnaireTemplateType.PROCESSING_ACTIVITY, QuestionnaireTemplateType.PROCESSING_ACTIVITY, QuestionnaireTemplateStatus.PUBLISHED);
                break;
            default:
                questionnaireTemplate = questionnaireTemplateRepository.getQuestionnaireTemplateByTemplateTypeAndUnitId(templateType, unitId, QuestionnaireTemplateStatus.PUBLISHED);
                break;

        }
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            exceptionService.invalidRequestException("message.questionnaire.template.Not.Found.For.Template.Type", templateType);
        }
        /*if (AssessmentSchedulingFrequency.CUSTOM_DATE.equals(assessmentDTO.getAssessmentSchedulingFrequency())) {
            if (!Optional.ofNullable(assessmentDTO.getAssessmentLaunchedDate()).isPresent()) {
                exceptionService.invalidRequestException("message.assessment.scheduling.date.not.Selected");
            } else if (LocalDate.now().equals(assessmentDTO.getAssessmentLaunchedDate()) || assessmentDTO.getAssessmentLaunchedDate().isBefore(LocalDate.now())) {
                exceptionService.invalidRequestException("message.assessment.enter.valid.date");

            assessment.setAssessmentLaunchedDate(assessmentDTO.getAssessmentLaunchedDate());
        }}*/
        assessment.setAssessmentLaunchedDate(LocalDate.now());
        assessment.setAssessmentSchedulingFrequency(assessmentDTO.getAssessmentSchedulingFrequency());
        assessment.setQuestionnaireTemplate(questionnaireTemplate);
        return assessment;

    }


    private QuestionnaireTemplate getPublishedQuestionnaireTemplateByUnitIdAndAssetTypeOrSubAssetType(Long unitId, Asset asset) {
        QuestionnaireTemplate questionnaireTemplate;
        if (asset.getSubAssetType() != null) {
            questionnaireTemplate = questionnaireTemplateRepository.findPublishedQuestionnaireTemplateByUnitIdAndAssetTypeIdAndSubAssetTypeId(unitId, asset.getAssetType().getId(), asset.getSubAssetType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
        } else {
            questionnaireTemplate = questionnaireTemplateRepository.findQuestionnaireTemplateByUnitIdAssetTypeIdAndTemplateStatus(unitId, asset.getAssetType().getId(), QuestionnaireTemplateType.ASSET_TYPE, QuestionnaireTemplateStatus.PUBLISHED);
        }
        if (!Optional.ofNullable(questionnaireTemplate).isPresent()) {
            questionnaireTemplate = questionnaireTemplateRepository.getDefaultPublishedAssetQuestionnaireTemplateByUnitId(unitId);
        }

        return questionnaireTemplate;
    }


    /**
     * @param unitId
     * @param assessmentDTO
     * @param entity
     * @return
     */
    private QuestionnaireTemplate getPublishedQuestionnaireTemplateByUnitIdAndAssociatedEntity(Long unitId, AssessmentDTO assessmentDTO, Assessment assessment, Object entity) {
        List<Risk> risks = new ArrayList<>();
        QuestionnaireTemplate questionnaireTemplate = null;
        if (QuestionnaireTemplateType.ASSET_TYPE.equals(assessmentDTO.getRiskAssociatedEntity())) {
            Asset asset = (Asset) entity;
            risks = asset.getAssetType().getRisks();
            if (Optional.ofNullable(asset.getSubAssetType()).isPresent())
                risks.addAll(asset.getSubAssetType().getRisks());
            questionnaireTemplate = Optional.ofNullable(asset.getSubAssetType()).isPresent() ? questionnaireTemplateRepository.findPublishedRiskTemplateByOrgIdAndAssetTypeIdAndSubAssetTypeId(unitId, asset.getAssetType().getId(), asset.getSubAssetType().getId())
                    : questionnaireTemplateRepository.findPublishedRiskTemplateByAssetTypeIdAndOrgId(unitId, asset.getAssetType().getId());
        } else if (QuestionnaireTemplateType.PROCESSING_ACTIVITY.equals(assessmentDTO.getRiskAssociatedEntity())) {
            ProcessingActivity processingActivity = (ProcessingActivity) entity;
            risks.addAll(processingActivity.getRisks());
            questionnaireTemplate = questionnaireTemplateRepository.findPublishedRiskTemplateByAssociatedEntityAndOrgId(unitId, QuestionnaireTemplateType.PROCESSING_ACTIVITY);
        }
        if (CollectionUtils.isEmpty(risks)) {
            exceptionService.invalidRequestException("message.assessment.cannotbe.launched.risk.not.present");
        }
        assessment.setRisks(risks);
        assessment.setRiskAssessment(true);
        return questionnaireTemplate;
    }


    private void mapEntityValueAsAssessmentAnswer(Assessment assessment, Asset asset, ProcessingActivity processingActivity) {

        QuestionnaireTemplate questionnaireTemplateDTO = assessment.getQuestionnaireTemplate();
        List<AssessmentAnswer> assessmentAnswerVOS = new ArrayList<>();
        for (QuestionnaireSection questionnaireSection : questionnaireTemplateDTO.getSections()) {
            for (Question question : questionnaireSection.getQuestions()) {
                assessmentAnswerVOS.add(Optional.ofNullable(asset).isPresent() ? mapAssetValueAsAsessmentAnswer(asset, question) : mapProcessingActivityValueAsAssessmentAnswer(processingActivity, question));
            }
        }
        assessment.setAssessmentAnswers(assessmentAnswerVOS);
    }


    private void mapRiskValueAsAssessmentAnswer(Assessment assessment) {

        QuestionnaireTemplate questionnaireTemplate = assessment.getQuestionnaireTemplate();
        List<AssessmentAnswer> riskAssessmentAnswer = new ArrayList<>();
        for (QuestionnaireSection questionnaireSection : questionnaireTemplate.getSections()) {
            for (Question question : questionnaireSection.getQuestions()) {
                riskAssessmentAnswer.add(new AssessmentAnswer(question.getId(), null, null, question.getQuestionType()));
            }
        }
        assessment.setAssessmentAnswers(riskAssessmentAnswer);
    }


    private AssessmentAnswer mapAssetValueAsAsessmentAnswer(Asset asset, Question question) {
        AssetAttributeName assetAttributeName = AssetAttributeName.valueOf(question.getAttributeName());
        switch (assetAttributeName) {
            case NAME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(asset.getName()), question.getQuestionType());
            case DESCRIPTION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(asset.getDescription()), question.getQuestionType());
            case HOSTING_LOCATION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(asset.getHostingLocation()), question.getQuestionType());
            case HOSTING_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(asset.getHostingType()).isPresent() ? new MetaDataVO(asset.getHostingType().getId(), asset.getHostingType().getName()) : new MetaDataVO()), question.getQuestionType());
            case DATA_DISPOSAL:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(asset.getHostingProvider()).isPresent() ? new MetaDataVO(asset.getDataDisposal().getId(), asset.getDataDisposal().getName()) : new MetaDataVO()), question.getQuestionType());
            case HOSTING_PROVIDER:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(asset.getHostingProvider()).isPresent() ? new MetaDataVO(asset.getHostingProvider().getId(), asset.getHostingProvider().getName()) : new MetaDataVO()), question.getQuestionType());
            case ASSET_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(asset.getHostingProvider()).isPresent() ? new MetaDataVO(asset.getAssetType().getId(), asset.getAssetType().getName()) : new MetaDataVO()), question.getQuestionType());
            case STORAGE_FORMAT:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(asset.getStorageFormats().stream().map(storageFormat -> new MetaDataVO(storageFormat.getId(), storageFormat.getName())).collect(Collectors.toList())), question.getQuestionType());
            case ASSET_SUB_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(asset.getHostingProvider()).isPresent() ? new MetaDataVO(asset.getSubAssetType().getId(), asset.getSubAssetType().getName()) : new MetaDataVO()), question.getQuestionType());
            case TECHNICAL_SECURITY_MEASURES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(asset.getTechnicalSecurityMeasures().stream().map(technicalSecurityMeasure -> new MetaDataVO(technicalSecurityMeasure.getId(), technicalSecurityMeasure.getName())).collect(Collectors.toList())), question.getQuestionType());
            case ORGANIZATION_SECURITY_MEASURES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(asset.getOrgSecurityMeasures().stream().map(securityMeasure -> new MetaDataVO(securityMeasure.getId(), securityMeasure.getName())).collect(Collectors.toList())), question.getQuestionType());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(asset.getDataRetentionPeriod().toString()), question.getQuestionType());
            default:
                return null;
        }

    }

    private AssessmentAnswer mapProcessingActivityValueAsAssessmentAnswer(ProcessingActivity processingActivity, Question question) {

        ProcessingActivityAttributeName processingActivityAttributeName = ProcessingActivityAttributeName.valueOf(question.getAttributeName());
        switch (processingActivityAttributeName) {
            case NAME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getName()), question.getQuestionType());
            case DESCRIPTION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getDescription()), question.getQuestionType());
            case RESPONSIBILITY_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(),
                        new SingleSelectChoice(Optional.ofNullable(processingActivity.getResponsibilityType()).isPresent() ? new MetaDataVO(processingActivity.getResponsibilityType().getId(), processingActivity.getResponsibilityType().getName()) : new MetaDataVO()), question.getQuestionType());
            case ACCESSOR_PARTY:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(processingActivity.getAccessorParties().stream().map(o -> new MetaDataVO(o.getId(), o.getName())).collect(Collectors.toList())), question.getQuestionType());
            case PROCESSING_PURPOSES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(processingActivity.getProcessingPurposes().stream().map(o -> new MetaDataVO(o.getId(), o.getName())).collect(Collectors.toList())), question.getQuestionType());
            case PROCESSING_LEGAL_BASIS:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(processingActivity.getProcessingLegalBasis().stream().map(o -> new MetaDataVO(o.getId(), o.getName())).collect(Collectors.toList())), question.getQuestionType());
            case TRANSFER_METHOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(processingActivity.getTransferMethods().stream().map(o -> new MetaDataVO(o.getId(), o.getName())).collect(Collectors.toList())), question.getQuestionType());
            case DATA_SOURCES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new MultipleSelectChoice(processingActivity.getDataSources().stream().map(o -> new MetaDataVO(o.getId(), o.getName())).collect(Collectors.toList())), question.getQuestionType());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getDataRetentionPeriod().toString()), question.getQuestionType());
            case DPO_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getDpoContactInfo().toString()), question.getQuestionType());
            case CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getControllerContactInfo().toString()), question.getQuestionType());
            case MAX_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getMaxDataSubjectVolume().toString()), question.getQuestionType());
            case MIN_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getMinDataSubjectVolume().toString()), question.getQuestionType());
            case JOINT_CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), new TextChoice(processingActivity.getJointControllerContactInfo().toString()), question.getQuestionType());
            default:
                return null;
        }

    }


    /**
     * @param //unitId
     * @param //assessmentId
     * @return
     */
    //TODO
    public List<QuestionnaireSectionResponseDTO> getAssessmentByUnitIdAndId(Long unitId, Long assessmentId) {

        Assessment assessment = assessmentRepository.findByOrganizationIdAndId(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        QuestionnaireTemplate assessmentQuestionnaireTemplate = assessment.getQuestionnaireTemplate();
        List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections = ObjectMapperUtils.copyPropertiesOfListByMapper(assessmentQuestionnaireTemplate.getSections(), QuestionnaireSectionResponseDTO.class);
        if (assessment.isRiskAssessment()) {
            getRiskAssessmentAnswer(assessment, assessmentQuestionnaireSections);
        } else {
            mapAssessmentSelectedChoicesAndOptionsToQuestion(unitId, assessment, assessmentQuestionnaireSections);
        }
        return assessmentQuestionnaireSections;
    }


    private void getRiskAssessmentAnswer(Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        List<AssessmentAnswer> assessmentAnswers = assessment.getAssessmentAnswers();
        Map<Long, Object> riskAssessmentAnswer = new HashMap<>();
        assessmentAnswers.forEach(assessmentAnswer -> riskAssessmentAnswer.put(assessmentAnswer.getQuestionId(), assessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO question : questionnaireSectionResponseDTO.getQuestions()) {
                question.setValue(riskAssessmentAnswer.get(question.getId()));
            }
        }

    }


    private void mapAssessmentSelectedChoicesAndOptionsToQuestion(Long unitId, Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        List<AssessmentAnswer> assessmentAnswers = assessment.getAssessmentAnswers();
        Map<String, SelectedChoice> attributeAndChoicesMap = new HashMap<>();
        assessmentAnswers.forEach(answer -> attributeAndChoicesMap.put(answer.getAttributeName().trim(), answer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO question : questionnaireSectionResponseDTO.getQuestions()) {
                if (attributeAndChoicesMap.containsKey(question.getAttributeName())) {
                    if (QuestionType.MULTIPLE_CHOICE.equals(question.getQuestionType()) && !Optional.ofNullable(attributeAndChoicesMap.get(question.getAttributeName())).isPresent()) {
                        question.setValue(new ArrayList<>());
                    } else {
                        question.setValue(attributeAndChoicesMap.get(question.getAttributeName()));
                    }
                    question.setAssessmentAnswerChoices(Optional.ofNullable(assessment.getAsset()).isPresent() ? getAssessmentAnswerOptionsByUnitIdAndAssetAttributeName(unitId, AssetAttributeName.valueOf(question.getAttributeName()))
                            : getAssessmentAnswerOptionsByUnitIdAndProcessingActvityAttributeName(unitId, ProcessingActivityAttributeName.valueOf(question.getAttributeName())));
                }
            }
        }


    }

    private Object getAssessmentAnswerOptionsByUnitIdAndAssetAttributeName(Long unitId, AssetAttributeName assetAttributeName) {

        switch (assetAttributeName) {
            case HOSTING_PROVIDER:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(hostingProviderRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case HOSTING_TYPE:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(hostingTypeRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case ASSET_TYPE:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(assetTypeRepository.getAllAssetTypesByOrganization(unitId), MetaDataResponseDTO.class);
            case STORAGE_FORMAT:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(storageFormatRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case DATA_DISPOSAL:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(dataDisposalRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case TECHNICAL_SECURITY_MEASURES:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(technicalSecurityMeasureRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case ORGANIZATION_SECURITY_MEASURES:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(organizationalSecurityMeasureRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            default:
                return null;
        }


    }


    private Object getAssessmentAnswerOptionsByUnitIdAndProcessingActvityAttributeName(Long unitId, ProcessingActivityAttributeName processingActivityAttributeName) {


        switch (processingActivityAttributeName) {
            case RESPONSIBILITY_TYPE:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(responsibilityTypeRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case PROCESSING_PURPOSES:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(processingPurposeRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case DATA_SOURCES:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(dataSourceRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case TRANSFER_METHOD:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(transferMethodRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case ACCESSOR_PARTY:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(accessorPartyRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            case PROCESSING_LEGAL_BASIS:
                return ObjectMapperUtils.copyPropertiesOfListByMapper(processingLegalBasisRepository.findAllByOrganizationId(unitId), MetaDataResponseDTO.class);
            default:
                return null;
        }


    }


    public List<AssessmentAnswerDTO> saveAssessmentAnswerByUnitIdAndAssessmentId(Long unitId, Long assessmentId, List<AssessmentAnswerDTO> assessmentAnswerValueObjects, AssessmentStatus status) {
        Assessment assessment = assessmentRepository.findByOrganizationIdAndId(unitId, assessmentId);
        UserVO currentUser = new UserVO(UserContext.getUserDetails().getId(), UserContext.getUserDetails().getUserName(), UserContext.getUserDetails().getEmail(), UserContext.getUserDetails().getFirstName(), UserContext.getUserDetails().getLastName());
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        } else if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
            exceptionService.invalidRequestException("message.assessment.change.status", AssessmentStatus.IN_PROGRESS);
        } else if (assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
            exceptionService.invalidRequestException("message.assessment.completed.cannot.fill.answer");
        }
        validateAssessmentAnswer(assessment, assessmentAnswerValueObjects);
        if (Optional.ofNullable(status).isPresent() && AssessmentStatus.COMPLETED.equals(status)) {
            if (!currentUser.equals(assessment.getAssessmentLastAssistBy())) {
                exceptionService.invalidRequestException("message.notAuthorized.toChange.assessment.status");
            }
            assessment.setCompletedDate(LocalDate.now());
            mapAssessmentAnswerToAssetOrProcessingActivity(assessment);
        }
        assessment.setAssessmentStatus(status);
        assessmentRepository.save(assessment);
        return assessmentAnswerValueObjects;

    }


    private void validateAssessmentAnswer(Assessment assessment, List<AssessmentAnswerDTO> assessmentAnswers) {
        if (assessment.getAssessmentAnswers().size() != assessmentAnswers.size()) {
            exceptionService.invalidRequestException("message.assessment.answer.size.not.equal.to.requestdata");
        }
        Map<String, AssessmentAnswer> assessmentAnswerAttributeNameMap = assessment.getAssessmentAnswers().stream().collect(Collectors.toMap(AssessmentAnswer::getAttributeName, Function.identity()));
        try {
            assessmentAnswers.forEach(assessmentAnswer -> {
                AssessmentAnswer answer = assessmentAnswerAttributeNameMap.get(assessmentAnswer.getAttributeName());

                switch (assessmentAnswer.getQuestionType()) {
                    case TEXTBOX:
                        TextChoiceDTO textChoice = (TextChoiceDTO) assessmentAnswer.getValue();
                        answer.setValue(ObjectMapperUtils.copyPropertiesByMapper(textChoice, TextChoice.class));
                        break;
                    case MULTIPLE_CHOICE:
                        MultipleSelectChoiceDTO multipleSelectChoice = (MultipleSelectChoiceDTO) assessmentAnswer.getValue();
                        answer.setValue(ObjectMapperUtils.copyPropertiesByMapper(multipleSelectChoice, MultipleSelectChoice.class));
                        break;
                    case SELECT_BOX:
                        SingleSelectChoiceDTO singleSelectChoice = (SingleSelectChoiceDTO) assessmentAnswer.getValue();
                        answer.setValue(ObjectMapperUtils.copyPropertiesByMapper(singleSelectChoice, SingleSelectChoice.class));
                        break;
                    default:
                        break;
                }
            });
        } catch (ClassCastException e) {
            exceptionService.invalidRequestException("message.assessment.answer.classcastException.request.data.not.match");
        }

    }


    /**
     * @param unitId
     * @param assessmentId
     * @param assessmentStatus
     * @return
     */

    public boolean updateAssessmentStatus(Long unitId, Long assessmentId, AssessmentStatus assessmentStatus) {
        Assessment assessment = assessmentRepository.findByOrganizationIdAndId(unitId, assessmentId);
        UserVO currentUser = new UserVO(UserContext.getUserDetails().getId(), UserContext.getUserDetails().getUserName(), UserContext.getUserDetails().getEmail(), UserContext.getUserDetails().getFirstName(), UserContext.getUserDetails().getLastName());
        switch (assessmentStatus) {
            case IN_PROGRESS:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                }
                break;
            case COMPLETED:

                if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                } else if (!currentUser.equals(assessment.getAssessmentLastAssistBy())) {
                    exceptionService.invalidRequestException("message.notAuthorized.toChange.assessment.status");
                }
                mapAssessmentAnswerToAssetOrProcessingActivity(assessment);
                break;
            case NEW:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.IN_PROGRESS) || assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                }
                break;
        }
        assessment.setAssessmentLastAssistBy(currentUser);
        assessment.setAssessmentStatus(assessmentStatus);
        assessmentRepository.save(assessment);
        return true;
    }


    private void mapAssessmentAnswerToAssetOrProcessingActivity(Assessment assessment) {

        if (!assessment.isRiskAssessment() && Optional.ofNullable(assessment.getAsset()).isPresent()) {
            Asset asset = assessment.getAsset();
            List<AssessmentAnswer> assessmentAnswersForAsset = assessment.getAssessmentAnswers();
            assessmentAnswersForAsset.forEach(assetAssessmentAnswer -> {
                if (Optional.ofNullable(assetAssessmentAnswer.getAttributeName()).isPresent()) {
                    saveAssessmentAnswerAsAssetValueOnCompletionOfAssessment(AssetAttributeName.valueOf(assetAssessmentAnswer.getAttributeName()), assetAssessmentAnswer.getValue(), asset);
                } else {
                    exceptionService.invalidRequestException("message.assessment.answer.attribute.null");
                }
            });
            assetRepository.save(asset);
        } else if (!assessment.isRiskAssessment() && Optional.ofNullable(assessment.getProcessingActivity()).isPresent()) {
            ProcessingActivity processingActivity = assessment.getProcessingActivity();
            List<AssessmentAnswer> assessmentAnswersForProcessingActivity = assessment.getAssessmentAnswers();
            assessmentAnswersForProcessingActivity.forEach(processingActivityAssessmentAnswer
                    -> {
                if (Optional.ofNullable(processingActivityAssessmentAnswer.getAttributeName()).isPresent()) {
                    saveAssessmentAnswerAsProcessingActivityValueOnCompletionOfAssessment(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentAnswer.getAttributeName()), processingActivityAssessmentAnswer.getValue(), processingActivity);
                } else {
                    exceptionService.invalidRequestException("message.assessment.answer.attribute.null");

                }
            });
            processingActivityRepository.save(processingActivity);

        }
    }

    /**
     * @param unitId
     * @return
     */
    public List<AssessmentBasicResponseDTO> getAllLaunchedAssessmentOfCurrentLoginUser(Long unitId) {

        Long staffId = genericRestClient.publishRequest(null, unitId, true, IntegrationOperation.GET, "/user/staffId", null, new ParameterizedTypeReference<RestTemplateResponseEnvelope<Long>>() {
        });
        List<Assessment> assessments = assessmentRepository.getAllAssessmentByUnitIdAndStaffId(unitId, staffId, assessmentStatusList);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(assessments, AssessmentBasicResponseDTO.class);
    }


    public List<AssessmentResponseDTO> getAllAssessmentByUnitId(Long unitId) {
        List<Assessment> assessments = assessmentRepository.getAllAssessmentByUnitId(unitId);
        return ObjectMapperUtils.copyPropertiesOfListByMapper(assessments, AssessmentResponseDTO.class);
    }

    public AssessmentSchedulingFrequency[] getSchedulingFrequency() {
        return AssessmentSchedulingFrequency.values();
    }

    public boolean deleteAssessmentById(Long unitId, Long assessmentId) {

        Assessment assessment = assessmentRepository.findByUnitIdAndIdAndAssessmentStatus(unitId, assessmentId, AssessmentStatus.IN_PROGRESS);
        if (Optional.ofNullable(assessment).isPresent()) {
            exceptionService.invalidRequestException("message.assessment.inprogress.cannot.delete", assessment.getName());
        }
        assessment.delete();
        assessmentRepository.save(assessment);
        return true;
    }


    public void saveAssessmentAnswerAsAssetValueOnCompletionOfAssessment(AssetAttributeName assetAttributeName, SelectedChoice selectedChoice, Asset asset) {
        switch (assetAttributeName) {
            case NAME:
                asset.setName(((TextChoice) selectedChoice).getTextChoice().trim());
                break;
            case DESCRIPTION:
                asset.setDescription(((TextChoice) selectedChoice).getTextChoice().trim());
                break;
            case HOSTING_LOCATION:
                asset.setHostingLocation(((TextChoice) selectedChoice).getTextChoice().trim());
                break;
            case HOSTING_TYPE:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO -> asset.setHostingType(hostingTypeRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case DATA_DISPOSAL:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO ->asset.setDataDisposal(dataDisposalRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case HOSTING_PROVIDER:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO -> asset.setHostingProvider(hostingProviderRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case ASSET_TYPE:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO -> asset.setAssetType(assetTypeRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case STORAGE_FORMAT:
                asset.setStorageFormats(storageFormatRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case ASSET_SUB_TYPE:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO -> asset.setSubAssetType(assetTypeRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case TECHNICAL_SECURITY_MEASURES:
                asset.setTechnicalSecurityMeasures(technicalSecurityMeasureRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case ORGANIZATION_SECURITY_MEASURES:
                asset.setOrgSecurityMeasures(organizationalSecurityMeasureRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case DATA_RETENTION_PERIOD:
                asset.setDataRetentionPeriod(Integer.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;

        }
    }


    public void saveAssessmentAnswerAsProcessingActivityValueOnCompletionOfAssessment(ProcessingActivityAttributeName processingActivityAttributeName, SelectedChoice selectedChoice, ProcessingActivity processingActivity) {
        switch (processingActivityAttributeName) {
            case NAME:
                processingActivity.setName(((TextChoice) selectedChoice).getTextChoice().trim());
                break;
            case DESCRIPTION:
                processingActivity.setDescription(((TextChoice) selectedChoice).getTextChoice().trim());
                break;
            case RESPONSIBILITY_TYPE:
                Optional.ofNullable(((SingleSelectChoice) selectedChoice).getSelectedChoice()).ifPresent(metaDataVO ->processingActivity.setResponsibilityType(responsibilityTypeRepository.findByIdAndDeletedFalse(metaDataVO.getMetadataId())));
                break;
            case ACCESSOR_PARTY:
                processingActivity.setAccessorParties(accessorPartyRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case PROCESSING_PURPOSES:
                processingActivity.setProcessingPurposes(processingPurposeRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case PROCESSING_LEGAL_BASIS:
                processingActivity.setProcessingLegalBasis(processingLegalBasisRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case TRANSFER_METHOD:
                processingActivity.setTransferMethods(transferMethodRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case DATA_SOURCES:
                processingActivity.setDataSources(dataSourceRepository.findAllByIds(((MultipleSelectChoice) selectedChoice).getSelectedChoice().stream().map(MetaDataVO::getMetadataId).collect(Collectors.toList())));
                break;
            case DATA_RETENTION_PERIOD:
                processingActivity.setDataRetentionPeriod(Integer.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
            case MAX_DATA_SUBJECT_VOLUME:
                processingActivity.setMaxDataSubjectVolume(Long.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
            case MIN_DATA_SUBJECT_VOLUME:
                processingActivity.setMinDataSubjectVolume(Long.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
            case JOINT_CONTROLLER_CONTACT_INFO:
                processingActivity.setJointControllerContactInfo(Integer.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
            case CONTROLLER_CONTACT_INFO:
                processingActivity.setJointControllerContactInfo(Integer.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
            case DPO_CONTACT_INFO:
                processingActivity.setDpoContactInfo(Integer.valueOf(((TextChoice) selectedChoice).getTextChoice().trim()));
                break;
        }
    }

}
