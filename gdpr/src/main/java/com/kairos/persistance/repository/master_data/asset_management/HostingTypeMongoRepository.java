package com.kairos.persistance.repository.master_data.asset_management;

import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
public interface HostingTypeMongoRepository extends MongoRepository<HostingType,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    HostingType findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    HostingType findByName(Long countryId,Long organizationId,String name);

    HostingType findByid(BigInteger id);

    @Query("{deleted:false,_id:?0}")
    HostingTypeResponseDTO findHostingTypeById(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<HostingType> findAllHostingTypes(Long countryId,Long organizationId);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<HostingType>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}



