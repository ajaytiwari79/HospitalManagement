package com.kairos.persistance.repository.master_data.asset_management.hosting_type;

import com.kairos.persistance.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JaversSpringDataAuditable
public interface HostingTypeMongoRepository extends MongoBaseRepository<HostingType,BigInteger>,CustomHostingTypeRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    HostingType findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    HostingType findByName(Long countryId,String name);

    HostingType findByid(BigInteger id);

    @Query("{deleted:false,_id:?0}")
    HostingTypeResponseDTO findHostingTypeById(BigInteger id);

    @Query("{countryId:?0,deleted:false}")
    List<HostingType> findAllHostingTypes(Long countryId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    HostingType findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    HostingType findByOrganizationIdAndName(Long organizationId,String name);

    @Query("{organizationId:?0,deleted:false}")
    List<HostingTypeResponseDTO> findAllOrganizationHostingTypes(Long organizationId);

}



