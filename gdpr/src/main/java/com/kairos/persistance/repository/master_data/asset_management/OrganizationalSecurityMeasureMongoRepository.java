package com.kairos.persistance.repository.master_data.asset_management;


import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface OrganizationalSecurityMeasureMongoRepository extends MongoRepository<OrganizationalSecurityMeasure,BigInteger> {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    OrganizationalSecurityMeasure findByIdAndNonDeleted(Long countryId, Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    OrganizationalSecurityMeasure findByName(Long countryId, Long organizationId,String name);

    OrganizationalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,organizationId:?1,countryId:?0}")
    List<OrganizationalSecurityMeasure> findAllOrganizationalSecurityMeasures(Long countryId, Long organizationId);

    @Query("{deleted:false,_id:{$in:?0}}")
    List<OrganizationalSecurityMeasureResponseDTO> findOrganizationalSecurityMeasuresListByIds(List<BigInteger> ids);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<OrganizationalSecurityMeasure>  findByCountryAndNameList(Long countryId, Long organizationId,Set<String> name);

}
