package com.kairos.persistence.repository.data_inventory.processing_activity;

import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivity;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

@Repository
////@JaversSpringDataAuditable
public interface ProcessingActivityRepository extends CustomGenericRepository<ProcessingActivity> {

    @Query(value = "Select PA from ProcessingActivity PA where PA.organizationId = ?1 and PA.id IN (?2) and PA.deleted = false")
    List<ProcessingActivity> findSubProcessingActivitiesByIdsAndOrganisationId(Long orgId, Set<Long> ids);

    @Query(value = "Select PA from ProcessingActivity PA where PA.id = ?1 and PA.organizationId = ?2 and PA.processingActivity.id = ?3 and PA.deleted = false and PA.isSubProcessingActivity = true")
    ProcessingActivity findByIdAndOrganizationIdAndProcessingActivityId(Long id, Long orgId, Long parentId);

    @Modifying
    @Transactional
    @Query(value = "Update ProcessingActivity PA set PA.deleted = false, PA.processingActivity = null where PA.id = ?1 and PA.organizationId = ?2 and PA.processingActivity.id = ?3 and PA.deleted = false and PA.isSubProcessingActivity = true")
    Integer unlinkSubProcessingActivityFromProcessingActivity(Long id, Long orgId, Long parentId);

    @Query(value = "SELECT PA FROM ProcessingActivity PA WHERE PA.id = ?1 and PA.organizationId = ?2 and PA.isSubProcessingActivity = ?3 and PA.deleted = false")
    ProcessingActivity findByIdAndOrganizationIdAndDeletedAndIsSubProcessingActivity(Long id, Long orgId, boolean isSubProcessingActivity);

    @Modifying
    @Transactional
    @Query(value = "update ProcessingActivity PA set PA.active = ?3 where PA.organizationId = ?1 and PA.id = ?2 and PA.deleted = false")
    Integer updateProcessingActivityStatus(Long orgId, Long processActivityId, boolean active);



    @Query(value = "Select name from ProcessingActivity where organizationId = ?1 and responsibilityType.id = ?2 and deleted = false")
    List<String> findAllProcessingActivityLinkedWithResponsibilityType(Long orgId, Long responsibilityTypeId);

    @Query(value = "Select PA.name from processing_activitymd PA INNER JOIN processing_activitymd_processing_purposes PP ON PA.id = PP.processing_activitymd_id where PA.organization_id = ?1 and PP.processing_purposes_id = ?2 and PA.deleted = false", nativeQuery = true)
    List<String> findAllProcessingActivityLinkedWithProcessingPurpose(Long orgId, Long processingPurposeId);

    @Query(value = "Select PA.name from processing_activitymd PA INNER JOIN processing_activitymd_data_sources DS ON PA.id = DS.processing_activitymd_id where PA.organization_id = ?1 and DS.data_sources_id = ?2 and PA.deleted = false", nativeQuery = true)
    List<String> findAllProcessingActivityLinkedWithDataSource(Long orgId, Long dataSourceId);

    @Query(value = "Select PA.name from processing_activitymd PA INNER JOIN processing_activitymd_transfer_methods TM ON PA.id = TM.processing_activitymd_id where PA.organization_id = ?1 and TM.transfer_methods_id = ?2 and PA.deleted = false", nativeQuery = true)
    List<String> findAllProcessingActivityLinkedWithTransferMethod(Long orgId, Long transferMethodId);

    @Query(value = "Select PA.name from processing_activitymd PA INNER JOIN processing_activitymd_processing_legal_basis LB ON PA.id = LB.processing_activitymd_id where PA.organization_id = ?1 and LB.processing_legal_basis_id = ?2 and PA.deleted = false", nativeQuery = true)
    List<String> findAllProcessingActivityLinkedWithProcessingLegalBasis(Long orgId, Long legalBasisId);

    @Query(value = "Select PA.name from processing_activitymd PA INNER JOIN processing_activitymd_accessor_parties AP ON PA.id = AP.processing_activitymd_id where PA.organization_id = ?1 and AP.accessor_parties_id = ?2 and PA.deleted = false", nativeQuery = true)
    List<String> findAllProcessingActivityLinkedWithAccessorParty(Long orgId, Long accessorPartyId);

}
