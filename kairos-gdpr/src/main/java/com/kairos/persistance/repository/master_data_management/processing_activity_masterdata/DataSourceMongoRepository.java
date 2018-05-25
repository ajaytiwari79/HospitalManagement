package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface DataSourceMongoRepository extends MongoRepository<DataSource,BigInteger> {

    @Query("{'countryId':?0,'_id':?0,deleted:false}")
    DataSource findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{'countryId':?0,'name':?1,deleted:false}")
    DataSource findByName(Long countryId,String name);

    @Query("{'_id':{$in:?0}}")
    List<DataSource> dataSourceList(List<BigInteger> ids);

    DataSource findByid(BigInteger id);

    @Query("{'countryId':?0,deleted:false}")
    List<DataSource> findAllDataSources(Long countryId);

}
