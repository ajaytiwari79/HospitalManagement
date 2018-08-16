package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.MasterAssetBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterAssetMongoRepository extends MongoBaseRepository<MasterAsset,BigInteger>,CustomMasterAssetRepository{


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    MasterAsset findByIdANdNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<MasterAsset> findAllMasterAssets( Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,assetType:?2,deleted:false}")
    List<MasterAssetBasicResponseDTO> findAllMasterAssetByAssetType(Long countryId, Long organizationId, BigInteger assetTypeId);

    MasterAsset findByid(BigInteger id);




}
