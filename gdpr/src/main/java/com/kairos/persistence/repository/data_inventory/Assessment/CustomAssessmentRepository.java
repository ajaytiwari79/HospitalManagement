package com.kairos.persistence.repository.data_inventory.Assessment;

import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.response.dto.common.AssessmentBasicResponseDTO;

import java.math.BigInteger;
import java.util.List;

public interface CustomAssessmentRepository {

    Assessment findAssessmentByNameAndUnitId(Long unitId,String name);

    List<AssessmentBasicResponseDTO> getAllLaunchedAssessmentAssignToRespondent(Long unitId,Long loggedInUserId);

    Assessment findPreviousLaunchedAssessmentOfAssetByUnitId(Long unitId, BigInteger assetId);

    Assessment findPreviousLaunchedAssessmentOfProcessingActivityByUnitId(Long unitId, BigInteger processingActivityId);



}
