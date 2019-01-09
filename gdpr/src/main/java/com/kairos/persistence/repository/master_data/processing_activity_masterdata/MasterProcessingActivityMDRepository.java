package com.kairos.persistence.repository.master_data.processing_activity_masterdata;

import com.kairos.persistence.model.master_data.default_proc_activity_setting.MasterProcessingActivityMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

//@JaversSpringDataAuditable
@Repository
public interface MasterProcessingActivityMDRepository extends JpaRepository<MasterProcessingActivityMD,Long>{


    @Query(value = "Select MPA from MasterProcessingActivityMD MPA where MPA.countryId = ?2 and lower(MPA.name) = lower(?1) and MPA.deleted = false")
    MasterProcessingActivityMD findByNameAndCountryId(String name, Long countryId);

    @Query(value = "Select MPA from MasterProcessingActivityMD MPA where MPA.countryId = ?1 and MPA.deleted = false and MPA.subProcessActivity = false")
    List<MasterProcessingActivityMD> findAllByCountryId(Long countryId);

    @Query(value = "Select MPA from MasterProcessingActivityMD MPA where MPA.countryId = ?1 and MPA.id = ?2 and MPA.deleted = false")
    MasterProcessingActivityMD getMasterAssetByCountryIdAndId(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterProcessingActivityMD set deleted = true where countryId = ?1 and id = ?2 and deleted = false")
    Integer updateMasterProcessingActivity(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterProcessingActivityMD set deleted = true where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateMasterMasterProcessingActivityStatus(Long countryId, Set<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "Update master_processing_activitymd set deleted = true, master_processing_activity_id = null where countryId = ?1 and master_processing_activity_id = ?2 and id = ?3", nativeQuery = true)
    Integer deleteSubProcessingActivityFromMasterProcessingActivity(Long countryId, Long processingActivityId, Long subProcessingActivityId);
}
