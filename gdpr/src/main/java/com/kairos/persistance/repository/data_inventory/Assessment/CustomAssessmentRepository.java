package com.kairos.persistance.repository.data_inventory.Assessment;

import com.kairos.persistance.model.data_inventory.assessment.Assessment;

public interface CustomAssessmentRepository {

    Assessment findAssessmentByNameAndUnitId(Long unitId,String name);



}
