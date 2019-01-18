package com.kairos.persistence.repository.data_inventory.processing_activity;

import com.kairos.persistence.model.data_inventory.processing_activity.ProcessingActivityMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ProcessingActivityRepository extends CustomGenericRepository<ProcessingActivityMD> {

    @Query(value = "Select PA from ProcessingActivityMD PA where PA.organizationId = ?1 and PA.id IN (?2) and PA.deleted = false")
    List<ProcessingActivityMD> findSubProcessingActivitiesByIdsAndOrganisationId(Long orgId, Set<Long> ids);

    @Query(value = "Select PA from ProcessingActivityMD PA where PA.id = ?1 and PA.organizationId = ?2 and PA.processingActivity.id = ?3 and PA.deleted = false and PA.subProcessingActivity = true")
    ProcessingActivityMD findByIdAndOrganizationIdAndProcessingActivityId(Long id, Long orgId, Long parentId);

    @Modifying
    @Transactional
    @Query(value = "Update ProcessingActivityMD PA set PA.deleted = false, PA.processingActivity = null where PA.id = ?1 and PA.organizationId = ?2 and PA.processingActivity.id = ?3 and PA.deleted = false and PA.subProcessingActivity = true")
    Integer unlinkSubProcessingActivityFromProcessingActivity(Long id, Long orgId, Long parentId);

    @Modifying
    @Transactional
    @Query(value = "update ProcessingActivityMD PA set PA.active = ?3 where PA.organizationId = ?1 and PA.id = ?2 and PA.deleted = false")
    Integer updateProcessingActivityStatus(Long orgId, Long processActivityId, boolean active);
}
