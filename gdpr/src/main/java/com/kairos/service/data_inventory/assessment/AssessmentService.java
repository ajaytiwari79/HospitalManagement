package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.dto.gdpr.ManagingOrganization;
import com.kairos.dto.gdpr.Staff;
import com.kairos.enums.gdpr.*;
import com.kairos.dto.gdpr.data_inventory.AssessmentDTO;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.data_inventory.assessment.AssessmentAnswerValueObject;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.AssetTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.data_disposal.DataDisposalMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_provider.HostingProviderMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.hosting_type.HostingTypeMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.org_security_measure.OrganizationalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.storage_format.StorageFormatMongoRepository;
import com.kairos.persistence.repository.master_data.asset_management.tech_security_measure.TechnicalSecurityMeasureMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.accessor_party.AccessorPartyMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.data_source.DataSourceMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.legal_basis.ProcessingLegalBasisMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.processing_purpose.ProcessingPurposeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type.ResponsibilityTypeMongoRepository;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.transfer_method.TransferMethodMongoRepository;
import com.kairos.persistence.repository.questionnaire_template.QuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.common.AssessmentResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionBasicResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireSectionResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.QuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.*;

@Service
public class AssessmentService extends MongoBaseService {


    @Inject
    private AssessmentMongoRepository assessmentMongoRepository;

    @Inject
    private AssetMongoRepository assetMongoRepository;

    @Inject
    private ProcessingActivityMongoRepository processingActivityMongoRepository;

    @Inject
    private ExceptionService exceptionService;

    @Inject
    private QuestionnaireTemplateMongoRepository questionnaireTemplateMongoRepository;

    @Inject
    private AssetTypeMongoRepository assetTypeMongoRepository;

    @Inject
    private StorageFormatMongoRepository storageFormatMongoRepository;

    @Inject
    private OrganizationalSecurityMeasureMongoRepository organizationalSecurityMeasureRepository;

    @Inject
    private TechnicalSecurityMeasureMongoRepository technicalSecurityMeasureMongoRepository;

    @Inject
    private HostingProviderMongoRepository hostingProviderMongoRepository;

    @Inject
    private HostingTypeMongoRepository hostingTypeMongoRepository;

    @Inject
    private DataDisposalMongoRepository dataDisposalMongoRepository;

    @Inject
    private ProcessingPurposeMongoRepository processingPurposeMongoRepository;

    @Inject
    private DataSourceMongoRepository dataSourceMongoRepository;

    @Inject
    private TransferMethodMongoRepository transferMethodMongoRepository;

    @Inject
    private AccessorPartyMongoRepository accessorPartyMongoRepository;

    @Inject
    private ProcessingLegalBasisMongoRepository processingLegalBasisMongoRepository;

    @Inject
    private ResponsibilityTypeMongoRepository responsibilityTypeMongoRepository;

    @Inject
    private ObjectMapper objectMapper;


