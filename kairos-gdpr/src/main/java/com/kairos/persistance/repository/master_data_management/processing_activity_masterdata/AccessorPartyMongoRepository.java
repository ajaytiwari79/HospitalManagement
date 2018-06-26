package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;

import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.AccessorParty;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;


@Repository
public interface AccessorPartyMongoRepository extends MongoRepository<AccessorParty,BigInteger> {





    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    AccessorParty findByIdAndNonDeleted(Long countryId,Long organizationId, BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    AccessorParty findByName(Long countryId,Long organizationId,String name);

    @Query("{_id:{$in:?0}}")
    List<AccessorParty> AccessorPartyList(List<BigInteger> ids);

    AccessorParty findByid(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<AccessorParty> findAllAccessorPartys(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<AccessorParty>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);

}
