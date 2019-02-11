package com.kairos.persistence.repository.master_data.asset_management;


import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
////@JaversSpringDataAuditable
public interface AssetTypeRepository extends JpaRepository<AssetType,Long> {

    @Query(value = "SELECT at FROM AssetType at WHERE at.countryId = ?2 and at.deleted = false and lower(at.name) = lower(?1) and at.subAssetType = ?3")
    AssetType findByNameAndCountryIdAndSubAssetType(String name, Long countryId, boolean hasSubAssetType);

    @Query(value = "SELECT at FROM AssetType at WHERE at.id =?1 and at.countryId = ?3 and at.deleted = false and lower(at.name) = lower(?2) and at.subAssetType = ?4")
    AssetType findByIdAndNameAndCountryIdAndSubAssetType(Long id, String name, Long countryId, boolean hasSubAssetType);


    @Query(value = "SELECT at FROM AssetType at WHERE at.id = ?1 and at.countryId = ?2 and at.deleted = false")
    AssetType findByIdAndCountryIdAndDeleted(Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposal set name = ?1 where id= ?2")
    Integer updateMasterAssetTypeName(String name, Long id);

    @Query(value = "SELECT at FROM AssetType at WHERE at.countryId = ?1 and at.subAssetType = false and at.deleted = false")
    List<AssetType> getAllAssetTypes(Long countryId);


    @Query(value = "SELECT at FROM AssetType at WHERE at.countryId = ?1 and at.deleted = false and at.id = ?2 and at.subAssetType = ?3")
    AssetType findByCountryIdAndId(Long countryId, Long id, boolean subAssetType);

    @Query(value = "SELECT at FROM AssetType at WHERE at.organizationId = ?2 and at.deleted = false and lower(at.name) = lower(?1) and at.subAssetType = ?3")
    AssetType findByNameAndOrganizationIdAndSubAssetType(String name, Long orgId, boolean subAssetType);

    @Query(value = "SELECT at FROM AssetType at WHERE at.id =?1 and at.organizationId = ?3 and at.deleted = false and lower(at.name) = lower(?2) and at.subAssetType = ?4")
    AssetType findByIdAndNameAndOrganizationIdAndSubAssetType(Long id, String name, Long countryId, boolean hasSubAssetType);


    @Query(value = "SELECT at FROM AssetType at WHERE at.id = ?1 and at.organizationId = ?2 and at.deleted = false")
    AssetType findByIdAndOrganizationIdAndDeleted(Long id, Long countryId);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposal set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateAssetTypeName(String name, Long id, Long orgId);

    @Query(value = "SELECT at FROM AssetType at WHERE at.organizationId = ?1 and at.subAssetType = false and at.deleted = false")
    List<AssetType> getAllAssetTypesByOrganization(Long orgId);


    @Query(value = "SELECT at FROM AssetType at WHERE at.organizationId = ?1 and at.deleted = false and at.id = ?2 and at.subAssetType = ?3")
    AssetType findByOrganizationIdIdAndId(Long orgId, Long id, boolean hasSubAssetType);

    @Query(value = "SELECT at FROM AssetType at WHERE at.organizationId = ?3 and at.deleted = false and at.id = ?1 and at.assetType.id = ?2 and at.subAssetType = true")
    AssetType findByIdAndOrganizationIdAndAssetTypeAndDeleted(Long id, Long assetTypeId, Long orgId);


    @Query(value = "SELECT at FROM AssetType at WHERE at.countryId = ?3 and at.deleted = false and at.id = ?1 and at.assetType.id = ?2 and at.subAssetType = true")
    AssetType findByIdAndCountryIdAndAssetTypeAndDeleted(Long id, Long assetTypeId, Long countryId);

    @Query(value = "SELECT at FROM AssetType at WHERE at.deleted = false and at.id = ?1")
    AssetType findByIdAndDeletedFalse(Long id);

    @Query(value = "SELECT at FROM AssetType at WHERE at.id IN (?1) and at.deleted = false and at.subAssetType = ?2")
    List<AssetType>  findAllByIds(List<Long> ids, boolean subAssetType);

}
