package com.kairos.persistance.repository.master_data_management.asset_management;


import com.kairos.persistance.model.master_data_management.asset_management.OrganizationalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface OrganizationalSecurityMeasureMongoRepository extends MongoRepository<OrganizationalSecurityMeasure,BigInteger> {


    @Query("{countryId:?0,'_id':?1,deleted:false}")
    OrganizationalSecurityMeasure findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    OrganizationalSecurityMeasure findByName(Long countryId,String name);

    OrganizationalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0}")
    List<OrganizationalSecurityMeasure> findAllOrganizationalSecurityMeasures(Long countryId);

}
