package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface StorageFormatMongoRepository extends MongoRepository<StorageFormat,BigInteger> {



    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    StorageFormat findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    StorageFormat findByNameAndCountryId(Long countryId,Long organizationId,String name);

    StorageFormat findByid(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<StorageFormat> findAllStorageFormats(Long countryId,Long organizationId);

    @Query("{_id:{$in:?0},deleted:false}")
    List<StorageFormatResponseDTO> findAllStorageFormatByIds(List<BigInteger> ids);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<StorageFormat>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);

}
