package com.kairos.persistance.repository.master_data_management.data_category_element;


import com.kairos.persistance.model.master_data_management.data_category_element.DataCategory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DataCategoryMongoRepository extends MongoRepository<DataCategory,BigInteger> ,CustomDataCategoryRepository{

    DataCategory findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataCategory findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,name:?1}")
    DataCategory findByCountryIdAndName(Long countryId,String name);


}
