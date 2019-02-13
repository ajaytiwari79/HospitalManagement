package com.kairos.persistence.repository.data_inventory.asset;

import com.kairos.persistence.model.data_inventory.asset.Asset;
import com.kairos.persistence.repository.master_data.processing_activity_masterdata.CustomGenericRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AssetRepository extends CustomGenericRepository<Asset> {

    @Query(value = "Select name from Asset where organizationId = ?1 and deleted = false and (assetType.id  = ?2 OR subAssetType.id = ?2)")
    List<String> findAllAssetLinkedWithAssetType(Long orgId, Long assetTypeId);


    @Query(value = "Select asset from #{#entityName} asset WHERE asset.organizationId = ?1 and asset.deleted = false and asset.active =  true")
    List<Asset> findAllActiveAssetByOrganizationId(Long orgId);

    @Query(value = "Select name from Asset where organizationId = ?1 and dataDisposal.id = ?2 and deleted = false")
    List<String> findAllAssetLinkedWithDataDisposal(Long orgId, Long disposalId);

    @Query(value = "Select AT.name from asset AT INNER JOIN asset_org_security_measures OSM ON AT.id = OSM.asset_id where AT.organization_id = ?1 and OSM.org_security_measures_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String> findAllAssetLinkedWithOrganizationalSecurityMeasure(Long orgId, Long securityMeasureId);

    @Query(value = "Select AT.name from asset AT INNER JOIN asset_storage_formats SF ON AT.id = SF.asset_id where AT.organization_id = ?1 and SF.storage_formats_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String> findAllAssetLinkedWithStorageFormat(Long orgId, Long storageId);

    @Query(value = "Select AT.name from asset AT INNER JOIN asset_technical_security_measures TSM ON AT.id = TSM.asset_id where AT.organization_id = ?1 and TSM.technical_security_measures_id = ?2 and AT.deleted = false", nativeQuery = true)
    List<String>findAllAssetLinkedWithTechnicalSecurityMeasure(Long orgId, Long technicalSecurityMeasureId);


}
