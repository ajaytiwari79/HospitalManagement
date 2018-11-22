package com.kairos.persistence.repository.master_data.asset_management;


import com.kairos.persistence.model.master_data.default_asset_setting.AssetType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface AssetTypeMongoRepository extends MongoBaseRepository<AssetType,BigInteger>,CustomAssetTypeRepository {




    @Query("{'countryId':?0,_id:?1,deleted:false}")
    AssetType findByCountryIdAndId(Long countryId, BigInteger assetTypeId);

    @Query("{_id:?0,deleted:false}")
    AssetTypeBasicResponseDTO findAssetTypeById( BigInteger assetTypeId);

    @Query("{_id:{$in:?0},deleted:false}")
    List<AssetTypeBasicResponseDTO> findAssetTypeListByIds(List<BigInteger> ids);

    AssetType findByid(BigInteger id);


    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypeByCountryIdAndIds(Long countryId, List<BigInteger> ids);

    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<AssetType> findAllByUnitIdAndIds(Long unitId, Set<BigInteger> ids);

    @Query("{deleted:false,organizationId:?0,_id:{$in:?1},subAssetType:true}")
    List<AssetType> findAllAssetSubTypeByUnitIdAndIds(Long unitId, Set<BigInteger> ids);


    @Query("{organizationId:?0,_id:?1,deleted:false,subAssetType:false}")
    AssetType findByIdAndUnitId(Long organizationId, BigInteger assetTypeId);


    @Query("{organizationId:?0,_id:?1,deleted:false,subAssetType:true}")
    AssetType findSubAssetTypeByIdAndUnitId(Long organizationId, BigInteger assetTypeId);

}
