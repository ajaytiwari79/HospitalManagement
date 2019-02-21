package com.kairos.persistence.repository.data_inventory.Assessment;

import com.kairos.persistence.model.data_inventory.assessment.Assessment;

import java.util.List;

public interface CustomAssessmentRepository {



    List<Assessment> findByOrgIdAndQuestionnaireTemplateIdAndAssessmentStatusNewOrInProgress(Long orgId, Long questionnaireTemplateId);



}
