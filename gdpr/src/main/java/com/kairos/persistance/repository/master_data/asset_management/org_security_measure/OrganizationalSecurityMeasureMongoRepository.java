package com.kairos.persistance.repository.master_data.asset_management.org_security_measure;


import com.kairos.persistance.model.master_data.default_asset_setting.OrganizationalSecurityMeasure;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.OrganizationalSecurityMeasureResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface OrganizationalSecurityMeasureMongoRepository extends MongoBaseRepository<OrganizationalSecurityMeasure,BigInteger>,CustomOrganizationalSecurityRepository{


    @Query("{countryId:?0,_id:?1,deleted:false}")
    OrganizationalSecurityMeasure findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    OrganizationalSecurityMeasure findByName(Long countryId,String name);

    OrganizationalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<OrganizationalSecurityMeasure> findAllOrganizationalSecurityMeasures(Long countryId);

    @Query("{deleted:false,_id:{$in:?0}}")
    List<OrganizationalSecurityMeasureResponseDTO> findOrganizationalSecurityMeasuresListByIds(List<BigInteger> ids);

    @Query("{deleted:false,organizationId:?0}")
    List<OrganizationalSecurityMeasureResponseDTO> findAllOrgOrganizationalSecurityMeasures( Long organizationId);

    @Query("{organizationId:?0,_id:?1,deleted:false}")
    OrganizationalSecurityMeasure findByOrganizationIdAndId( Long organizationId,BigInteger id);


    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    OrganizationalSecurityMeasure findByOrganizationIdAndName(Long organizationId,String name);

}
