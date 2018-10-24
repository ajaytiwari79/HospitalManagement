package com.kairos.persistence.repository.master_data.asset_management.hosting_type;

import com.kairos.persistence.model.master_data.default_asset_setting.HostingType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.HostingTypeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Sort;
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
    @Query("{deleted:false,countryId:?0}")
    List<HostingTypeResponseDTO> findAllByCountryId(Long countryId);

    @Query("{deleted:false,countryId:?0}")
    List<HostingTypeResponseDTO> findAllByCountryIdSortByCreatedDate(Long countryId, Sort  sort);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<HostingType> getHostingTypeListByIds(Long countryId, Set<BigInteger> hostingTypeIds);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    HostingType findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    HostingType findByOrganizationIdAndName(Long organizationId,String name);

    @Query("{organizationId:?0,deleted:false}")
    List<HostingTypeResponseDTO> findAllByUnitIdSortByCreatedDate(Long organizationId, Sort sort);

    @Query("{organizationId:?0,deleted:false}")
    List<HostingTypeResponseDTO> findAllByUnitId(Long organizationId);

}



