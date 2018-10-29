package com.kairos.persistence.repository.master_data.data_category_element;


import com.kairos.persistence.model.master_data.data_category_element.DataCategory;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
@JaversSpringDataAuditable
public interface DataCategoryMongoRepository extends MongoBaseRepository<DataCategory,BigInteger>,CustomDataCategoryRepository{

    DataCategory findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataCategory findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataCategory findByCountryIdAndName(Long countryId,Long organizationId,String name);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<DataCategory> findDataCategoryByIds(Long countryId,Set<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataCategory findByUnitIdAndId(Long unitId,BigInteger id);




}
