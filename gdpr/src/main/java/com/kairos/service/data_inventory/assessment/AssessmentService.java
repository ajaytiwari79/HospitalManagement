package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.enums.AssessmentStatus;
import com.kairos.enums.AssetAttributeName;
import com.kairos.enums.ProcessingActivityAttributeName;
import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.gdpr.data_inventory.AssessmentDTO;
import com.kairos.persistance.model.data_inventory.assessment.Assessment;
import com.kairos.persistance.model.data_inventory.assessment.AssessmentAnswerValueObject;
import com.kairos.persistance.model.data_inventory.assessment.AssetAssessmentAnswer;
import com.kairos.persistance.model.data_inventory.assessment.ProcessingActivityAssessmentAnswer;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.response.dto.data_inventory.AssessmentResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import com.kairos.response.dto.data_inventory.ProcessingActivityResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionBasicResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireSectionResponseDTO;
import com.kairos.response.dto.master_data.questionnaire_template.MasterQuestionnaireTemplateResponseDTO;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
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
    private MasterQuestionnaireTemplateMongoRepository masterQuestionnaireTemplateMongoRepository;

    @Inject
    private ObjectMapper objectMapper;


    /**
     * @param unitId        organization id
     * @param assetId       asset id for which assessment is related
     * @param assessmentDTO Assessment Dto conatin detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO saveAssessmentForAsset(Long unitId, Long countryId, BigInteger assetId, AssessmentDTO assessmentDTO) {

        Asset asset = assetMongoRepository.findByIdAndNonDeleted(unitId, assetId);
        if (!Optional.ofNullable(asset).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assetId);
        }
        Assessment assessment = buildAssessmentWithBasicDetail(unitId, countryId, assessmentDTO, QuestionnaireTemplateType.ASSET_TYPE);
        assessment.setAssetId(assetId);
        assessmentMongoRepository.save(assessment);
        asset.getAssessments().add(assessment.getId());
        assetMongoRepository.save(asset);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
    }


    /**
     * @param unitId
     * @param processingActivityId Processing activity id for which assessment is related
     * @param assessmentDTO        Assessment Dto conatin detail about who assign assessment and to whom assessment is assigned
     * @return
     */
    public AssessmentDTO saveAssessmentForProcessingActivity(Long unitId, Long countryId, BigInteger processingActivityId, AssessmentDTO assessmentDTO) {

        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, processingActivityId);
        if (!Optional.ofNullable(processingActivity).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Processing Activity", processingActivityId);
        }
        Assessment assessment = buildAssessmentWithBasicDetail(unitId, countryId, assessmentDTO, QuestionnaireTemplateType.PROCESSING_ACTIVITY);
        assessmentMongoRepository.save(assessment);
        processingActivity.getAssessments().add(assessment.getId());
        processingActivityMongoRepository.save(processingActivity);
        assessmentDTO.setId(assessment.getId());
        return assessmentDTO;
    }


    /**
     * @param unitId
     * @param assessmentDTO
     * @return
     */
    private Assessment buildAssessmentWithBasicDetail(Long unitId, Long countryId, AssessmentDTO assessmentDTO, QuestionnaireTemplateType templateType) {

        Assessment previousAssessment = assessmentMongoRepository.findAssessmentByNameAndUnitId(unitId, assessmentDTO.getName());
        if (Optional.ofNullable(previousAssessment).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Assessment", assessmentDTO.getName());
        }
        BigInteger questionnaireTemplateId = masterQuestionnaireTemplateMongoRepository.
                getMasterQuestionanaireTemplateIdListByTemplateType(countryId, templateType);
        if (!Optional.ofNullable(questionnaireTemplateId).isPresent()) {
            exceptionService.invalidRequestException("message.questionnaire.template.Not.Found.For.Template.Type", templateType);
        }
        Assessment assessment = new Assessment(assessmentDTO.getName(), assessmentDTO.getEndDate(), assessmentDTO.getAssignee(), assessmentDTO.getApprover());
        assessment.setOrganizationId(unitId);
        assessment.setComment(assessmentDTO.getComment());
        assessment.setQuestionnaireTemplateId(questionnaireTemplateId);
        return assessment;

    }


    /**
     * @param countryId
     * @param unitId
     * @param assessmentId
     * @return
     */
    public List<MasterQuestionnaireSectionResponseDTO> getAssessmentById(Long countryId, Long unitId, BigInteger assessmentId) throws IOException {

        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        MasterQuestionnaireTemplateResponseDTO assessmentQuestionnaireTemplate = masterQuestionnaireTemplateMongoRepository.getMasterQuestionnaireTemplateWithSectionsByCountryIdAndId(countryId, assessment.getQuestionnaireTemplateId());
        List<MasterQuestionnaireSectionResponseDTO> assessmentQuestionnaireSections = assessmentQuestionnaireTemplate.getSections();
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
     * @throws IOException
     */
    private void getAssetAssessmentQuestionAndValuesById(Long unitId, AssessmentStatus assessmentStatus, Assessment assessment, List<MasterQuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) throws IOException {

        switch (assessmentStatus) {
            case NEW:
                AssetResponseDTO asset = assetMongoRepository.findAssetWithMetaDataById(unitId, assessment.getAssetId());
                if (!Optional.ofNullable(asset).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assessment.getAssetId());
                }

                Map<String, Object> props = objectMapper.convertValue(asset, Map.class);
                for (MasterQuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (MasterQuestionBasicResponseDTO assetAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (props.containsKey(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            assetAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(props.get(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value));
                        }
                    }
                }
                break;
            case INPROGRESS:
                List<AssetAssessmentAnswer> assetAssessmentAnswers = assessment.getAssetAssessmentAnswers();
                Map<AssetAttributeName, Object> assetAttributeNameObjectMap = new HashMap<>();
                assetAssessmentAnswers.forEach(assetAssessmentAnswer -> assetAttributeNameObjectMap.put(assetAssessmentAnswer.getAssetField(), assetAssessmentAnswer.getValue()));

                for (MasterQuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (MasterQuestionBasicResponseDTO assetAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
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
     * @throws IOException
     */
    private void getProcessingActivityAssessmentQuestionAndValuesById(Long unitId, AssessmentStatus assessmentStatus, Assessment assessment, List<MasterQuestionnaireSectionResponseDTO> assessmentQuestionnaireSections) throws IOException {

        switch (assessmentStatus) {
            case NEW:
                ProcessingActivityResponseDTO processingActivity = processingActivityMongoRepository.getProcessingActivityAndMetaDataById(unitId, assessment.getProcessingActivityId());
                if (!Optional.ofNullable(processingActivity).isPresent()) {
                    exceptionService.dataNotFoundByIdException("message.dataNotFound", "Asset", assessment.getProcessingActivityId());
                }
                Map<String, Object> props = objectMapper.convertValue(processingActivity, Map.class);
                for (MasterQuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (MasterQuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (props.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(props.get(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName())));
                        }
                    }
                }
                break;
            case INPROGRESS:
                List<ProcessingActivityAssessmentAnswer> processingActivityAssessmentAnswers = assessment.getProcessingActivityAssessmentAnswers();
                Map<ProcessingActivityAttributeName, Object> processingActivityAttributeNameObjectMap = new HashMap<>();
                processingActivityAssessmentAnswers.forEach(processingActivityAssessmentAnswer -> processingActivityAttributeNameObjectMap.put(processingActivityAssessmentAnswer.getProcessingActivityField(), processingActivityAssessmentAnswer.getValue()));
                for (MasterQuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (MasterQuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
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
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.dataNotFoundByIdException("message.dataNotFound", "Assessment", assessmentId);
        }
        switch (assessmentStatus) {
            case INPROGRESS:
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
                        List<AssetAssessmentAnswer> assessmentAnswersForAsset = assessment.getAssetAssessmentAnswers();
                        assessmentAnswersForAsset.forEach(assetAssessmentAnswer -> saveAssessmentAnswerForAsset(assetAssessmentAnswer.getAssetField(), assetAssessmentAnswer.getValue(), asset));
                        assetMongoRepository.save(asset);

                    } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {
                        ProcessingActivity processingActivity = processingActivityMongoRepository.findByIdAndNonDeleted(unitId, assessment.getAssetId());
                        List<ProcessingActivityAssessmentAnswer> assessmentAnswersForProcessingActivity = assessment.getProcessingActivityAssessmentAnswers();

                        assessmentAnswersForProcessingActivity.forEach(processingActivityAssessmentAnswer
                                -> saveAssessmentAnswerForProcessingActivity(processingActivityAssessmentAnswer.getProcessingActivityField(), processingActivityAssessmentAnswer.getValue(), processingActivity));
                        processingActivityMongoRepository.save(processingActivity);

                    }
                }
                break;
            case NEW:
                if (assessment.getAssessmentStatus().equals(AssessmentStatus.INPROGRESS) || assessment.getAssessmentStatus().equals(AssessmentStatus.COMPLETED)) {
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
     */
    public List<AssessmentResponseDTO> getAllLaunchedAssessmentOfAssignee(Long unitId) {
        return assessmentMongoRepository.getAllLaunchedAssessmentAssignToRespondent(unitId);
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
            exceptionService.invalidRequestException("message.assessment.change.status", AssessmentStatus.INPROGRESS);
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
     * @param assetAttributeValue asset value corressponding to field
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
     * @param processingActivityAttributeValue processing activity  value corressponding to field
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
