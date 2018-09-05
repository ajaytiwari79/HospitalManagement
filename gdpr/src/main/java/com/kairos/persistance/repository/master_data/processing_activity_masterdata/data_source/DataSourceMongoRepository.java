package com.kairos.persistance.repository.master_data.processing_activity_masterdata.data_source;


import com.kairos.persistance.model.master_data.default_proc_activity_setting.DataSource;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.common.DataSourceResponseDTO;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataSourceMongoRepository extends MongoBaseRepository<DataSource,BigInteger>,CustomDataSourceRepository {

    @Query("{countryId:?0,_id:?1,deleted:false}")
    DataSource findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{countryId:?0,name:?1,deleted:false}")
    DataSource findByNameAndCountryId(Long countryId,String name);

    @Query("{_id:{$in:?0},deleted:false}")
    List<DataSourceResponseDTO> findDataSourceByIds(List<BigInteger> dataSourceIds);

    DataSource findByid(BigInteger id);


    @Query("{deleted:false,countryId:?0}")
    List<DataSourceResponseDTO> findAllDataSources(Long countryId);

      @Query("{organizationId:?0,deleted:false}")
    List<DataSourceResponseDTO> findAllOrganizationDataSources(Long organizationId);

    @Query("{organizationId:?0,_id:?2,deleted:false}")
    DataSource findByOrganizationIdAndId(Long organizationId,BigInteger id);

    @Query("{organizationId:?0,name:?1,deleted:false}")
    DataSource findByNameAndOrganizationId(Long organizationId,String name);

}
