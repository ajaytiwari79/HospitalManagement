package com.kairos.persistance.repository.master_data.asset_management.storage_format;

import com.kairos.persistance.model.master_data.default_asset_setting.StorageFormat;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.StorageFormatResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface StorageFormatMongoRepository extends MongoBaseRepository<StorageFormat,BigInteger>,CustomStorageFormatRepository {



    @Query("{countryId:?0,_id:?1,deleted:false}")
    StorageFormat findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    StorageFormat findByNameAndCountryId(Long countryId,String name);

    StorageFormat findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<StorageFormatResponseDTO> findAllStorageFormats(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<StorageFormat> getStorageFormatListByIds(Long countryId, Set<BigInteger> storageFormatIds);

    @Query("{_id:{$in:?0},deleted:false}")
    List<StorageFormatResponseDTO> findStorageFormatByIds(List<BigInteger> ids);

    @Query("{organizationId:?0,deleted:false}")
    List<StorageFormatResponseDTO> findAllOrganizationStorageFormats(Long organizationId);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    StorageFormat findByOrganizationIdAndName(Long organizationId,String name);


    @Query("{organizationId:?0,_id:?1,deleted:false}")
    StorageFormat findByOrganizationIdAndId(Long organizationId,BigInteger id);

}
