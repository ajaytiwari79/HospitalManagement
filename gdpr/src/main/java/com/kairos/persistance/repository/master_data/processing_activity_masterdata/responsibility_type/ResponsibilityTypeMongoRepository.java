package com.kairos.persistance.repository.master_data.processing_activity_masterdata.responsibility_type;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.ResponsibilityTypeResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
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
    List<ResponsibilityTypeResponseDTO> findAllResponsibilityTypes(Long countryId);

    @Query("{organizationId:?0,deleted:false}")
    List<ResponsibilityTypeResponseDTO> findAllOrganizationResponsibilityTypes(Long organizationId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    ResponsibilityType findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    ResponsibilityType findByOrganizationIdAndName(Long organizationId,String name);


}
