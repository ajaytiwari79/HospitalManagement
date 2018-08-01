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




    @Query("{'countryId':?0,organizationId:?1,_id:?2,deleted:false}")
    AssetType findByIdAndNonDeleted(Long countryId,Long organizationId, BigInteger id);

    @Query("{_id:?0,deleted:false}")
    AssetTypeBasicResponseDTO findAssetTypeById( BigInteger id);

    @Query("{_id:{$in:?0},deleted:false}")
    List<AssetTypeBasicResponseDTO> findAssetTypeListByIds(List<BigInteger> ids);

    AssetType findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<AssetType> findAllAssetTypes(Long countryId,Long organizationId);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<AssetType> findAllAssetTypebyIds(Long countryId,Long organizationId,List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,nameInLowerCase:{$in:?2},deleted:false}")
    List<AssetType>  findByCountryAndNameList(Long countryId,Long organizationId, Set<String> name);
}
