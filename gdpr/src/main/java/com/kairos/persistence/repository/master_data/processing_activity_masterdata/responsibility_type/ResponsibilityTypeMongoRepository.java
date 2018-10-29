package com.kairos.persistence.repository.master_data.processing_activity_masterdata.responsibility_type;


import com.kairos.persistence.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ResponsibilityTypeMongoRepository extends MongoBaseRepository<ResponsibilityType,BigInteger>,CustomResponsibilityTypeRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    ResponsibilityType findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    ResponsibilityType findByName(Long countryId,String name);

    @Query("{_id:?0,deleted:false}")
    ResponsibilityTypeResponseDTO findResponsibilityTypeByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<ResponsibilityType> getResponsibilityTypeListByIds(Long countryId, Set<BigInteger> responsibilityTypeIds);

    ResponsibilityType findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<ResponsibilityTypeResponseDTO> findAllByCountryId(Long countryId);

    @Query("{deleted:false,countryId:?0}")
    List<ResponsibilityTypeResponseDTO> findAllByCountryIdSortByCreatedDate(Long countryId, Sort sort);

    @Query("{organizationId:?0,deleted:false}")
    List<ResponsibilityTypeResponseDTO> findAllByUnitIdSortByCreatedDate(Long unitId, Sort sort);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ResponsibilityType findByUnitIdAndId(Long unitId, BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    ResponsibilityType findByUnitIdAndName(Long unitId, String name);

    @Query("{organizationId:?0,deleted:false}")
    List<ResponsibilityTypeResponseDTO> findAllByUnitId(Long unitId);


}
