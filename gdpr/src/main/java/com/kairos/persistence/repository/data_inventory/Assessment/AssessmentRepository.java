package com.kairos.persistence.repository.data_inventory.Assessment;


import com.kairos.enums.gdpr.AssessmentStatus;
import com.kairos.persistence.model.data_inventory.assessment.Assessment;
import com.kairos.persistence.model.data_inventory.assessment.AssessmentMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface AssessmentRepository extends JpaRepository<AssessmentMD, Long> {

    @Query(value = "Select assessment from AssessmentMD assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    AssessmentMD findPreviousLaunchedAssessmentByUnitIdAndAssetId(Long orgId, Long assetId, List<AssessmentStatus> status, boolean isRiskAssessment);

    @Query(value = "Select assessment from AssessmentMD assessment where assessment.organizationId = ?1 and assessment.asset.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    AssessmentMD findPreviousLaunchedAssessmentByUnitIdAndAssetId();

    @Query(value = "SELECT assessment FROM AssessmentMD assessment WHERE assessment.organizationId = ?1 and assessment.deleted = ?2 and lower(assessment.name) = lower(?3)")
    AssessmentMD findByOrganizationIdAndDeletedAndName(Long orgId, boolean deleted, String name);

    @Query(value = "Select assessment from AssessmentMD assessment where assessment.organizationId = ?1 and assessment.processingActivity.id = ?2 and assessment.assessmentStatus IN (?3) and assessment.isRiskAssessment = ?4 and assessment.deleted = false")
    AssessmentMD findPreviousLaunchedRiskAssessmentByUnitIdAndProcessingActivityId(Long orgId, Long processingActivityId, List<AssessmentStatus> status, boolean isRiskAssessment);

   /* @Query("{deleted:false,organizationId:?0,_id:?1}")
    Assessment findByUnitIdAndId(Long unitId, BigInteger assessmentId);

    @Query("{deleted:false,organizationId:?0,_id:?1,assessmentStatus:?2}")
    Assessment findByUnitIdAndIdAndAssessmentStatus(Long unitId, BigInteger assessmentId, AssessmentStatus assessmentStatus);
*/


}
