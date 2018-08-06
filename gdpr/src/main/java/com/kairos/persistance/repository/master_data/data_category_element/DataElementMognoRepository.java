package com.kairos.persistance.repository.master_data.data_category_element;

import com.kairos.persistance.model.master_data.data_category_element.DataElement;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@JaversSpringDataAuditable
public interface DataElementMognoRepository extends MongoRepository<DataElement, BigInteger> {

    DataElement findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:?2}")
    DataElement findByIdAndNonDeleted(Long countryId, Long organizationId,BigInteger id);

    @Query("{deleted:false,countryId:?0,organizationId:?1,name:{$in:?2}}")
    List<DataElement> findByCountryIdAndNames(Long countryId, Long organizationId,List<String> names);

    @Query("{deleted:false,countryId:?0,organizationId:1}")
    List<DataElement> getAllDataElement(Long countryId, Long organizationId);

    @Query("{deleted:false,countryId:?0,organizationId:?1,_id:{$in:?2}}")
    List<DataElement> getAllDataElementListByIds(Long countryId, Long organizationId,List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:{$in:?1}}")
    List<DataElement> findAllDataElementByUnitIdAndIdss( Long unitId,List<BigInteger> ids);


    @Query("{deleted:false,organizationId:?0,_id:?1}")
    DataElement findByUnitIdAndId( Long organizationId,BigInteger id);

}
