package com.kairos.persistance.repository.master_data_management.asset_management;

import com.kairos.persistance.model.master_data_management.asset_management.TechnicalSecurityMeasure;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface TechnicalSecurityMeasureMongoRepository extends MongoRepository<TechnicalSecurityMeasure,BigInteger> {


    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    TechnicalSecurityMeasure findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    TechnicalSecurityMeasure findByNameAndCountryId(Long countryId,Long organizationId,String name);


    TechnicalSecurityMeasure findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1}")
    List<TechnicalSecurityMeasure> findAllTechnicalSecurityMeasures(Long countryId,Long organizationId);


    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<TechnicalSecurityMeasure>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);
}
