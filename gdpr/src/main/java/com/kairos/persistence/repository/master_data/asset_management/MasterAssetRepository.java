package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
////@JaversSpringDataAuditable
@Repository
public interface MasterAssetRepository extends JpaRepository<MasterAsset,Long>{


    @Query(value = "Select MA from MasterAsset MA where MA.countryId = ?2 and lower(MA.name) = lower(?1) and MA.deleted = false")
    MasterAsset findByNameAndCountryId(String name, Long countryId);

    @Query(value = "Select MA from MasterAsset MA where MA.countryId = ?1 and MA.deleted = false")
    List<MasterAsset> findAllByCountryId(Long countryId);

    @Query(value = "Select MA from MasterAsset MA where MA.countryId = ?1 and MA.id = ?2 and MA.deleted = false")
    MasterAsset getMasterAssetByCountryIdAndId(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterAsset set deleted = true where countryId = ?1 and id = ?2 and deleted = false")
    Integer updateMasterAsset(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterAsset set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateMasterAssetStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);

    @Query(value = "Select MA.name from MasterAsset MA where MA.countryId = ?1 and MA.assetType.id = ?2 and MA.deleted = false")
    List<String> findMasterAssetsLinkedWithAssetType(Long countryId, Long assetTypeId);
}
