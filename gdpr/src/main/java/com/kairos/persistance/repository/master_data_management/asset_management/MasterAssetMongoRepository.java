package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.MasterAsset;
import com.kairos.response.dto.filter.FilterAttributes;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface MasterAssetMongoRepository extends MongoRepository<MasterAsset,BigInteger> ,CustomMasterAssetRepository{


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    MasterAsset findByIdANdNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<MasterAsset> findAllMasterAssets( Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    MasterAsset findByNameAndCountry(Long countryId,Long organizationId,String name);

    MasterAsset findByid(BigInteger id);




}
