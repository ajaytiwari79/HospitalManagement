package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.master_data.MasterAssetBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterAssetMongoRepository extends MongoBaseRepository<MasterAsset,BigInteger>,CustomMasterAssetRepository{


    @Query("{countryId:?0,_id:?1,deleted:false}")
    MasterAsset findByIdANdNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<MasterAsset> findAllMasterAssets( Long countryId);

    @Query("{countryId:?0,assetType:?1,deleted:false}")
    List<MasterAssetBasicResponseDTO> findAllMasterAssetByAssetType(Long countryId, BigInteger assetTypeId);

    MasterAsset findByid(BigInteger id);




}
