package com.kairos.persistance.repository.data_inventory.Assessment;

import com.kairos.persistance.model.data_inventory.assessment.Assessment;
import com.kairos.response.dto.data_inventory.AssessmentResponseDTO;

import java.util.List;

public interface CustomAssessmentRepository {

    Assessment findAssessmentByNameAndUnitId(Long unitId,String name);

    List<AssessmentResponseDTO> getAllLaunchedAssessmentAssignToRespondent(Long unitId);



}
