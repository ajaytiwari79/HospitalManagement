package com.kairos.persistance.repository.master_data.data_category_element;


import com.kairos.persistance.model.master_data.data_category_element.DataSubjectMapping;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@JaversSpringDataAuditable
public interface DataSubjectMappingRepository extends MongoRepository<DataSubjectMapping, BigInteger> ,CustomDataSubjectMappingRepository{


    DataSubjectMapping findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    DataSubjectMapping findByIdAndNonDeleted(Long countryId,Long organizationId,BigInteger id);


    @Query("{deleted:false,countryId:?0,organizationId:?1,name:?2}")
    DataSubjectMapping findByCountryIdAndName(Long countryId,Long organizationId,String name);


}
