package com.kairos.persistance.repository.master_data_management.data_category_element;


import com.kairos.persistance.model.master_data_management.data_category_element.DataSubjectMapping;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface DataSubjectMappingRepository extends MongoRepository<DataSubjectMapping, BigInteger> ,CustomDataSubjectMappingRepository{


    DataSubjectMapping findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    DataSubjectMapping findByIdAndNonDeleted(Long countryId, BigInteger id);


    @Query("{deleted:false,countryId:?0,name:?1}")
    DataSubjectMapping findByCountryIdAndName(Long countryId,String name);


}
