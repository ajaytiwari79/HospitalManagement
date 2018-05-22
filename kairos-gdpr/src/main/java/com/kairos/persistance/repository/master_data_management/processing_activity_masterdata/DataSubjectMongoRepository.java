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

    @Query("{'_id':?0,deleted:false}")
    DataSubject findByIdAndNonDeleted(BigInteger id);

    @Query("{'name':?0,deleted:false}")
    DataSubject findByName(String name);

    @Query("{'_id':{$in:?0}}")
    List<DataSubject> getDataSubjectList(List<BigInteger> ids);



    @Query("{deleted:false}")
    List<DataSubject> findAllDataSubjects();
}
