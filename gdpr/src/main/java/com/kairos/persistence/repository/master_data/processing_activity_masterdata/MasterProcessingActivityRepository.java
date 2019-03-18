package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

////@JaversSpringDataAuditable
@Repository
public interface MasterProcessingActivityRepository extends JpaRepository<MasterProcessingActivity, Long> {


    @Query(value = "Select MPA from MasterProcessingActivity MPA where MPA.countryId = ?2 and lower(MPA.name) = lower(?1) and MPA.deleted = false")
    MasterProcessingActivity findByNameAndCountryId(String name, Long countryId);

    @Query(value = "Select MPA from MasterProcessingActivity MPA where MPA.countryId = ?1 and MPA.deleted = false and MPA.subProcessActivity = false")
    List<MasterProcessingActivity> findAllByCountryId(Long countryId);

    @Query(value = "Select MPA from MasterProcessingActivity MPA where MPA.countryId = ?1 and MPA.id in (?2) and MPA.deleted = false")
    List<MasterProcessingActivity> findAllByCountryIdAndIds(Long countryId, Set<Long> ids);

    @Query(value = "Select MPA from MasterProcessingActivity MPA where MPA.countryId = ?1 and MPA.id = ?2 and MPA.deleted = false")
    MasterProcessingActivity findByCountryIdAndId(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterProcessingActivity set deleted = true where countryId = ?1 and id = ?2 and deleted = false")
    Integer updateMasterProcessingActivity(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterProcessingActivity set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateMasterMasterProcessingActivityStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Transactional
    @Modifying
    @Query(value = "Update master_processing_activity set deleted = true, master_processing_activity_id = null where countryId = ?1 and master_processing_activity_id = ?2 and id = ?3", nativeQuery = true)
    Integer deleteSubProcessingActivityFromMasterProcessingActivity(Long countryId, Long processingActivityId, Long subProcessingActivityId);

    @Query(value = "Select MPA from MasterProcessingActivity MPA JOIN MPA.organizationTypes OT JOIN MPA.organizationSubTypes OST JOIN MPA.organizationServices SC JOIN MPA.organizationSubServices SSC where MPA.countryId = ?1 and MPA.deleted = false and OT.id IN (?2) and OST.id IN (?3) and SC.id IN (?4) and SSC.id IN (?5)")
    List<MasterProcessingActivity> findAllByCountryIdAndOrganizationalMetadata(Long countryId, List<Long> organizationTypeIds, List<Long> organizationSubTypeIds, List<Long> organizationServiceCategoryIds, List<Long> organizationSubServiceCategoryTypeIds);
}
