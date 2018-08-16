package com.kairos.persistance.repository.data_inventory.asset;

import com.kairos.persistance.model.data_inventory.asset.Asset;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.data_inventory.AssetBasicResponseDTO;
import com.kairos.response.dto.data_inventory.AssetResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
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

    @Query("{organizationId:?0,processingActivities:?1,deleted:false}")
    List<AssetResponseDTO> getAllAssetRelatedToProcessingActivityById(Long unitId,BigInteger processingActivityId);

    @Query("{organizationId:?0,subProcessingActivities:?1,deleted:false}")
    List<AssetResponseDTO> getAllAssetRelatedToSubProcessingActivityById(Long unitId,BigInteger subProcessingActivityId);

    @Query("{organizationId:?0,deleted:false,active:?1}")
    List<AssetBasicResponseDTO> getAllAssetWithBasicDetailByStatus(Long unitId, boolean active);

}
