package com.kairos.persistance.repository.master_data_management.processing_activity_masterdata;


import com.kairos.persistance.model.master_data_management.processing_activity_masterdata.DataSource;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataSourceMongoRepository extends MongoRepository<DataSource,BigInteger> {

    @Query("{countryId:?0,organizationId:?1,_id:?2,deleted:false}")
    DataSource findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{countryId:?0,organizationId:?1,name:?2,deleted:false}")
    DataSource findByNameAndCountryId(Long countryId,Long organizationId,String name);

    @Query("{_id:{$in:?0}}")
    List<DataSource> dataSourceList(List<BigInteger> ids);

    DataSource findByid(BigInteger id);

    @Query("{countryId:?0,organizationId:?1,deleted:false}")
    List<DataSource> findAllDataSources(Long countryId,Long organizationId);

    @Query("{countryId:?0,organizationId:?1,name:{$in:?2},deleted:false}")
    List<DataSource>  findByCountryAndNameList(Long countryId,Long organizationId,Set<String> name);

}
