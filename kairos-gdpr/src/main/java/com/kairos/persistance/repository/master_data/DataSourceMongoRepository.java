package com.kairos.persistance.repository.master_data;


import com.kairos.persistance.model.master_data.DataSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DataSourceMongoRepository extends MongoRepository<DataSource,BigInteger> {

    DataSource findByid(BigInteger id);
    DataSource findByName(String name);

    @Query("{'_id':{$in:?0}}")
    List<DataSource> dataSourceList(List<BigInteger> dataSourceids);
}
