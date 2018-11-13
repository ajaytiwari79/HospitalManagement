package com.kairos.persistence.repository.data_inventory.Assessment;

import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;
import com.kairos.response.dto.common.AssessmentResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssessmentRepository {

    Assessment findAssessmentByNameAndUnitId(Long unitId, String name);

    List<AssessmentBasicResponseDTO> getAllAssessmentByUnitIdAndStaffId(Long unitId, Long staffId);

    List<AssessmentResponseDTO> getAllAssessmentByUnitId(Long unitId);

    Assessment findPreviousLaunchedAssessmentOfAssetByUnitId(Long unitId, BigInteger assetId);

    Assessment findPreviousLaunchedAssessmentOfProcessingActivityByUnitId(Long unitId, BigInteger processingActivityId);

    List<Assessment> getAssessmentLinkedWithQuestionnaireTemplateByTemplateIdAndUnitId(Long unitId, BigInteger templateId);

    Assessment findPreviousLaunchedAssessmentForAssetRisksByUnitId(Long unitId, BigInteger assetId);

    Assessment findPreviousLaunchedAssessmentForProcessingActivityRisksByUnitId(Long unitId, BigInteger processingActivityId);

    List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForAssetByAssetIdAndUnitId(Long unitId, BigInteger assetId);

    List<AssessmentBasicResponseDTO> findAllAssessmentLaunchedForProcessingActivityByActivityIdAndUnitId(Long unitId, BigInteger processingActivityId);


}
