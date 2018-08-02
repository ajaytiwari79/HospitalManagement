package com.kairos.persistance.repository.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.AssetType;
import com.kairos.response.dto.common.AssetTypeBasicResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface AssetTypeMongoRepository extends MongoRepository<AssetType,BigInteger>,CustomAssetTypeRepository {




    @Query("{'countryId':?0,_id:?1,deleted:false}")
    AssetType findByIdAndNonDeleted(Long countryId, BigInteger id);

    @Query("{_id:?0,deleted:false}")
    AssetTypeBasicResponseDTO findAssetTypeById( BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<AssetTypeBasicResponseDTO> findAssetTypeListByIds(List<BigInteger> ids);

    AssetType findByid(BigInteger id);


    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypebyIds(Long countryId,List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<AssetType> findAllAssetTypebyIdsAndOrganizationId(Long organizationId,List<BigInteger> ids);


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    AssetType findByOrganizationIdAndId(Long organizationId, BigInteger id);

}
