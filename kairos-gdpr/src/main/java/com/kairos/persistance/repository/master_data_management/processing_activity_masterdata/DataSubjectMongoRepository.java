package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSubject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataSubjectMongoRepository extends MongoRepository<DataSubject,BigInteger> {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    DataSubject findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    DataSubject findByName(Long countryId,String name);

    @Query("{countryId:?0,_id:{$in:?1}}")
    List<DataSubject> getDataSubjectList(Long countryId,List<BigInteger> ids);

    DataSubject findByid(BigInteger id);


    @Query("{'countryId':?0,'deleted':false}")
    List<DataSubject> findAllDataSubjects(Long countryId);
}
