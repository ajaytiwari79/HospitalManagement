package com.kairos.persistance.repository.master_data;


import com.kairos.persistance.model.master_data.DataSource;
import com.kairos.persistance.model.master_data.DataSubject;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DataSubjectMongoRepository extends MongoRepository<DataSubject,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    DataSubject findByIdAndNonDeleted(BigInteger id);
    DataSubject findByName(String name);

    @Query("{'_id':{$in:?0}}")
    List<DataSubject> dataSubjectList(List<BigInteger> dataSubjectIds);



    @Query("{deleted:false}")
    List<DataSubject> findAllDataSubjects();
}
