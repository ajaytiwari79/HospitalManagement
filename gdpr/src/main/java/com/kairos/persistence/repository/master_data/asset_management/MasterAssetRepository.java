package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.enums.gdpr.SuggestedDataStatus;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.model.master_data.default_asset_setting.MasterAssetMD;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.MasterAssetResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
//@JaversSpringDataAuditable
@Repository
public interface MasterAssetRepository extends JpaRepository<MasterAssetMD,Long>{


    @Query(value = "Select MA from MasterAssetMD MA where MA.countryId = ?2 and lower(MA.name) = lower(?1) and MA.deleted = false")
    MasterAssetMD findByNameAndCountryId(String name, Long countryId);

    @Query(value = "Select MA from MasterAssetMD MA where MA.countryId = ?1 and MA.deleted = false")
    List<MasterAssetMD> findAllByCountryId(Long countryId);

    @Query(value = "Select MA from MasterAssetMD MA where MA.countryId = ?1 and MA.id = ?2 and MA.deleted = false")
    MasterAssetMD getMasterAssetByCountryIdAndId(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterAssetMD set deleted = true where countryId = ?1 and id = ?2 and deleted = false")
    Integer updateMasterAsset(Long countryId, Long id);

    @Transactional
    @Modifying
    @Query(value = "update MasterAssetMD set suggestedDataStatus = ?3 where countryId = ?1 and id IN (?2) and deleted = false")
    Integer updateMasterAssetStatus(Long countryId, Set<Long> ids, SuggestedDataStatus status);
}
