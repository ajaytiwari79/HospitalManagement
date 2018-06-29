package com.kairos.persistance.repository.master_data_management.data_category_element;


import com.kairos.persistance.model.master_data_management.data_category_element.DataCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Repository
public interface DataCategoryMongoRepository extends MongoRepository<DataCategory,BigInteger> ,CustomDataCategoryRepository{

    DataCategory findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    DataCategory findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataCategory findByCountryIdAndName(Long countryId,Long organizationId,String name);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<DataCategory> findDataCategoryByIds(Long countryId,Long organizationId,Set<BigInteger> ids);


}
