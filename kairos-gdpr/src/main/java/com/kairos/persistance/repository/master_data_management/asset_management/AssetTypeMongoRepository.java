package com.kairos.persistance.repository.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.AssetType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface AssetTypeMongoRepository extends MongoRepository<AssetType,BigInteger>,CustomStorageTypeRepository {




    @Query("{'countryId':?0,_id:?1,deleted:false}")
    AssetType findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{countryId:?0,nameInLowerCase:?1,deleted:false}")
    AssetType findByName(Long countryId, String name);

    AssetType findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<AssetType> findAllAssetTypes(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypesbyIds(Long countryId,List<BigInteger> ids);


    @Query("{countryId:?0,nameInLowerCase:{$in:?1},deleted:false}")
    List<AssetType>  findByCountryAndNameList(Long countryId, Set<String> name);
}
