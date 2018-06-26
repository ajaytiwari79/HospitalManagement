package com.kairos.persistance.repository.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.OrganizationalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface OrganizationalSecurityMeasureMongoRepository extends MongoRepository<OrganizationalSecurityMeasure,BigInteger> {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    OrganizationalSecurityMeasure findByIdAndNonDeleted(Long countryId, Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    OrganizationalSecurityMeasure findByName(Long countryId, Long organizationId,String name);

    OrganizationalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,organizationId:1,countryId:?0}")
    List<OrganizationalSecurityMeasure> findAllOrganizationalSecurityMeasures(Long countryId, Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<OrganizationalSecurityMeasure>  findByCountryAndNameList(Long countryId, Long organizationId,Set<String> name);

}
