package com.kairos.persistence.repository.data_inventory.Assessment;


import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.enums.gdpr.QuestionnaireTemplateStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AssessmentRepository extends JpaRepository<Assessment, Long> ,CustomAssessmentRepository {

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedAssessmentByUnitIdAndAssetId(Long orgId, Long assetId, List<AssessmentStatus> status, boolean isRiskAssessment);

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedAssessmentByUnitIdAndAssetId();

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = false and lower(assessment.name) = lower(?2)")
    Assessment findByOrganizationIdAndDeletedAndName(Long orgId, String name);

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.processingActivity.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(Long orgId, Long processingActivityId, List<AssessmentStatus> status, boolean isRiskAssessment);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = false and assessment.id = ?2")
    Assessment findByOrganizationIdAndId(Long orgId, Long id);

    @Query(value = "SELECT assessment FROM assessment assessment JOIN assessment_assignee_list staff ON assessment.id = assessment_assignee_list.assessment_id WHERE assessment.organization_id = ?1 and staff.staff_id = ?2 and assessment.assessment_status IN (?3)", nativeQuery = true)
    List<Assessment> getAllAssessmentByUnitIdAndStaffId(Long organizationId, Long staffId, List<AssessmentStatus> status);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = false")
    List<Assessment> getAllAssessmentByUnitId(Long organizationId);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.id = ?2 and assessment.assessmentStatus = ?3 and assessment.deleted = false")
    Assessment findByUnitIdAndIdAndAssessmentStatus(Long organizationId, Long assessmentId, AssessmentStatus status);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.processingActivity.id = ?2 and assessment.deleted = false")
    List<Assessment> findAllProcessingActivityAssessmentByActivityIdAndUnitId(Long organizationId, Long processingActivityId);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.deleted = false")
    List<Assessment> findAllAssetAssessmentByAssetIdAndUnitId(Long organizationId, Long assetId);

    @Query(value = "SELECT assessment.name FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.questionnaireTemplate.id = ?2 and assessment.assessmentStatus =?3 and assessment.deleted = false")
    List<String> findAllNamesByUnitIdQuestionnaireTemplateIdAndStatus(Long orgId, Long questionnaireTemplateId, AssessmentStatus assessmentStatus);

}
