package com.kairos.service.data_inventory.assessment;


import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            exceptionService.duplicateDataException("message.duplicate", "Assessment", assessmentId);
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
                ObjectMapper mapValuesAndField = new ObjectMapper();
                Map<String, Object> props = mapValuesAndField.convertValue(asset, Map.class);
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
                        if (assetAttributeNameObjectMap.containsKey(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            assetAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(assetAttributeNameObjectMap.get(AssetAttributeName.valueOf(assetAssessmentQuestionBasicResponseDTO.getAttributeName()).value));
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
                ObjectMapper mapValuesAndField = new ObjectMapper();
                Map<String, Object> props = mapValuesAndField.convertValue(processingActivity, Map.class);
                for (MasterQuestionnaireSectionResponseDTO questionnaireSectionResponseDTO : assessmentQuestionnaireSections) {
                    for (MasterQuestionBasicResponseDTO processingActivityAssessmentQuestionBasicResponseDTO : questionnaireSectionResponseDTO.getQuestions()) {
                        if (props.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
                            processingActivityAssessmentQuestionBasicResponseDTO.setAssessmentQuestionValues(props.get(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value));
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
                        if (processingActivityAttributeNameObjectMap.containsKey(ProcessingActivityAttributeName.valueOf(processingActivityAssessmentQuestionBasicResponseDTO.getAttributeName()).value)) {
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
     * @return
     */
    public List<AssessmentResponseDTO> getAllLaunchAssessment(Long unitId) {
        return assessmentMongoRepository.getAllLaunchAssessmentAssignToRespondent(unitId);
    }


    /**
     * @param unitId
     * @param assessmentId
     * @param assessmentAnswerValueObject
     * @return
     */
    public AssessmentAnswerValueObject addAssessmentAnswerForAssetOrProcessingActivity(Long unitId, BigInteger assessmentId, AssessmentAnswerValueObject assessmentAnswerValueObject) {

        Assessment assessment = assessmentMongoRepository.findByIdAndNonDeleted(unitId, assessmentId);
        if (!Optional.ofNullable(assessment).isPresent()) {
            exceptionService.duplicateDataException("message.duplicate", "Assessment", assessmentId);
        }
        if (Optional.ofNullable(assessment.getAssetId()).isPresent()) {
            if (Optional.ofNullable(assessmentAnswerValueObject.getAssetAssessmentAnswers()).isPresent()) {
                assessment.setAssetAssessmentAnswers(assessmentAnswerValueObject.getAssetAssessmentAnswers());

            } else {
                exceptionService.invalidRequestException("Unable to create Assessment for Asset " + assessment.getRelatedAssetOrProcessingActivityName() + "");
            }
        } else if (Optional.ofNullable(assessment.getProcessingActivityId()).isPresent()) {
            if (Optional.ofNullable(assessmentAnswerValueObject.getProcessingActivityAssessmentAnswers()).isPresent()) {

            } else {
                exceptionService.invalidRequestException("Unable to create Assessment for Processing Activity " + assessment.getRelatedAssetOrProcessingActivityName() + "");
            }
        }
        assessmentMongoRepository.save(assessment);
        return assessmentAnswerValueObject;

    }


}
