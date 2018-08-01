package com.kairos.persistance.repository.master_data.processing_activity_masterdata;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.ResponsibilityType;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface ResponsibilityTypeMongoRepository extends MongoRepository<ResponsibilityType,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    ResponsibilityType findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    ResponsibilityType findByName(Long countryId,Long organizationId,String name);

    ResponsibilityType findByid(BigInteger id);
    @Query("{_id:{$in:?0},deleted:false}")
    List<ResponsibilityType> responsibilityTypeList(List<BigInteger> ids);


    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<ResponsibilityType> findAllResponsibilityTypes(Long countryId,Long organizationId);



    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<ResponsibilityType>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);



}
