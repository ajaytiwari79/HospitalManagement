package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
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

    @Query("{organizationId:?0,deleted:false,active:true,dataDisposal:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithDataDisposal(Long unitId, BigInteger dataDisposalId);

    @Query("{organizationId:?0,deleted:false,active:true,hostingType:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithHostingType(Long unitId, BigInteger dataDisposalId);

    @Query("{organizationId:?0,deleted:false,active:true,hostingProvider:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithHostingProvider(Long unitId, BigInteger hostingProviderId);

    @Query("{organizationId:?0,deleted:false,active:true,technicalSecurityMeasures:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithTechnicalSecurityMeasure(Long unitId, BigInteger technicalSecurityMeasureId);

    @Query("{organizationId:?0,deleted:false,active:true,storageFormats:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithStorageFormat(Long unitId, BigInteger storageFormatId);

    @Query("{organizationId:?0,deleted:false,active:true,orgSecurityMeasures:?1},{name:1,_id:0}")
    List<String> findAllAssetLinkedWithOrganizationalSecurityMeasure(Long unitId, BigInteger orgSecurityMeasureId);




}
