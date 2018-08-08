package com.kairos.persistance.repository.master_data.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataCategory;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataCategoryMongoRepository extends MongoBaseRepository<DataCategory,BigInteger>,CustomDataCategoryRepository{

    DataCategory findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    DataCategory findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataCategory findByCountryIdAndName(Long countryId,Long organizationId,String name);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<DataCategory> findDataCategoryByIds(Long countryId,Long organizationId,Set<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataCategory findByUnitIdAndId(Long unitId,BigInteger id);



}
