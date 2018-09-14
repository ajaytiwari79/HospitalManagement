package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AssetMongoRepository extends MongoBaseRepository<Asset,BigInteger>,CustomAssetRepository {


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    Asset findByIdAndNonDeleted(Long organizationId,BigInteger id);

    Asset findByid(BigInteger id);

    @Query("{organizationId:?0,deleted:false,active:true}")
    List<AssetBasicResponseDTO> getAllAssetWithBasicDetailByStatus(Long unitId, boolean active);

    @Query(value = "{organizationId:?0,deleted:false,active:true,dataDisposal:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithDataDisposal(Long unitId, BigInteger dataDisposalId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,assetType:?1}",fields = "{name:1,_id:0}")
    List<AssetBasicResponseDTO> findAllAssetLinkedWithAssetType(Long unitId, BigInteger assetTypeId);

    @Query(value = "{organizationId:?0,deleted:false,active:true,assetSubTypes:?1}",fields = "{name:1,_id:0}")
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
    List<AssetBasicResponseDTO> findAllAssetLinkedWithOrganizationalSecurityMeasure(Long unitId, BigInteger orgSecurityMeasureId);




}