    /**
     * @param unitId        organization id
     * @param assetId       asset id for which assessment is related
     * @param assessmentDTO Assessment Dto contain detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO saveAssessmentForAsset(Long unitId, BigInteger assetId, AssessmentDTO assessmentDTO) {

        Assessment previousAssessment = assessmentMongoRepository.findPreviousLaunchedAssessmentOfAssetByUnitId(unitId, assetId);
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.assessment.cannotbe.launched.asset", previousAssessment.getName(), previousAssessment.getAssessmentStatus());
        }
        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        Assessment assessment = buildAssessmentWithBasicDetail(unitId, assessmentDTO, QuestionnaireTemplateType.ASSET_TYPE, asset);
        assessment.setAssetId(assetId);
        saveAssetValueToAssessmentAnswer(unitId, assessment);
        assessmentMongoRepository.save(assessment);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
    }


    /**
     * @param unitId
     * @param assessmentDTO
     * @return
     *///todo remove find by name
    private Assessment buildAssessmentWithBasicDetail(Long unitId, AssessmentDTO assessmentDTO, QuestionnaireTemplateType templateType, Object entity) {

        Assessment previousAssessment = assessmentMongoRepository.findAssessmentByNameAndUnitId(unitId, assessmentDTO.getName());
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Assessment", assessmentDTO.getName());
        }
        QuestionnaireTemplate questionnaireTemplateType = null;
        switch (templateType) {
            case ASSET_TYPE:
                Asset asset = (Asset) entity;
                if (CollectionUtils.isNotEmpty(asset.getAssetSubTypes())) {
                    questionnaireTemplateType = questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndSubAssetTypeByUnitId(unitId, asset.getAssetType(), asset.getAssetSubTypes());
                } else {
                    questionnaireTemplateType = questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndByUnitId(unitId, asset.getAssetType());
                }
                if (!Optional.ofNullable(questionnaireTemplateType).isPresent()) {
                    questionnaireTemplateType = questionnaireTemplateMongoRepository.findDefaultAssetQuestionnaireTemplateByUnitId(unitId);
                }
                break;
            default:
                questionnaireTemplateType = questionnaireTemplateMongoRepository.getQuestionnaireTemplateByTemplateTypeByUnitId(unitId, templateType);
                break;

        }
        if (!Optional.ofNullable(questionnaireTemplateType).isPresent()) {
            exceptionService.invalidRequestException("message.questionnaire.template.Not.Found.For.Template.Type", templateType);
        } else if (questionnaireTemplateType.getTemplateStatus().equals(QuestionnaireTemplateStatus.DRAFT)) {
            exceptionService.invalidRequestException("message.assessment.cannotbe.launched.questionnaireTemplate.notPublished");
        }
        Assessment assessment = new Assessment(assessmentDTO.getName(), assessmentDTO.getEndDate(), assessmentDTO.getAssignee(), assessmentDTO.getApprover());
        assessment.setOrganizationId(unitId);
        assessment.setComment(assessmentDTO.getComment());
        assessment.setQuestionnaireTemplateId(questionnaireTemplateType.getId());
        return assessment;

    }

    /**
     * @param unitId
     * @param processingActivityId Processing activity id for which assessment is related
     * @param assessmentDTO        Assessment Dto contain detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO saveAssessmentForProcessingActivity(Long unitId, BigInteger processingActivityId, AssessmentDTO assessmentDTO) {


        Assessment previousAssessment = assessmentMongoRepository.findPreviousLaunchedAssessmentOfProcessingActivityByUnitId(unitId, processingActivityId);
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.assessment.cannotbe.launched.processing.activity", previousAssessment.getName(), previousAssessment.getAssessmentStatus());
        }
        Assessment assessment = buildAssessmentWithBasicDetail(unitId, assessmentDTO, QuestionnaireTemplateType.PROCESSING_ACTIVITY, null);
        assessment.setProcessingActivityId(processingActivityId);
        saveProcessingActivityValueToAssessmentAnswer(unitId, assessment);
        assessmentMongoRepository.save(assessment);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
    }


    /**
     * @param unitId
     * @param assessmentId
     * @return
     */
    public List<QuestionnaireSectionResponseDTO> getAssessmentById(Long unitId, BigInteger assessmentId) {

        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        QuestionnaireTemplateResponseDTO assessmentQuestionnaireTemplate = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, assessment.getQuestionnaireTemplateId());
        List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections = assessmentQuestionnaireTemplate.getSections();
        if (Optional.ofNullable(assessment.getAssetId()).isPresent())
            getAssetAssessmentQuestionAndAnswer(unitId, assessment, assessmentQuestionnaireSections);
        else
            getProcessingActivityAssessmentQuestionAndAnswer(unitId, assessment, assessmentQuestionnaireSections);
        return assessmentQuestionnaireSections;
    }


    private void getAssetAssessmentQuestionAndAnswer(Long unitId, Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {


        List<AssessmentAnswerValueObject> assetAssessmentAnswers = assessment.getAssessmentAnswers();
        Map<AssetAttributeName, Object> assetAttributeNameObjectMap = new HashMap<>();
        assetAssessmentAnswers.forEach(assetAssessmentAnswer -> assetAttributeNameObjectMap.put(AssetAttributeName.valueOf(assetAssessmentAnswer.getAttributeName()), assetAssessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO assetAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                if (assetAttributeNameObjectMap.containsKey(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()))) {
                    assetAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(assetAttributeNameObjectMap.get(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName())));
                    assetAssessmentQuestionBasicResponseDTO.setAssessmentAnswerChoices(addAssessmentAnswerOptionsForAsset(unitId, AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName())));
                }
            }
        }


    }


    private void getProcessingActivityAssessmentQuestionAndAnswer(Long unitId, Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {


        List<AssessmentAnswerValueObject> processingActivityAssessmentAnswers = assessment.getAssessmentAnswers();
        Map<ProcessingActivityAttributeName, Object> processingActivityAttributeNameObjectMap = new HashMap<>();
        processingActivityAssessmentAnswers.forEach(processingActivityAssessmentAnswer -> processingActivityAttributeNameObjectMap.put(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentAnswer.getAttributeName()), processingActivityAssessmentAnswer.getValue()));
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
            for (QuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                if (processingActivityAttributeNameObjectMap.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()))) {
                    processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(processingActivityAttributeNameObjectMap.get(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName())));

                    processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentAnswerChoices(addAssessmentAnswerOptionsForProcessingActivity(unitId, ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName())));
                }
            }
        }

    }


    private Object addAssessmentAnswerOptionsForAsset(Long unitId, AssetAttributeName assetAttributeName) {


        switch (assetAttributeName) {
            case HOSTING_PROVIDER:
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
                return organizationalSecurityMeasureRepository.findAllByUnitId(unitId);
            default:
                return null;
        }


    }


    private Object addAssessmentAnswerOptionsForProcessingActivity(Long unitId, ProcessingActivityAttributeName processingActivityAttributeName) {


        switch (processingActivityAttributeName) {
            case RESPONSIBILITY_TYPE:
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
                return processingLegalBasisMongoRepository.findAllByUnitId(unitId);
            default:
                return null;
        }


    }


    //todo modifying method

    /**
     * @param unitId
     * @param assessmentId
     * @param assessmentStatus
     * @return
     */
    public boolean updateAssessmentStatus(Long unitId, BigInteger assessmentId, AssessmentStatus assessmentStatus) {
        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        switch (assessmentStatus) {
            case IN_PROGRESS:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                }
                if (Optional.ofNullable(assessment.getAssetId()).isPresent())
                    saveAssetValueToAssessmentAnswer(unitId, assessment);
                else saveProcessingActivityValueToAssessmentAnswer(unitId, assessment);
                break;
            case COMPLETED:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                } else {
                    if (Optional.ofNullable(assessment.getAssetId()).isPresent()) {
                        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assessment.getAssetId());
                        List<AssessmentAnswerValueObject> assessmentAnswersForAsset = assessment.getAssessmentAnswers();
                        assessmentAnswersForAsset.forEach(assetAssessmentAnswer -> saveAssessmentAnswerForAssetOnCompletionOfAssessment(AssetAttributeName.valueOf(assetAssessmentAnswer.getAttributeName()), assetAssessmentAnswer.getValue(), asset));
                        assetMongoRepository.save(asset);

                    } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {
                        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, assessment.getAssetId());
                        List<AssessmentAnswerValueObject> assessmentAnswersForProcessingActivity = assessment.getAssessmentAnswers();
                        assessmentAnswersForProcessingActivity.forEach(processingActivityAssessmentAnswer
                                -> saveAssessmentAnswerForProcessingActivityOnCompletionOfAssessment(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentAnswer.getAttributeName()), processingActivityAssessmentAnswer.getValue(), processingActivity));
                        processingActivityMongoRepository.save(processingActivity);

                    }
                }
                break;
            case NEW:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.IN_PROGRESS) || assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                }
                break;
        }
        assessment.setAssessmentStatus(assessmentStatus);
        assessmentMongoRepository.save(assessment);
        return true;
    }


    /**
     * @param unitId
     * @return
     *///todo add argument for assignee as well
    public List<AssessmentBasicResponseDTO> getAllLaunchedAssessmentOfAssignee(Long unitId, Long loggedInUserId) {
        return assessmentMongoRepository.getAllLaunchedAssessmentAssignToRespondent(unitId, loggedInUserId);
    }


    public List<AssessmentResponseDTO> getAllAssessmentByUnitId(Long unitId) {
        return assessmentMongoRepository.getAllAssessmentByUnitId(unitId);
    }


    public boolean deleteAssessmentbyId(Long unitId, BigInteger assessmentId) {

        Assessment assessment = assessmentMongoRepository.findByUnitIdAndIdAndAssessmentStatus(unitId, assessmentId, AssessmentStatus.IN_PROGRESS);
        if (Optional.ofNullable(assessment).isPresent()) {
            exceptionService.invalidRequestException("message.assessment.inprogress.cannot.delete", assessment.getName());
        }
        assetMongoRepository.safeDelete(assessmentId);
        return true;
    }

    /**
     * @param unitId
     * @param assessmentId
     * @return
     */
    public List<AssessmentAnswerValueObject> addAssessmentAnswerForAssetOrProcessingActivityToAssessment(Long unitId, BigInteger assessmentId, List<AssessmentAnswerValueObject> assessmentAnswerValueObjects) {

        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
            exceptionService.invalidRequestException("message.assessment.change.status", AssessmentStatus.IN_PROGRESS);
        }
        if (assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
            exceptionService.invalidRequestException("message.assessment.completed.cannot.fill.answer");
        }
        assessment.setAssessmentAnswers(assessmentAnswerValueObjects);
        assessmentMongoRepository.save(assessment);
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
                asset.setHostingType(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0));
                break;
            case DATA_DISPOSAL:
                asset.setDataDisposal(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0));
                break;
            case HOSTING_PROVIDER:
                asset.setHostingProvider(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0));
                break;
            case ASSET_TYPE:
                asset.setAssetType(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue).get(0));
                break;
            case STORAGE_FORMAT:
                asset.setStorageFormats(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue));
                break;
            case ASSET_SUB_TYPE:
                asset.setAssetSubTypes(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue));
                break;
            case TECHNICAL_SECURITY_MEASURES:
                asset.setTechnicalSecurityMeasures(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue));
                break;
            case ORGANIZATION_SECURITY_MEASURES:
                asset.setOrgSecurityMeasures(castObjectIntoLinkedHashMapAndReturnIdList(assetAttributeValue));
                break;
            case MANAGING_DEPARTMENT:
                asset.setManagingDepartment((ManagingOrganization) assetAttributeValue);
                break;
            case ASSET_OWNER:
                asset.setAssetOwner((Staff) assetAttributeValue);
                break;
            case MIN_DATA_SUBJECT_VOLUME:
                asset.setMinDataSubjectVolume((Long) assetAttributeValue);
                break;
            case MAX_DATA_SUBJECT_VOLUME:
                asset.setMaxDataSubjectVolume((Long) assetAttributeValue);
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
                processingActivity.setResponsibilityType(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue).get(0));
                break;
            case ACCESSOR_PARTY:
                processingActivity.setAccessorParties(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue));
                break;
            case PROCESSING_PURPOSES:
                processingActivity.setProcessingPurposes(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue));
                break;
            case PROCESSING_LEGAL_BASIS:
                processingActivity.setProcessingLegalBasis(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue));
                break;
            case TRANSFER_METHOD:
                processingActivity.setTransferMethods(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue));
                break;
            case DATA_SOURCES:
                processingActivity.setDataSources(castObjectIntoLinkedHashMapAndReturnIdList(processingActivityAttributeValue));
                break;
            case MANAGING_DEPARTMENT:
                processingActivity.setManagingDepartment((ManagingOrganization) processingActivityAttributeValue);
                break;
            case PROCESS_OWNER:
                processingActivity.setProcessOwner((Staff) processingActivityAttributeValue);
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

    private List<BigInteger> castObjectIntoLinkedHashMapAndReturnIdList(Object objectToCast) {
        List<BigInteger> entityIdList = new ArrayList<>();
        if (objectToCast instanceof ArrayList) {
            List<LinkedHashMap<String, Object>> entityList = (List<LinkedHashMap<String, Object>>) objectToCast;
            entityList.forEach(entityKeyValueMap -> entityIdList.add(new BigInteger((String) entityKeyValueMap.get("_id"))));
        } else {
            LinkedHashMap<String, Object> entityKeyValueMap = (LinkedHashMap<String, Object>) objectToCast;
            entityIdList.add(new BigInteger((String) entityKeyValueMap.get("_id")));
        }
        return entityIdList;
    }


    private void saveAssetValueToAssessmentAnswer(Long unitId, Assessment assessment) {

        AssetResponseDTO assetResponseDTO = assetMongoRepository.findAssetWithMetaDataById(unitId, assessment.getAssetId());
        QuestionnaireTemplateResponseDTO questionnaireTemplateDTO = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, assessment.getQuestionnaireTemplateId());
        if (!Optional.ofNullable(questionnaireTemplateDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template");
        }
        List<AssessmentAnswerValueObject> assetAssessmentAnswerVOS = new ArrayList<>();
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : questionnaireTemplateDTO.getSections()) {
            for (QuestionBasicResponseDTO questionBasicDTO : questionnaireSectionResponseDTO.getQuestions()) {
                assetAssessmentAnswerVOS.add(mapAssetValueAsAsessmentAnswerStatusUpdatingFromNewToInProgress(assetResponseDTO, questionBasicDTO));

            }
        }
        assessment.setAssessmentAnswers(assetAssessmentAnswerVOS);
    }


    private void saveProcessingActivityValueToAssessmentAnswer(Long unitId, Assessment assessment) {
        ProcessingActivityResponseDTO processingActivityDTO = processingActivityMongoRepository.getProcessingActivityAndMetaDataById(unitId, assessment.getProcessingActivityId());
        QuestionnaireTemplateResponseDTO questionnaireTemplateDTO = questionnaireTemplateMongoRepository.getQuestionnaireTemplateWithSectionsByUnitId(unitId, assessment.getQuestionnaireTemplateId());
        if (!Optional.ofNullable(questionnaireTemplateDTO).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Questionnaire Template");
        }
        List<AssessmentAnswerValueObject> processingActivityAssessmentAnswerVOS = new ArrayList<>();
        for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : questionnaireTemplateDTO.getSections()) {
            for (QuestionBasicResponseDTO questionBasicDTO : questionnaireSectionResponseDTO.getQuestions()) {
                processingActivityAssessmentAnswerVOS.add(mapProcessingActivityValueAssessmentAnswerOnStatusUpdatingFromNewToInProgress(processingActivityDTO, questionBasicDTO));
            }
        }
        assessment.setAssessmentAnswers(processingActivityAssessmentAnswerVOS);

    }

    private AssessmentAnswerValueObject mapAssetValueAsAsessmentAnswerStatusUpdatingFromNewToInProgress(AssetResponseDTO assetResponseDTO, QuestionBasicResponseDTO questionBasicDTO) {

        AssetAttributeName assetAttributeName = AssetAttributeName.valueOf(questionBasicDTO.getAttributeName());
        switch (assetAttributeName) {
            case NAME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getName());
            case DESCRIPTION:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getDescription());
            case HOSTING_LOCATION:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getHostingLocation());
            case HOSTING_TYPE:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getHostingType());
            case DATA_DISPOSAL:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getDataDisposal());
            case HOSTING_PROVIDER:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getHostingProvider());
            case ASSET_TYPE:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getAssetType());
            case STORAGE_FORMAT:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getStorageFormats());
            case ASSET_SUB_TYPE:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getAssetSubTypes());
            case TECHNICAL_SECURITY_MEASURES:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getTechnicalSecurityMeasures());
            case ORGANIZATION_SECURITY_MEASURES:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getOrgSecurityMeasures());
            case MANAGING_DEPARTMENT:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getManagingDepartment());
            case ASSET_OWNER:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getAssetOwner());
            case MIN_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getMinDataSubjectVolume());
            case MAX_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getMaxDataSubjectVolume());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), assetResponseDTO.getDataRetentionPeriod());
            default:
                return null;
        }

    }

    private AssessmentAnswerValueObject mapProcessingActivityValueAssessmentAnswerOnStatusUpdatingFromNewToInProgress(ProcessingActivityResponseDTO processingActivityDTO, QuestionBasicResponseDTO questionBasicDTO) {

        ProcessingActivityAttributeName processingActivityAttributeName = ProcessingActivityAttributeName.valueOf(questionBasicDTO.getAttributeName());
        switch (processingActivityAttributeName) {
            case NAME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getName());
            case DESCRIPTION:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getDescription());
            case RESPONSIBILITY_TYPE:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getResponsibilityType());
            case ACCESSOR_PARTY:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getAccessorParties());
            case PROCESSING_PURPOSES:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getProcessingPurposes());
            case PROCESSING_LEGAL_BASIS:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getProcessingLegalBasis());
            case TRANSFER_METHOD:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getTransferMethods());
            case DATA_SOURCES:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getDataSources());
            case PROCESS_OWNER:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getProcessOwner());
            case MANAGING_DEPARTMENT:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getManagingDepartment());
            case DATA_RETENTION_PERIOD:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getDataRetentionPeriod());
            case DPO_CONTACT_INFO:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getDpoContactInfo());
            case CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getControllerContactInfo());
            case MAX_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getMaxDataSubjectVolume());
            case MIN_DATA_SUBJECT_VOLUME:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getMinDataSubjectVolume());
            case JOINT_CONTROLLER_CONTACT_INFO:
                return new AssessmentAnswerValueObject(questionBasicDTO.getId(), questionBasicDTO.getAttributeName(), processingActivityDTO.getJointControllerContactInfo());
            default:
                return null;
        }

    }


}
