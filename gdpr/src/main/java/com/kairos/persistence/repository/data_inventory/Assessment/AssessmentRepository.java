package com.kairos.persistence.repository.data_inventory.Assessment;


import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
////@JaversSpringDataAuditable
public interface AssessmentRepository extends JpaRepository<Assessment, Long> ,CustomAssessmentRepository {

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedAssessmentByUnitIdAndAssetId(Long orgId, Long assetId, List<AssessmentStatus> status, boolean isRiskAssessment);

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedAssessmentByUnitIdAndAssetId();

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = ?2 and lower(assessment.name) = lower(?3)")
    Assessment findByOrganizationIdAndDeletedAndName(Long orgId, String name);

    @Query(value = "Select assessment from Assessment assessment where assessment.organizationId = ?1 and assessment.processingActivity.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    Assessment findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(Long orgId, Long processingActivityId, List<AssessmentStatus> status, boolean isRiskAssessment);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = ?2 and assessment.id = ?3")
    Assessment findByOrganizationIdAndDeletedAndId(Long orgId, boolean deleted, Long id);

    @Query(value = "SELECT assessment FROM assessmentmd assessment JOIN assessmentmd_assignee_list staff ON assessment.id = assessmentmd_assignee_list.assessmentmd_id WHERE assessment.organization_id = ?1 and staff.staff_id = ?2 and assessment.assessment_status IN (?3)", nativeQuery = true)
    List<Assessment> getAllAssessmentByUnitIdAndStaffId(Long unitId, Long staffId, List<AssessmentStatus> status);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.deleted = false")
    List<Assessment> getAllAssessmentByUnitId(Long unitId);

    @Query(value = "SELECT assessment FROM Assessment assessment WHERE assessment.organizationId = ?1 and assessment.id = ?2 and assessment.assessmentStatus = ?3 and assessment.deleted = false")
    Assessment findByUnitIdAndIdAndAssessmentStatus(Long unitId, Long assessmentId, AssessmentStatus status);



}
