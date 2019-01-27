package com.kairos.persistence.repository.master_data.asset_management;


import com.kairos.persistence.model.master_data.default_asset_setting.AssetTypeMD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface AssetTypeRepository extends JpaRepository<AssetTypeMD,Long> {

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?2 and at.deleted = false and lower(at.name) = lower(?1) and at.subAssetType = ?3")
    AssetTypeMD findByNameAndCountryIdAndSubAssetType(String name,Long countryId, boolean hasSubAssetType);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.id =?1 and at.countryId = ?3 and at.deleted = false and lower(at.name) = lower(?2) and at.subAssetType = ?4")
    AssetTypeMD findByIdAndNameAndCountryIdAndSubAssetType(Long id, String name,Long countryId, boolean hasSubAssetType);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.id = ?1 and at.countryId = ?2 and at.deleted = ?3")
    AssetTypeMD findByIdAndCountryIdAndDeleted(Long id, Long countryId, boolean deleted);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2")
    Integer updateMasterAssetTypeName(String name, Long id);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?1 and at.subAssetType = false and at.deleted = false")
    List<AssetTypeMD> getAllAssetTypes( Long countryId);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?1 and at.deleted = false and at.id = ?2 and at.subAssetType = ?3")
    AssetTypeMD findByCountryIdAndId(Long countryId, Long id, boolean subAssetType);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.organizationId = ?2 and at.deleted = false and lower(at.name) = lower(?1) and at.subAssetType = ?3")
    AssetTypeMD findByNameAndOrganizationIdAndSubAssetType(String name,Long orgId, boolean subAssetType);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.id =?1 and at.organizationId = ?3 and at.deleted = false and lower(at.name) = lower(?2) and at.subAssetType = ?4")
    AssetTypeMD findByIdAndNameAndOrganizationIdAndSubAssetType(Long id, String name,Long countryId, boolean hasSubAssetType);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.id = ?1 and at.organizationId = ?2 and at.deleted = ?3")
    AssetTypeMD findByIdAndOrganizationIdAndDeleted(Long id, Long countryId, boolean deleted);

    @Transactional
    @Modifying
    @Query(value = "update DataDisposalMD set name = ?1 where id= ?2 and organizationId = ?3")
    Integer updateAssetTypeName(String name, Long id, Long orgId);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.organizationId = ?1 and at.subAssetType = false and at.deleted = false")
    List<AssetTypeMD> getAllAssetTypesByOrganization( Long orgId);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.organizationId = ?1 and at.deleted = false and at.id = ?2 and at.subAssetType = ?3")
    AssetTypeMD findByOrganizationIdIdAndId(Long orgId, Long id, boolean hasSubAssetType);

    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.organizationId = ?3 and at.deleted = false and at.id = ?1 and at.assetType.id = ?2 and at.subAssetType = true")
    AssetTypeMD findByIdAndOrganizationIdAndAssetTypeAndDeleted(Long id, Long assetTypeId, Long orgId);


    @Query(value = "SELECT at FROM AssetTypeMD at WHERE at.countryId = ?3 and at.deleted = false and at.id = ?1 and at.assetType.id = ?2 and at.subAssetType = true")
    AssetTypeMD findByIdAndCountryIdAndAssetTypeAndDeleted(Long id, Long assetTypeId, Long countryId);



}
