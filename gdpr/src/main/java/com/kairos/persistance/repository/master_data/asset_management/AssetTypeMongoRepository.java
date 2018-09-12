package com.kairos.persistance.repository.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface AssetTypeMongoRepository extends MongoBaseRepository<AssetType,BigInteger>,CustomAssetTypeRepository {




    @Query("{'countryId':?0,_id:?1,deleted:false}")
    AssetType findByIdAndCountryId(Long countryId, BigInteger id);

    @Query("{_id:?0,deleted:false}")
    AssetTypeBasicResponseDTO findAssetTypeById( BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<AssetTypeBasicResponseDTO> findAssetTypeListByIds(List<BigInteger> ids);

    AssetType findByid(BigInteger id);


    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypeByCountryIdAndIds(Long countryId, List<BigInteger> ids);

    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypeByUnitIdAndIds(Long unitId, List<BigInteger> ids);


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    AssetType findByUnitIdAndId(Long organizationId, BigInteger id);


}
