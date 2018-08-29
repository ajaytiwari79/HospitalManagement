package com.kairos.service.data_inventory.assessment;


import com.kairos.enums.QuestionnaireTemplateType;
import com.kairos.gdpr.data_inventory.AssessmentDTO;
import com.kairos.persistance.model.data_inventory.assessment.Assessment;
import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistance.repository.data_inventory.Assessment.AssessmentMongoRepository;
import com.kairos.persistance.repository.data_inventory.asset.AssetMongoRepository;
import com.kairos.persistance.repository.data_inventory.processing_activity.ProcessingActivityMongoRepository;
import com.kairos.persistance.repository.master_data.questionnaire_template.MasterQuestionnaireTemplateMongoRepository;
import com.kairos.service.common.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
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
                getMasterQuestionanaireTemplateIdListByTemplateType(countryId,  templateType);
        if (!Optional.ofNullable(questionnaireTemplateId).isPresent()) {
            exceptionService.invalidRequestException("message.questionnaire.template.Not.Found.For.Template.Type", templateType);
        }
        Assessment assessment = new Assessment(assessmentDTO.getName(), assessmentDTO.getEndDate(), assessmentDTO.getAssignee(), assessmentDTO.getApprover());
        assessment.setOrganizationId(unitId);
        assessment.setComment(assessmentDTO.getComment());
        assessment.setQuestionnaireTemplateId(questionnaireTemplateId);
        return assessment;

    }


}
