package com.kairos.persistance.repository.master_data;


import com.kairos.persistance.model.processing_activity_masterdata.DataSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DataSourceMongoRepository extends MongoRepository<DataSource,BigInteger> {

    @Query("{'_id':?0,deleted:false}")
    DataSource findByIdAndNonDeleted(BigInteger id);
    DataSource findByName(String name);

    @Query("{'_id':{$in:?0}}")
    List<DataSource> dataSourceList(List<BigInteger> dataSourceids);


    @Query("{deleted:false}")
    List<DataSource> findAllDataSources();

}
