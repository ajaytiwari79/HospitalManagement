package com.kairos.persistence.repository.master_data.asset_management;


import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface AssetTypeRepository extends JpaRepository<AssetTypeMD,Integer> {

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?2 and at.deleted = false and lower(at.name) = lower(?1) and subAssetType = ?3")
    AssetTypeMD findByNameAndCountryIdAndSubAssetType(String name,Long countryId, boolean hasSubAssetType);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.id = ?1 and at.countryId = ?2 and at.deleted = ?3")
    AssetTypeMD findByIdAndCountryIdAndDeleted(Integer id, Long countryId, boolean deleted);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2")
    Integer updateDataDisposalName(String name, Integer id);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?1 and at.subAssetType = false and deleted = false")
    List<AssetTypeMD> getAllAssetTypes( Long countryId);

}
