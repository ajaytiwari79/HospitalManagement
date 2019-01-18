package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.persistence.model.data_inventory.asset.AssetMD;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
//@JaversSpringDataAuditable
public interface AssetRepository extends CustomGenericRepository<AssetMD> {

    @Query(value = "Select name from AssetMD where organizationId = ?1 and deleted = false and (assetType.id  = ?2 OR subAssetType.id = ?2)")
    List<String> findAllAssetLinkedWithAssetType(Long orgId, Long assetTypeId);


    @Query(value = "Select asset from #{#entityName} asset WHERE asset.organizationId = ?1 and asset.deleted = false and asset.active =  true")
    List<AssetMD> findAllActiveAssetByOrganizationId(Long orgId);

    @Query(value = "Select name from AssetMD where organizationId = ?1 and dataDisposal.id = ?2 and deleted = false")
    List<String> findAllAssetLinkedWithDataDisposal(Long orgId, Long disposalId);

    @Query(value = "Select AT.name from assetmd AT INNER JOIN assetmd_org_security_measures OSM ON AT.id = OSM.assetmd_id where AT.organization_id = ?1 and OSM.org_security_measures_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String> findAllAssetLinkedWithOrganizationalSecurityMeasure(Long orgId, Long securityMeasureId);

    @Query(value = "Select AT.name from assetmd AT INNER JOIN assetmd_storage_formats SF ON AT.id = SF.assetmd_id where AT.organization_id = ?1 and SF.storage_formats_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String> findAllAssetLinkedWithStorageFormat(Long orgId, Long storageId);

    @Query(value = "Select AT.name from assetmd AT INNER JOIN assetmd_technical_security_measures TSM ON AT.id = TSM.assetmd_id where AT.organization_id = ?1 and TSM.technical_security_measures_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String>findAllAssetLinkedWithTechnicalSecurityMeasure(Long orgId, Long technicalSecurityMeasureId);


    /*@Query("{organizationId:?0,_id:?1,deleted:false}")
    Asset findByIdAndNonDeleted(Long organizationId, BigInteger id);

    Asset findByid(BigInteger id);

    @Query("{organizationId:?0,deleted:false,active:true}")
    List<AssetBasicResponseDTO> getAllAssetWithBasicDetailByStatus(Long unitId, boolean active);

    @Query(value = "{organizationId:?0,deleted:false,active:true,dataDisposal:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithDataDisposal(Long unitId, BigInteger dataDisposalId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,assetTypeId:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithAssetType(Long unitId, BigInteger assetTypeId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,assetSubTypeId:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithAssetSubType(Long unitId, BigInteger subAssetTypeId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,hostingType:?1}", fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithHostingType(Long unitId, BigInteger dataDisposalId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,hostingProvider:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithHostingProvider(Long unitId, BigInteger hostingProviderId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,technicalSecurityMeasures:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO>findAllAssetLinkedWithTechnicalSecurityMeasure(Long unitId, BigInteger technicalSecurityMeasureId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,storageFormats:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithStorageFormat(Long unitId, BigInteger storageFormatId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,orgSecurityMeasures:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithOrganizationalSecurityMeasure(Long unitId, BigInteger orgSecurityMeasureId);*/



}
