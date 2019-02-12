package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.commons.client.RestTemplateResponseEnvelope;
import com.kairos.commons.utils.DateUtils;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.enums.DurationType;
import com.kairos.enums.IntegrationOperation;
import com.kairos.enums.gdpr.*;
import com.kairos.dto.gdpr.assessment.AssessmentDTO;
import com.kairos.persistence.model.data_inventory.assessment.*;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.embeddables.ManagingOrganization;
import com.kairos.persistence.model.embeddables.Staff;
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
import com.kairos.rest_client.GenericRestClient;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.user_context.UserContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;

@Service
public class AssessmentService {


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
        if (!Optional.ofNullable(assessmentDTO.getRelativeDeadlineDuration()).isPresent() || !Optional.ofNullable(assessmentDTO.getRelativeDeadlineType()).isPresent()) {
            exceptionService.illegalArgumentException("message.assessment.relativedeadline.require");
        }
        Assessment previousAssessment = assessmentDTO.isRiskAssessment() ? assessmentRepository.findPreviousLaunchedAssessmentByUnitIdAndAssetId(unitId, assetId, assessmentStatusList, true) : assessmentRepository.findPreviousLaunchedAssessmentByUnitIdAndAssetId(unitId, assetId, assessmentStatusList, false);
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.duplicateDataException("message.assessment.cannotbe.launched.asset", previousAssessment.getName(), previousAssessment.getAssessmentStatus());
        }
        Asset asset = assetRepository.findByIdAndOrganizationIdAndDeleted(assetId, unitId);
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
            //saveRiskTemplateAnswerToAssessment(unitId, assessment);
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
                //  saveRiskTemplateAnswerToAssessment(unitId, assessment);
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
        } else if (QuestionnaireTemplateStatus.DRAFT.equals(questionnaireTemplate.getTemplateStatus())) {
            exceptionService.invalidRequestException("message.assessment.cannotbe.launched.questionnaireTemplate.notPublished");
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


    /**
     * @param //unitId
     * @param //assessmentId
     * @return
     */
    //TODO
    /*public List<QuestionnaireSectionResponseDTO> getAssessmentById(Long unitId, BigInteger assessmentId) {

        Assessment assessment = assessmentMongoRepository.findByUnitIdAndId(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        QuestionnaireTemplateResponseDTO assessmentQuestionnaireTemplate = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, assessment.getQuestionnaireTemplateId());
        List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections = assessmentQuestionnaireTemplate.getSections();
        if (assessment.isRiskAssessment()) {
            getRiskAssessmentAnswer(assessment, assessmentQuestionnaireSections);
        } else {
            if (Optional.ofNullable(assessment.getAssetId()).isPresent())
                getAssetAssessmentQuestionAndAnswer(unitId, assessment, assessmentQuestionnaireSections);
            else
                getProcessingActivityAssessmentQuestionAndAnswer(unitId, assessment, assessmentQuestionnaireSections);
        }
        return assessmentQuestionnaireSections;
    }*/


 /*
    private void getRiskAssessmentAnswer(AssessmentDeprecated assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        List<AssessmentAnswerValueObject> assessmentAnswers = assessment.getAssessmentAnswers();
        Map<BigInteger, Object> riskAssessmentAnswer = new HashMap<>();
        assessmentAnswers.forEach(assessmentAnswer -> riskAssessmentAnswer.put(assessmentAnswer.getQuestionId(), assessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO question : questionnaireSectionResponseDTO.getQuestions()) {
                question.setValue(riskAssessmentAnswer.get(question.getId()));
            }
        }

    }



    private void getAssetAssessmentQuestionAndAnswer(Long unitId, AssessmentDeprecated assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        List<AssessmentAnswerValueObject> assetAssessmentAnswers = assessment.getAssessmentAnswers();
        Map<AssetAttributeName, Object> assetAttributeNameObjectMap = new HashMap<>();
        assetAssessmentAnswers.forEach(assetAssessmentAnswer -> assetAttributeNameObjectMap.put(AssetAttributeName.valueOf(assetAssessmentAnswer.getAttributeName()), assetAssessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO question : questionnaireSectionResponseDTO.getQuestions()) {
                if (assetAttributeNameObjectMap.containsKey(AssetAttributeName.valueOf(question.getAttributeName()))) {
                    if (QuestionType.MULTIPLE_CHOICE.equals(question.getQuestionType()) && !Optional.ofNullable(assetAttributeNameObjectMap.get(AssetAttributeName.valueOf(question.getAttributeName()))).isPresent()) {
                        question.setValue(new ArrayList<>());
                    } else {
                        question.setValue(assetAttributeNameObjectMap.get(AssetAttributeName.valueOf(question.getAttributeName())));
                    }
                    question.setAssessmentAnswerChoices(addAssessmentAnswerOptionsForAsset(unitId, AssetAttributeName.valueOf(question.getAttributeName())));
                }
            }
        }


    }


    private void getProcessingActivityAssessmentQuestionAndAnswer(Long unitId, AssessmentDeprecated assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        List<AssessmentAnswerValueObject> processingActivityAssessmentAnswers = assessment.getAssessmentAnswers();
        Map<ProcessingActivityAttributeName, Object> processingActivityAttributeNameObjectMap = new HashMap<>();
        processingActivityAssessmentAnswers.forEach(processingActivityAssessmentAnswer -> processingActivityAttributeNameObjectMap.put(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentAnswer.getAttributeName()), processingActivityAssessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO question : questionnaireSectionResponseDTO.getQuestions()) {
                if (processingActivityAttributeNameObjectMap.containsKey(ProcessingActivityAttributeName.valueOf(question.getAttributeName()))) {
                    if (QuestionType.MULTIPLE_CHOICE.equals(question.getQuestionType()) && !Optional.ofNullable(processingActivityAttributeNameObjectMap.get(ProcessingActivityAttributeName.valueOf(question.getAttributeName()))).isPresent()) {
                        question.setValue(new ArrayList<>());
                    } else {
                        question.setValue(processingActivityAttributeNameObjectMap.get(ProcessingActivityAttributeName.valueOf(question.getAttributeName())));
                    }
                    question.setAssessmentAnswerChoices(addAssessmentAnswerOptionsForProcessingActivity(unitId, ProcessingActivityAttributeName.valueOf(question.getAttributeName())));
                }
            }
        }

    }
*/
    private Object addAssessmentAnswerOptionsForAsset(Long unitId, AssetAttributeName assetAttributeName) {


        switch (assetAttributeName) {
            //TODO
            /*case HOSTING_PROVIDER:
                return hostingProviderMongoRepository.findAllByUnitId(unitId);
            case HOSTING_TYPE:
                return hostingTypeMongoRepository.findAllByUnitId(unitId);
            case ASSET_TYPE:
                return assetTypeMongoRepository.getAllAssetTypeWithSubAssetTypeByUnitId(unitId);
            case STORAGE_FORMAT:
                return storageFormatMongoRepository.findAllByUnitId(unitId);
            case DATA_DISPOSAL:
                return dataDisposalMongoRepository.findAllByUnitId(unitId);
            case TECHNICAL_SECURITY_MEASURES:
                return technicalSecurityMeasureMongoRepository.findAllByUnitId(unitId);
            case ORGANIZATION_SECURITY_MEASURES:
                return organizationalSecurityMeasureRepository.findAllByUnitId(unitId);*/
            default:
                return null;
        }


    }


    private Object addAssessmentAnswerOptionsForProcessingActivity(Long unitId, ProcessingActivityAttributeName processingActivityAttributeName) {


        switch (processingActivityAttributeName) {
            //TODO
            /*case RESPONSIBILITY_TYPE:
                return responsibilityTypeMongoRepository.findAllByUnitId(unitId);
            case PROCESSING_PURPOSES:
                return processingPurposeMongoRepository.findAllByUnitId(unitId);
            case DATA_SOURCES:
                return dataSourceMongoRepository.findAllByUnitId(unitId);
            case TRANSFER_METHOD:
                return transferMethodMongoRepository.findAllByUnitId(unitId);
            case ACCESSOR_PARTY:
                return accessorPartyMongoRepository.findAllByUnitId(unitId);
            case PROCESSING_LEGAL_BASIS:
                return processingLegalBasisMongoRepository.findAllByUnitId(unitId);*/
            default:
                return null;
        }


    }


    /**
     * @param unitId
     * @param assessmentId
     * @param assessmentStatus
     * @return
     */
    public boolean updateAssessmentStatus(Long unitId, Long assessmentId, AssessmentStatus assessmentStatus) {
        Assessment assessment = assessmentRepository.findByOrganizationIdAndDeletedAndId(assessmentId, false, unitId);
        UserVO currentUser = ObjectMapperUtils.copyPropertiesByMapper(UserContext.getUserDetails(), UserVO.class);
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
                saveAssessmentAnswerOnCompletionToAssetOrProcessingActivity(unitId, assessment);
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


    private void saveAssessmentAnswerOnCompletionToAssetOrProcessingActivity(Long unitId, Assessment assessment) {

        if (!assessment.isRiskAssessment() && Optional.ofNullable(assessment.getAsset()).isPresent()) {
            Asset asset = assetRepository.findByIdAndOrganizationIdAndDeleted(unitId, assessment.getAsset().getId());
            List<AssessmentAnswer> assessmentAnswersForAsset = assessment.getAssessmentAnswers();
            assessmentAnswersForAsset.forEach(assetAssessmentAnswer -> {
                if (Optional.ofNullable(assetAssessmentAnswer.getAttributeName()).isPresent()) {
                    saveAssessmentAnswerForAssetOnCompletionOfAssessment(AssetAttributeName.valueOf(assetAssessmentAnswer.getAttributeName()), assetAssessmentAnswer.getValue(), asset);
                } else {
                    exceptionService.invalidRequestException("message.assessment.answer.attribute.null");
                }
            });
            assetRepository.save(asset);
        } else if (!assessment.isRiskAssessment() && Optional.ofNullable(assessment.getProcessingActivity()).isPresent()) {
            ProcessingActivity processingActivity = processingActivityRepository.findByIdAndOrganizationIdAndDeleted(assessment.getProcessingActivity().getId(), unitId);
            List<AssessmentAnswer> assessmentAnswersForProcessingActivity = assessment.getAssessmentAnswers();
            assessmentAnswersForProcessingActivity.forEach(processingActivityAssessmentAnswer
                    -> {
                if (Optional.ofNullable(processingActivityAssessmentAnswer.getAttributeName()).isPresent()) {
                    saveAssessmentAnswerForProcessingActivityOnCompletionOfAssessment(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentAnswer.getAttributeName()), processingActivityAssessmentAnswer.getValue(), processingActivity);
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

    /**
     * @param unitId
     * @param assessmentId
     * @return
     */
    public List<AssessmentAnswer> addAssessmentAnswerForAssetOrProcessingActivity(Long unitId, BigInteger assessmentId, List<AssessmentAnswer> assessmentAnswerValueObjects, AssessmentStatus status) {
//TODO
        /*Assessment assessment = assessmentMongoRepository.findByUnitIdAndId(unitId, assessmentId);
        UserVO currentUser = new UserVO();
        ObjectMapperUtils.copyProperties(UserContext.getUserDetails(), currentUser);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        } else if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
            exceptionService.invalidRequestException("message.assessment.change.status", AssessmentStatus.IN_PROGRESS);
        } else if (assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
            exceptionService.invalidRequestException("message.assessment.completed.cannot.fill.answer");
        }
        assessment.setAssessmentAnswers(assessmentAnswerValueObjects);
        if (Optional.ofNullable(status).isPresent() && AssessmentStatus.COMPLETED.equals(status)) {
            if (!currentUser.equals(assessment.getAssessmentLastAssistBy())) {
                exceptionService.invalidRequestException("message.notAuthorized.toChange.assessment.status");
            }
            assessment.setAssessmentStatus(status);
            assessment.setCompletedDate(LocalDate.now());
            //saveAssessmentAnswerOnCompletionToAssetOrProcessingActivity(unitId, assessment);
        }
        assessmentMongoRepository.save(assessment);*/
        return assessmentAnswerValueObjects;

    }


    /**
     * @param assetAttributeName  asset field
     * @param assetAttributeValue asset value corresponding to field
     * @param asset               asset to which value Assessment answer were filed by assignee
     */
    public void saveAssessmentAnswerForAssetOnCompletionOfAssessment(AssetAttributeName assetAttributeName, Object assetAttributeValue, Asset asset) {
        switch (assetAttributeName) {
            case NAME:
                asset.setName((String) assetAttributeValue);
                break;
            case DESCRIPTION:
                asset.setDescription((String) assetAttributeValue);
                break;
            case HOSTING_LOCATION:
                asset.setHostingLocation((String) assetAttributeValue);
                break;
            case HOSTING_TYPE:
                asset.setHostingType(hostingTypeRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0)));
                break;
            case DATA_DISPOSAL:
                asset.setDataDisposal(dataDisposalRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0)));
                break;
            case HOSTING_PROVIDER:
                asset.setHostingProvider(hostingProviderRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0)));
                break;
            case ASSET_TYPE:
                asset.setAssetType(assetTypeRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0)));
                break;
            case STORAGE_FORMAT:
                asset.setStorageFormats(storageFormatRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue)));
                break;
            case ASSET_SUB_TYPE:
                asset.setSubAssetType(assetTypeRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0)));
                break;
            case TECHNICAL_SECURITY_MEASURES:
                asset.setTechnicalSecurityMeasures(technicalSecurityMeasureRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue)));
                break;
            case ORGANIZATION_SECURITY_MEASURES:
                asset.setOrgSecurityMeasures(organizationalSecurityMeasureRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue)));
                break;
            case MANAGING_DEPARTMENT:
                asset.setManagingDepartment(objectMapper.convertValue(assetAttributeValue, ManagingOrganization.class));
                break;
            case ASSET_OWNER:
                asset.setAssetOwner(objectMapper.convertValue(assetAttributeValue, com.kairos.persistence.model.embeddables.Staff.class));
                break;
            case DATA_RETENTION_PERIOD:
                asset.setDataRetentionPeriod((Integer) assetAttributeValue);
                break;

        }
    }


    /**
     * @param processingActivityAttributeName  processing activity field
     * @param processingActivityAttributeValue processing activity  value corresponding to field
     * @param processingActivity               processing activity to which value Assessment answer were filed by assignee
     */
    public void saveAssessmentAnswerForProcessingActivityOnCompletionOfAssessment(ProcessingActivityAttributeName processingActivityAttributeName, Object processingActivityAttributeValue, ProcessingActivity processingActivity) {
        switch (processingActivityAttributeName) {
            case NAME:
                processingActivity.setName((String) processingActivityAttributeValue);
                break;
            case DESCRIPTION:
                processingActivity.setDescription((String) processingActivityAttributeValue);
                break;
            case RESPONSIBILITY_TYPE:
                processingActivity.setResponsibilityType(responsibilityTypeRepository.findByIdAndDeletedFalse(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue).get(0)));
                break;
            case ACCESSOR_PARTY:
                processingActivity.setAccessorParties(accessorPartyRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue)));
                break;
            case PROCESSING_PURPOSES:
                processingActivity.setProcessingPurposes(processingPurposeRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue)));
                break;
            case PROCESSING_LEGAL_BASIS:
                processingActivity.setProcessingLegalBasis(processingLegalBasisRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue)));
                break;
            case TRANSFER_METHOD:
                processingActivity.setTransferMethods(transferMethodRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue)));
                break;
            case DATA_SOURCES:
                processingActivity.setDataSources(dataSourceRepository.findAllByIds(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue)));
                break;
            case MANAGING_DEPARTMENT:
                processingActivity.setManagingDepartment(objectMapper.convertValue(processingActivityAttributeValue, ManagingOrganization.class));
                break;
            case PROCESS_OWNER:
                processingActivity.setProcessOwner(objectMapper.convertValue(processingActivityAttributeValue, Staff.class));
                break;
            case DATA_RETENTION_PERIOD:
                processingActivity.setDataRetentionPeriod((Integer) processingActivityAttributeValue);
                break;
            case MAX_DATA_SUBJECT_VOLUME:
                processingActivity.setMaxDataSubjectVolume((Long) processingActivityAttributeValue);
                break;
            case MIN_DATA_SUBJECT_VOLUME:
                processingActivity.setMinDataSubjectVolume((Long) processingActivityAttributeValue);
                break;
            case JOINT_CONTROLLER_CONTACT_INFO:
                processingActivity.setJointControllerContactInfo((Integer) processingActivityAttributeValue);
                break;
            case CONTROLLER_CONTACT_INFO:
                processingActivity.setJointControllerContactInfo((Integer) processingActivityAttributeValue);
                break;
            case DPO_CONTACT_INFO:
                processingActivity.setDpoContactInfo((Integer) processingActivityAttributeValue);
                break;
        }
    }

    private List<Long> castObjectIntoLinkedHashMapAndReturnIdList(Object objectToCast) {

        List<Long> entityIdList = new ArrayList<>();
        if (Optional.ofNullable(objectToCast).isPresent()) {
            if (objectToCast instanceof ArrayList) {
                List<LinkedHashMap<String, Object>> entityList = (List<LinkedHashMap<String, Object>>) objectToCast;
                entityList.forEach(entityKeyValueMap -> entityIdList.add(new Long(entityKeyValueMap.get("id").toString())));
            } else {
                LinkedHashMap<String, Object> entityKeyValueMap = (LinkedHashMap<String, Object>) objectToCast;
                entityIdList.add(new Long(entityKeyValueMap.get("id").toString()));
            }
        }
        return entityIdList;
    }

    private void mapEntityValueAsAssessmentAnswer(Assessment assessment, Asset asset, ProcessingActivity processingActivity) {

        QuestionnaireTemplate questionnaireTemplateDTO = assessment.getQuestionnaireTemplate();
        if (!Optional.ofNullable(questionnaireTemplateDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template");
        }
        List<AssessmentAnswer> assessmentAnswerVOS = new ArrayList<>();
        for (QuestionnaireSection questionnaireSection : questionnaireTemplateDTO.getSections()) {
            for (Question question : questionnaireSection.getQuestions()) {
                assessmentAnswerVOS.add(Optional.ofNullable(asset).isPresent() ? mapAssetValueAsAsessmentAnswer(asset, question) : mapProcessingActivityValueAssessmentAnswer(processingActivity, question));
            }
        }
        assessment.setAssessmentAnswers(assessmentAnswerVOS);
    }



   /* private void saveRiskTemplateAnswerToAssessment(Long unitId, Assessment assessment) {

        QuestionnaireTemplateResponseDTO questionnaireTemplateDTO = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, assessment.getQuestionnaireTemplate());
        if (!Optional.ofNullable(questionnaireTemplateDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template");
        }
        List<AssessmentAnswer> riskAssessmentAnswer = new ArrayList<>();
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : questionnaireTemplateDTO.getSections()) {
            for (QuestionBasicResponseDTO questionBasicDTO : questionnaireSectionResponseDTO.getQuestions()) {
                riskAssessmentAnswer.add(new AssessmentAnswer(questionBasicDTO.getId(), null, null, questionBasicDTO.getQuestionType()));
            }
        }
        assessment.setAssessmentAnswers(riskAssessmentAnswer);*/


    //}


    //  private void mapRisk

    //TODO
    private AssessmentAnswer mapAssetValueAsAsessmentAnswer(Asset asset, Question question) {
        AssetAttributeName assetAttributeName = AssetAttributeName.valueOf(question.getAttributeName());
        switch (assetAttributeName) {
            case NAME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getName(), question.getQuestionType());
            case DESCRIPTION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getDescription(), question.getQuestionType());
            case HOSTING_LOCATION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getHostingLocation(), question.getQuestionType());
            case HOSTING_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getHostingType(), question.getQuestionType());
            case DATA_DISPOSAL:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getDataDisposal(), question.getQuestionType());
            case HOSTING_PROVIDER:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getHostingProvider(), question.getQuestionType());
            case ASSET_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getAssetType(), question.getQuestionType());
            case STORAGE_FORMAT:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getStorageFormats(), question.getQuestionType());
            case ASSET_SUB_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getSubAssetType(), question.getQuestionType());
            case TECHNICAL_SECURITY_MEASURES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getTechnicalSecurityMeasures(), question.getQuestionType());
            case ORGANIZATION_SECURITY_MEASURES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getOrgSecurityMeasures(), question.getQuestionType());
            case MANAGING_DEPARTMENT:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getManagingDepartment(), question.getQuestionType());
            case ASSET_OWNER:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getAssetOwner(), question.getQuestionType());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), asset.getDataRetentionPeriod(), question.getQuestionType());
            default:
                return null;
        }

    }

    private AssessmentAnswer mapProcessingActivityValueAssessmentAnswer(ProcessingActivity processingActivity, Question question) {

        ProcessingActivityAttributeName processingActivityAttributeName = ProcessingActivityAttributeName.valueOf(question.getAttributeName());
        switch (processingActivityAttributeName) {
            case NAME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getName(), question.getQuestionType());
            case DESCRIPTION:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getDescription(), question.getQuestionType());
            case RESPONSIBILITY_TYPE:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getResponsibilityType(), question.getQuestionType());
            case ACCESSOR_PARTY:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getAccessorParties(), question.getQuestionType());
            case PROCESSING_PURPOSES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getProcessingPurposes(), question.getQuestionType());
            case PROCESSING_LEGAL_BASIS:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getProcessingLegalBasis(), question.getQuestionType());
            case TRANSFER_METHOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getTransferMethods(), question.getQuestionType());
            case DATA_SOURCES:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getDataSources(), question.getQuestionType());
            case PROCESS_OWNER:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getProcessOwner(), question.getQuestionType());
            case MANAGING_DEPARTMENT:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getManagingDepartment(), question.getQuestionType());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getDataRetentionPeriod(), question.getQuestionType());
            case DPO_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getDpoContactInfo(), question.getQuestionType());
            case CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getControllerContactInfo(), question.getQuestionType());
            case MAX_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getMaxDataSubjectVolume(), question.getQuestionType());
            case MIN_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getMinDataSubjectVolume(), question.getQuestionType());
            case JOINT_CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswer(question.getId(), question.getAttributeName(), processingActivity.getJointControllerContactInfo(), question.getQuestionType());
            default:
                return null;
        }

    }


}
