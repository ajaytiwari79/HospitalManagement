package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.enums.gdpr.AssetAttributeName;
import com.kairos.enums.gdpr.ProcessingActivityAttributeName;
import com.kairos.enums.gdpr.QuestionnaireTemplateType;
import com.kairos.dto.gdpr.data_inventory.AssessmentDTO;
import com.kairos.persistence.model.data_inventory.assessment.AssetAssessmentAnswerVO;
import com.kairos.persistence.model.data_inventory.assessment.ProcessingActivityAssessmentAnswerVO;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.data_inventory.assessment.AssessmentAnswerValueObject;
import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.model.questionnaire_template.QuestionnaireTemplate;
import com.kairos.persistence.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistence.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistence.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
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
        assessmentMongoRepository.save(assessment);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
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
                    questionnaireTemplateType = questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndSubAssetType(unitId, asset.getAssetType(), asset.getAssetSubTypes());
                } else {
                    questionnaireTemplateType = questionnaireTemplateMongoRepository.findQuestionnaireTemplateByAssetTypeAndUnitId(unitId, asset.getAssetType());
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
        }
        Assessment assessment = new Assessment(assessmentDTO.getName(), assessmentDTO.getEndDate(), assessmentDTO.getAssignee(), assessmentDTO.getApprover());
        assessment.setOrganizationId(unitId);
        assessment.setComment(assessmentDTO.getComment());
        assessment.setQuestionnaireTemplateId(questionnaireTemplateType.getId());
        return assessment;

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
        QuestionnaireTemplateResponseDTO assessmentQuestionnaireTemplate = questionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsByUnitIdAndId(unitId, assessment.getQuestionnaireTemplateId());
        List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections = assessmentQuestionnaireTemplate.getSections();
        if (Optional.ofNullable(assessment.getAssetId()).isPresent()) {
            getAssetAssessmentQuestionAndValuesById(unitId, assessment.getAssessmentStatus(), assessment, assessmentQuestionnaireSections);
        } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {

            getProcessingActivityAssessmentQuestionAndValuesById(unitId, assessment.getAssessmentStatus(), assessment, assessmentQuestionnaireSections);
        }
        return assessmentQuestionnaireSections;
    }

    /**
     * @param unitId
     * @param assessmentStatus
     * @param assessment
     * @param assessmentQuestionnaireSections
     */
    private void getAssetAssessmentQuestionAndValuesById(Long unitId, AssessmentStatus assessmentStatus, Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        switch (assessmentStatus) {
            case NEW:
                AssetResponseDTO assetDTO = assetMongoRepository.findAssetWithMetaDataById(unitId, assessment.getAssetId());
                if (!Optional.ofNullable(assetDTO).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assessment.getAssetId());
                }

                Map<String, Object> props = objectMapper.convertValue(assetDTO, Map.class);
                for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (QuestionBasicResponseDTO assetAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (props.containsKey(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            assetAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(props.get(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value));
                        }
                    }
                }
                break;
            case IN_PROGRESS:
                List<AssetAssessmentAnswerVO> assetAssessmentAnswers = assessment.getAssetAssessmentAnswers();
                Map<AssetAttributeName, Object> assetAttributeNameObjectMap = new HashMap<>();
                assetAssessmentAnswers.forEach(assetAssessmentAnswer -> assetAttributeNameObjectMap.put(assetAssessmentAnswer.getAssetField(), assetAssessmentAnswer.getValue()));
                for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (QuestionBasicResponseDTO assetAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (assetAttributeNameObjectMap.containsKey(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()))) {
                            assetAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(assetAttributeNameObjectMap.get(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName())));
                        }
                    }
                }
                break;
            case COMPLETED:
                exceptionService.invalidRequestException("Assessment is Completed");
                break;
        }

    }


    /**
     * @param unitId
     * @param assessmentStatus
     * @param assessment
     * @param assessmentQuestionnaireSections
     */
    private void getProcessingActivityAssessmentQuestionAndValuesById(Long unitId, AssessmentStatus assessmentStatus, Assessment assessment, List<QuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) {

        switch (assessmentStatus) {
            case NEW:
                ProcessingActivityResponseDTO processingActivity = processingActivityMongoRepository.getProcessingActivityAndMetaDataById(unitId, assessment.getProcessingActivityId());
                if (!Optional.ofNullable(processingActivity).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assessment.getProcessingActivityId());
                }
                Map<String, Object> props = objectMapper.convertValue(processingActivity, Map.class);
                for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (QuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (props.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(props.get(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName())));
                        }
                    }
                }
                break;
            case IN_PROGRESS:
                List<ProcessingActivityAssessmentAnswerVO> processingActivityAssessmentAnswers = assessment.getProcessingActivityAssessmentAnswers();
                Map<ProcessingActivityAttributeName, Object> processingActivityAttributeNameObjectMap = new HashMap<>();
                processingActivityAssessmentAnswers.forEach(processingActivityAssessmentAnswer -> processingActivityAttributeNameObjectMap.put(processingActivityAssessmentAnswer.getProcessingActivityField(), processingActivityAssessmentAnswer.getValue()));
                for (QuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (QuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (processingActivityAttributeNameObjectMap.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()))) {
                            processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(processingActivityAttributeNameObjectMap.get(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value));
                        }
                    }
                }
                break;
            case COMPLETED:
                exceptionService.invalidRequestException("Assessment is Completed");
                break;
        }

    }


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
                break;
            case COMPLETED:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
                    exceptionService.invalidRequestException("message.assessment.invalid.status", assessment.getAssessmentStatus(), assessmentStatus);
                } else {
                    if (Optional.ofNullable(assessment.getAssetId()).isPresent()) {
                        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assessment.getAssetId());
                        List<AssetAssessmentAnswerVO> assessmentAnswersForAsset = assessment.getAssetAssessmentAnswers();
                        assessmentAnswersForAsset.forEach(assetAssessmentAnswer -> saveAssessmentAnswerForAsset(assetAssessmentAnswer.getAssetField(), assetAssessmentAnswer.getValue(), asset));
                        assetMongoRepository.save(asset);

                    } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {
                        ProcessingActivity processingActivity = processingActivityMongoRepository.findByUnitIdAndId(unitId, assessment.getAssetId());
                        List<ProcessingActivityAssessmentAnswerVO> assessmentAnswersForProcessingActivity = assessment.getProcessingActivityAssessmentAnswers();

                        assessmentAnswersForProcessingActivity.forEach(processingActivityAssessmentAnswer
                                -> saveAssessmentAnswerForProcessingActivity(processingActivityAssessmentAnswer.getProcessingActivityField(), processingActivityAssessmentAnswer.getValue(), processingActivity));
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

    /**
     * @param unitId
     * @param assessmentId
     * @param assessmentAnswerValueObject
     * @return
     */
    public AssessmentAnswerValueObject addAssessmentAnswerForAssetOrProcessingActivityToAssessment(Long unitId, BigInteger assessmentId, AssessmentAnswerValueObject assessmentAnswerValueObject) {

        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        if (assessment.getAssessmentStatus().equals(AssessmentStatus.NEW)) {
            exceptionService.invalidRequestException("message.assessment.change.status", AssessmentStatus.IN_PROGRESS);
        }

        if (Optional.ofNullable(assessment.getAssetId()).isPresent()) {
            assessment.setAssetAssessmentAnswers(assessmentAnswerValueObject.getAssetAssessmentAnswers());
        } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {
            assessment.setProcessingActivityAssessmentAnswers(assessmentAnswerValueObject.getProcessingActivityAssessmentAnswers());
        }
        assessmentMongoRepository.save(assessment);
        return assessmentAnswerValueObject;

    }


    /**
     * @param assetAttributeName  asset field
     * @param assetAttributeValue asset value corresponding to field
     * @param asset               asset to which value Assessment answer were filed by assignee
     */
    public void saveAssessmentAnswerForAsset(AssetAttributeName assetAttributeName, Object assetAttributeValue, Asset asset) {
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
        }
    }


    /**
     * @param processingActivityAttributeName  processing activity field
     * @param processingActivityAttributeValue processing activity  value corresponding to field
     * @param processingActivity               processing activity to which value Assessment answer were filed by assignee
     */
    public void saveAssessmentAnswerForProcessingActivity(ProcessingActivityAttributeName processingActivityAttributeName, Object processingActivityAttributeValue, ProcessingActivity processingActivity) {
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


}
