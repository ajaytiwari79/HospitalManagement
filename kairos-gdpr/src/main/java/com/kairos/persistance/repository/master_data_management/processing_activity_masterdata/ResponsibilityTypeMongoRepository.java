package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.ResponsibilityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface ResponsibilityTypeMongoRepository extends MongoRepository<ResponsibilityType,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    ResponsibilityType findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    ResponsibilityType findByName(String name);


    @Query("{'_id':{$in:?0},deleted:false}")
    List<ResponsibilityType> responsibilityTypeList(List<BigInteger> ids);


    @Query("{deleted:false}")
    List<ResponsibilityType> findAllResponsibilityTypes();





}
