package com.kairos.persistence.repository.master_data.asset_management;

import com.kairos.persistence.model.master_data.default_asset_setting.MasterAsset;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface MasterAssetMongoRepository extends MongoBaseRepository<MasterAsset,BigInteger>,CustomMasterAssetRepository{


    @Query("{countryId:?0,_id:?1,deleted:false}")
    MasterAsset findByIdAndCountryId(Long countryId, BigInteger id);

    @Query("{countryId:?0,_id:{$in:?1},deleted:false}")
    List<MasterAsset> findMasterAssetByCountryIdAndIds(Long countryId, Set<BigInteger> assetIds);

    @Query(value = "{countryId:?0,assetType:?1,deleted:false}",fields = "{_id:0,name:1}")
    List<MasterAsset> findAllByCountryIdAndAssetTypeId(Long countryId, BigInteger assetId);

    MasterAsset findByid(BigInteger id);




}
